(ns snowbird.render.serialize-results
  (:require [clojure.pprint :refer [pprint]]
            [clojure.java.io :as io]))

(defn specify
  [results {:keys [destination-file]} _]
  (with-open [target (io/writer destination-file)]
    (pprint results target)))


