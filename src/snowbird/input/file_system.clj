(ns snowbird.input.file-system
  (:import org.apache.commons.io.FileUtils)
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [snowbird.specs.core :as specs]
            [snowbird.utils.core :as utils]
            [clojure.data.xml :as xml]))


(defn read-default-config
  "Get the default config file from resources dir and read it to a map."
  []
  {:post [(s/assert ::specs/config %)]}
  (println "in read-default-config!")
  (-> "snowbird_config.edn" io/resource slurp edn/read-string))

;; filetypes are recorded in config as keywords.
(def filetype->ext
  {:apex ".cls"
   :js ".js"})

(defn file-paths-of-type
  [filetype atpath]
  {:pre [(s/assert ::specs/filetype filetype)]}
  (->> atpath
       io/file
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
  ;; TODO: this fn assumes we're loading xml from the resource directory. need to work with other sources such as loading from JSON
  (println "in pmd-xml->rule-names with path " xml-path)
  (let [in (xml/parse-str (-> xml-path io/resource slurp))]
    (println in)
    (->> in
         (tree-seq #(-> % :content seq)
                   :content)
         (filter #(= (:tag %) :rule))
         (map #(-> % :attrs :ref))
         (map #(string/split % #"/"))
         (map last))))

(defn make-directory
  [name]
  (FileUtils/forceMkdir (io/file name)))

(defn remove-directory
  [name]
  (FileUtils/deleteDirectory (io/file name)))

(defn write-files-to
  [dirname ftype filemap]
  (if (not (-> dirname io/file .isDirectory))
    (make-directory dirname))
  (doall
    (map (fn [[fname fcontents]]
           (let [fpath (str dirname "/" fname (filetype->ext ftype))]
             (spit fpath fcontents)
             fpath))
         filemap)))

(comment
  (let [testfiles (slurp "/Users/rgscherf/projects/snowbird/tech-debt-measurement/classes/Controller_ContactWorkOrderDisplay.cls")]
    (write-files-to "./test-dir" :apex {"Controller_ContactWorkOrderDisplay" testfiles}))

  (remove-directory "./test-dir")
  (make-directory "./test-dir")
  (FileUtils/deleteDirectory (io/file "./test-dir"))
  (.delete (io/file "./test-dir")))
; Not a file:
; jar:file:/Users/rgscherf/.m2/repository/snowbird/snowbird/0.2.0/snowbird-0.2.0.jar!/pmdrules_apex_v1.xml


