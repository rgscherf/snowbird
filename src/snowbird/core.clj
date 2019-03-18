(ns snowbird.core
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [snowbird.input.file-system :as fs]
            [snowbird.input.cli :as cli]
            [snowbird.analysis.pmd :as pmd]))

(s/check-asserts true)
(set! s/*explain-out* expound/printer)

#_(defn tech-debt-ratio
    "Calculate the technical debt ratio. Keys of the file-map are filenames. If there are violations for a key, val will be not-nil.
  Therefore, to find files-with-violations, remove any val that is nil."
    [file-map]
    (let [total-files (-> file-map keys count)
          files-with-violations (->> file-map vals (remove nil?) count)]
      (double (/ files-with-violations total-files))))

;; get files being operated upon:w

#_(defn violations->merged
    [file-type path violations]
    (into (sorted-map)
          (merge (fs/base-filemap-for-filetype file-type path)
                 violations)))

#_(defn analyze-filetype
    [filetype config]
    (->> (pmd/violation-map filetype config)
         (violations->merged filetype (:file-search-path config))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TODO: IMPLEMENT HIGH LEVEL PLAN
(def cfg (fs/read-config-file))
#_(def out
    {:apex {:files-searched (fs/files-of-type :apex (:file-search-path cfg))
            :violations (pmd/violation-seq :apex cfg)}})

;; for filetype in config-filetypes...
;; construct filetype map as above
;; TODO: (currently missing list of rules parsed in top level map ...)
;; then merge these into a combined map which has top level keys:
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:analysis-id "a UUID"
 :date "a DATE"
 :analysis {:analysis :maps}
 :config-used {:copy-of :config-file}}

;; then send this top-level to the config file's render method.
;; and the database?



#_(defn -main
    [& args]
    (let [cli-opts (cli/config-from-cli-args args)
          file-opts (fs/read-config-file)
          merged-opts (merge file-opts cli-opts)]
      (reduce (fn [acc f]
                (let [analysis (analyze-filetype f merged-opts)]
                  (println analysis)
                  (-> acc
                      (assoc-in [f :files] analysis)
                      (assoc-in [f :tech-debt-ratio] (tech-debt-ratio analysis)))))
              {}
              (:file-types merged-opts))))



