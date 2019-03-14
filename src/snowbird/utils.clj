(ns snowbird.utils
  (:require [clojure.string :as string]))

(defn name-from-path
  "Get a filename from path. Filename is the last part of the path, including extension."
  [path]
  (-> path
      (string/split #"\\")
      last))

