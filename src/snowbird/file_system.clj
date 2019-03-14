(ns snowbird.file-system
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.java.io :as jio]
            [clojure.edn :as edn]
            [snowbird.specs :as specs]
            [snowbird.utils :as utils]))


(defn read-config-file
  []
  {:post [(s/assert ::specs/config %)]}
  (let [config (-> "snowbird_config.edn" slurp edn/read-string)]
    config))


;; filetypes are recorded in config as keywords.
(def filetype->ext
  {:apex ".cls"
   :js ".js"})

(defn files-of-type
  [filetype atpath]
  {:pre [(s/assert ::specs/filetype filetype)]}
  (->> atpath
       jio/file
       file-seq
       (filter #(.isFile %))
       (filter #(string/ends-with? % (get filetype->ext filetype)))
       (map #(.getCanonicalPath %))
       (map utils/name-from-path)))


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

