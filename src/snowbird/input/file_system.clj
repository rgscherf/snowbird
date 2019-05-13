(ns snowbird.input.file-system
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.java.io :as jio]
            [clojure.edn :as edn]
            [snowbird.specs.core :as specs]
            [snowbird.utils.core :as utils]
            [clojure.data.xml :as xml]))


(defn read-default-config
  "Get the default config file from resources dir and read it to a map."
  []
  {:post [(s/assert ::specs/config %)]}
  (-> "snowbird_config.edn" jio/resource slurp edn/read-string))

;; filetypes are recorded in config as keywords.
(def filetype->ext
  {:apex ".cls"
   :js ".js"})

(defn file-paths-of-type
  [filetype atpath]
  {:pre [(s/assert ::specs/filetype filetype)]}
  (->> atpath
       jio/file
       file-seq
       (filter #(.isFile %))
       (filter #(string/ends-with? % (get filetype->ext filetype)))
       (map #(.getCanonicalPath %))))

(defn file-names-of-type
  [filetype atpath]
  {:pre [(s/assert ::specs/filetype filetype)]}
  (map utils/name-from-path
       (file-paths-of-type filetype atpath)))


(defn base-filemap-for-filetype
  "Get all the files for a given path and filetype, and put them into a map of
  filename => nil. This map will be merged with the violations map."
  [filetype path]
  {:pre [(s/assert ::specs/filetype filetype)]}
  (reduce
    (fn [acc x]
      (assoc acc x nil))
    {}
    (file-names-of-type filetype path)))


(defn pmd-xml->rule-names
  [xml-path]
  (let [in (xml/parse-str (-> xml-path jio/resource slurp))]
    (->> in
         (tree-seq #(-> % :content seq)
                   :content)
         (filter #(= (:tag %) :rule))
         (map #(-> % :attrs :ref))
         (map #(string/split % #"/"))
         (map last))))
