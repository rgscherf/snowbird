(ns snowbird.input.files-in-dir
  (:require [snowbird.input.file-system :as fs]
            [clojure.spec.alpha :as s]))

(fs/read-config-file)
(fs/file-paths-of-type :apex (:file-search-path (fs/read-config-file)))

(defn specify
  [config]
  (apply concat
    (for [ft (-> config :file-types)]
      (fs/file-paths-of-type ft (-> config :input-args :file-search-path)))))

