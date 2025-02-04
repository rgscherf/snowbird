(ns snowbird.input.files-in-dir
  (:require [snowbird.input.file-system :as fs]
            [clojure.spec.alpha :as s]))

(defn specify
  "Search a given directory for files to analyze,
  indicated by the :file-search-path opts key. Search path may be relative."
  [config opts]
  (apply concat
         (for [ft (-> config :file-types)]
           (fs/file-paths-of-type ft (:file-search-path opts)))))
