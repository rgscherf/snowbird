(ns snowbird.utils.core
  (:require [clojure.core.match :refer [match]]
            [clojure.string :as string]))

(defn name-from-path
  "Get a filename from path. Filename is the last part of the path,
  including extension."
  [path]
  (-> path
      (string/split (match (System/getProperty "os.name")
                           "Mac OS X" #"/"
                           :else #"\\"))
      last))


(defn resolve-symbol
  "Gets the same symbol across multiple namespaces.
  Does not require any namespaces, but returns the named symbol
  (will throw an exception if any ns is not found."
  [sym namespace]
  (do (require namespace)
      (ns-resolve namespace sym)))

