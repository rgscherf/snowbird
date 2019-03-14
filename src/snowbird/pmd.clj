(ns snowbird.pmd
  (:require [clojure.java.shell :refer [sh]]
            [clojure.string :as string]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

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
  [csv-str]
  (-> csv-str csv/read-csv csv-data->maps))


(defn run-pmd
  "Run PMD for a given directory and ruleset(s)
  "
  [{:keys [file-search-path apex-rules]}]
  (-> (run-cmd file-search-path apex-rules) :out as-maps))

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

