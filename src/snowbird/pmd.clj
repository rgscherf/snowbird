(ns snowbird.pmd
  (:require [clojure.java.shell :refer [sh]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [snowbird.specs :as specs]
            [snowbird.utils :as utils]))

(defn- pmd-path
  []
  (-> "pmd/bin/pmd.bat"
      io/resource
      .getPath
      (string/replace-first "/" "")))

(defn- csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map #(string/replace % " " "-"))
            (map string/lower-case)
            (map keyword) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn- run-cmd
  "Run the PMD binary bundled with this program."
  [path rules]
  ;; https://stackoverflow.com/questions/6734908/how-to-execute-system-commands
  (sh "cmd"
      "/C"
      (pmd-path)
      "-R" rules
      "-f" "csv"
      "-d" path))

(defn- as-maps
  "Read a CSV string as a seq of maps keyed to the first row values."
  [csv-str]
  (-> csv-str csv/read-csv csv-data->maps))

(defn run-pmd
  "Run PMD for a given directory and ruleset(s) "
  [file-type {:keys [pmd-rules file-search-path]}]
  (->> (get pmd-rules file-type)
       (run-cmd file-search-path)
       :out
       as-maps))

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

(defn violation-map*
  "Given a CSV (seq of violation-maps), create a map of {:file => {:violated-rule [violation-maps]}
  File names are string keys, violated rules are kw keys."
  [csv-output]
  {:post [(s/assert ::specs/violation-map %)]}
  (let [transposed-by-file (transpose-csv-by :file csv-output :keyfn utils/name-from-path)]
    (reduce-kv (fn [m k v]
                 (assoc m k (transpose-csv-by :rule v :keyfn keyword)))
               {}
               transposed-by-file)))

(defn violation-map
  "Given a config file and filetype, run PMD for that filetype and return a seq of violation-maps."
  [filetype config]
  (violation-map* (run-pmd filetype config)))

