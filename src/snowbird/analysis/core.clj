(ns snowbird.analysis.core
  (:require [snowbird.analysis.pmd :as pmd]
            [snowbird.specs.core :as specs]
            [snowbird.input.file-system :as fs]
            [clojure.spec.alpha :as s]
            [snowbird.utils.core :as utils])
  (:import java.util.Date
           java.util.UUID))

(defn analyze
  [file-paths config]
  {:pre [(s/assert (s/coll-of ::specs/file-path) file-paths)
         (s/assert ::specs/config config)]
   :post [(s/assert ::specs/analysis-result %)]}
  (let [analysis-date (Date.)
        id (UUID/randomUUID)]
    {:analysis-time analysis-date
     :id            id
     :config        config
     :results       (apply merge
                           (for [t (:file-types config)]
                             {t {:files-examined
                                   (map utils/name-from-path file-paths)
                                 :rules
                                   (fs/pmd-xml->rule-names (-> config
                                                               :pmd-rules
                                                               t))
                                 :violations
                                   (map #(assoc % :analysis-id id)
                                        (pmd/violation-seq file-paths
                                                           t
                                                           config))}}))}))
