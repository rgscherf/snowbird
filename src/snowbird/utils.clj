(ns snowbird.utils
  (:require [clojure.core.match :refer [match]]
            [clojure.string :as string]))

(defn name-from-path
  "Get a filename from path. Filename is the last part of the path, including extension."
  [path]
  (-> path
      (string/split (match (System/getProperty "os.name")
                           "Mac OS X" #"/"
                           :else #"\\"))
      last))
