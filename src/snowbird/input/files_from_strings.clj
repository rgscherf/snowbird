(ns snowbird.input.files-from-strings
  (:require [snowbird.input.file-system :as fs]))

(defn specify
  [config {:keys [analysis-path]}]
  (apply concat
    (for [ft (-> config :file-types)]
      (fs/write-files-to analysis-path ft (-> config :input-file-map)))))
