(ns snowbird.core
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [snowbird.file-system :as fs]
            [snowbird.pmd :as pmd]))

(s/check-asserts true)
(set! s/*explain-out* expound/printer)

;; dev stuff
(def config (fs/read-config-file))
(def out (pmd/run-pmd :apex config))

(defn tech-debt-ratio
  "Calculate the technical debt ratio. Keys of the file-map are filenames. If there are violations for a key, val will be not-nil.
  Therefore, to find files-with-violations, remove any val that is nil."
  [file-map]
  (let [total-files (-> file-map keys count)
        files-with-violations (->> file-map vals (remove nil?) count)]
    (double (/ files-with-violations total-files))))

;; get files being operated upon:w

(defn violations->merged
  [file-type path violations]
  (into (sorted-map)
        (merge (fs/base-filemap-for-filetype file-type path)
               violations)))

(->> out
     pmd/violation-map*
     (violations->merged :apex (:file-search-path config))
     tech-debt-ratio)



