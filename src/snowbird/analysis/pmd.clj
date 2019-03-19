(ns snowbird.analysis.pmd
  (:require [clojure.java.shell :refer [sh]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.core.match :refer [match]]
            [snowbird.specs.core :as specs]
            [snowbird.utils.core :as utils]
            [clojure.java.io :as jio]))


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
  [file-paths rules]
  ;; https://stackoverflow.com/questions/6734908/how-to-execute-system-commands
  (let [_ (spit "temp.txt" (string/join "," file-paths))
        res (apply sh
                   (concat (pmd-bin-path)
                           ["-R" rules
                            "-f" "csv"
                            "-filelist" "temp.txt"]))]
    (do (io/delete-file "temp.txt")
        res)))

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
  [file-paths file-type {:keys [pmd-rules]}]
  (->> (get pmd-rules file-type)
       (run-cmd file-paths)
       :out
       stdin-csv->csv-maps))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; RUNNING PMD AND CLEANING OUTPUT

(defn violation-seq
  "Run PMD and return a seq of spec-conforming violation maps."
  [file-paths filetype config]
  {:pre [#(s/assert ::specs/config config)
         #(s/assert ::specs/filetype filetype)]
   :post [#(s/assert (s/coll-of ::specs/violation-map) %)]}
  (map #(-> %
            (assoc :file-name (utils/name-from-path (:file %)))
            (assoc :file-path (:file %))
            (dissoc :file)
            (dissoc :package))
       (run-pmd file-paths filetype config)))

