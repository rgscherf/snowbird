(ns snowbird.analysis.pmd
  (:require [clojure.java.shell :refer [sh]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.core.match :refer [match]]
            [snowbird.specs.core :as specs]
            [snowbird.utils.core :as utils]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; CMD LINE AND PMD PATH BOOKKEEPING

(defn- windows-embedded-pmd-path
  []
  (-> "pmd/bin/pmd.bat"
      io/resource
      .getPath
      (string/replace-first "/" "")))

(defn- pmd-bin-path
  []
  (match (System/getProperty "os.name")
         "Mac OS X" ["pmd" "pmd"]
         :else ["cmd" "/C" (windows-embedded-pmd-path)]))

(defn- run-cmd
  "Run the PMD binary bundled with this program."
  [path rules]
  ;; https://stackoverflow.com/questions/6734908/how-to-execute-system-commands
  (apply sh
         (concat (pmd-bin-path)
                 ["-R" rules
                  "-f" "csv"
                  "-d" path])))


;;;;;;;;;;;;;;;;;;;;;
;; CSV FILE WRANGLING

(defn- csv-data->maps
  "Key a seq of CSV lines to the values of the first line."
  [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map #(string/replace % " " "-"))
            (map string/lower-case)
            (map keyword) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn- stdin-csv->csv-maps
  "Read a CSV string as a seq of maps keyed to the first row values."
  [csv-str]
  (-> csv-str csv/read-csv csv-data->maps))

(defn run-pmd
  "Run PMD for a given directory and ruleset(s) "
  [file-type {:keys [pmd-rules file-search-path]}]
  (->> (get pmd-rules file-type)
       (run-cmd file-search-path)
       :out
       stdin-csv->csv-maps))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; MAKING VIOLATION SEQS INTO MAPS

(defn transpose-csv-by
  "Transpose a CSV by some key. A CSV is a sequence of maps.
  Result is a map of transposition-key to all rows matching that key.
  Optional arg `keyfn` will be applied to the resultant key."
  [transposition-key input-map & {:keys [keyfn] :or {keyfn identity}}]
  (reduce (fn [acc-map input-row]
            (update acc-map
                    (keyfn (get input-row transposition-key))
                    conj
                    input-row))
          {}
          input-map))

(defn transpose-to-violation-map
  "Given a CSV (seq of violation-maps), create a map of {:file => {:violated-rule [violation-maps]}
  File names are string keys, violated rules are kw keys."
  [csv-output]
  {:post [(s/assert ::specs/violation-map %)]}
  (let [transposed-by-file (transpose-csv-by :file-name csv-output)]
    (reduce-kv (fn [m k v]
                 (assoc m k (transpose-csv-by :rule v :keyfn keyword)))
               {}
               transposed-by-file)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; RUNNING PMD AND CLEANING OUTPUT

(defn violation-seq
  "Run PMD and return a seq of spec-conforming violation maps."
  [filetype config]
  {:pre [#(s/assert ::specs/config config)
         #(s/assert ::specs/filetype filetype)]
   :post [#(s/assert (s/coll-of ::specs/violation-map) %)]}
  (map #(-> %
            (assoc :file-name (utils/name-from-path (:file %)))
            (assoc :file-path (:file %))
            (dissoc :file)
            (dissoc :package))
       (run-pmd filetype config)))

