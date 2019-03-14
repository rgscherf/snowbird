(ns snowbird.core
  (:require [clojure.string :as string]
            [clojure.java.io :as jio]
            [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [snowbird.specs :as specs]
            [snowbird.pmd :as pmd]))

(s/check-asserts true)
(set! s/*explain-out* expound/printer)

(def path "../tech-debt-measurement")

(defn name-from-path
  "Get a filename from path. Filename is the last part of the path, including extension."
  [path]
  (-> path
      (string/split #"\\")
      last))

(defn violation-map
  "Given a CSV (seq of violation-maps), create a map of {:file => {:violated-rule [violation-maps]}
  File names are string keys, violated rules are kw keys."
  [csv-output]
  {:post [(s/assert ::specs/violation-map %)]}
  (let [transposed-by-file (pmd/transpose-csv-by :file csv-output :keyfn name-from-path)]
    (reduce-kv (fn [m k v]
                 (assoc m k (pmd/transpose-csv-by :rule v :keyfn keyword)))
               {}
               transposed-by-file)))

(defn files-of-type
  [filext atpath]
  {:pre [(s/assert ::specs/filetype filext)]}
  (->> atpath
       jio/file
       file-seq
       (filter #(.isFile %))
       (filter #(string/ends-with? % filext))
       (map #(.getCanonicalPath %))
       (map name-from-path)))


(defn tech-debt-ratio
  "only files with violations will appear in the violations map.
  Therefore, we only need to (count keys) the violations map for # files with violations."
  [file-type path violation-map]
  (let [num-files (count (files-of-type file-type path))
        num-violations (count (keys violation-map))]
    (double (/ num-violations num-files))))


;; get files being operated upon:w

(defn base-filemap-for-filetype
  "Get all the files for a given path and filetype, and put them into a map of
  filename => nil. This map will be merged with the violations map."
  [filetype path]
  {:pre [(s/assert ::specs/filetype filetype)]}
  (reduce
    (fn [acc x]
      (assoc acc x nil))
    {}
    (files-of-type filetype path)))

(defn read-config-file
  []
  {:post [(s/assert ::specs/config %)]}
  (let [config (-> "snowbird_config.edn" slurp edn/read-string)]
    config))

(read-config-file)

