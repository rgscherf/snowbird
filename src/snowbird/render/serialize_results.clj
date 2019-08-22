(ns snowbird.render.serialize-results
  (:require [clojure.pprint :refer [pprint]]
            [clojure.java.io :as io]))

(defn write-to-file
  [results destination-file]
  (with-open [target (io/writer destination-file)]
    (pprint results target)))

(defn specify
  [results {:keys [as destination-file]} _]
  (let [render-instructions (into #{} (if (coll? as) as [as]))]
    render-instructions
    (merge {}
           (if (contains? render-instructions :data)
               {::results results})
           (if (contains? render-instructions :file)
               (do (write-to-file results destination-file)
                   {::results-file destination-file})))))





