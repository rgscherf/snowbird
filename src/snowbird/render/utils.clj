(ns snowbird.render.utils
  (:require [snowbird.specs.core :as specs]
            [clojure.spec.alpha :as s]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; MAKING VIOLATION SEQS INTO MAPS

(defn transpose-csv-by
  "Transpose a CSV by some key. A CSV is a sequence of maps.
  Result is a map of transposition-key to all rows matching that key.
  Optional arg `keyfn` will be applied to the resultant key."
  [transposition-key input-map & {:keys [keyfn] :or {keyfn identity}}]
  (reduce (fn [acc-map input-row]
            (update acc-map
                    (keyfn (get input-row transposition-key))
                    conj
                    input-row))
          {}
          input-map))

(defn transpose-to-violation-map
  "Given a CSV (seq of violation-maps), create a map of {:file => {:violated-rule [violation-maps]}
  File names are string keys, violated rules are kw keys."
  [csv-output]
  {:post [(s/assert ::specs/violation-map %)]}
  (let [transposed-by-file (transpose-csv-by :file-name csv-output)]
    (reduce-kv (fn [m k v]
                 (assoc m k (transpose-csv-by :rule v :keyfn keyword)))
               {}
               transposed-by-file)))


