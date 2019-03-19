(ns snowbird.input.files-in-dir
  (:require [snowbird.input.file-system :as fs]
            [clojure.spec.alpha :as s]))

(fs/read-config-file)
(fs/file-paths-of-type :apex (:file-search-path (fs/read-config-file)))

(defn specify
  "Search a given directory for files to analyze,
  indicated by the :file-search-path opts key. Search path may be relative."
  [config]
  (apply concat
    (for [ft (-> config :file-types)]
      (fs/file-paths-of-type ft (-> config :input-opts :file-search-path)))))

