(ns snowbird.analysis.core
  (:require [snowbird.analysis.pmd :as pmd]
            [snowbird.specs.core :as specs]
            [snowbird.input.file-system :as fs]
            [clojure.spec.alpha :as s])
  (:import java.util.Date
           java.util.UUID))

(defn analyze
  [config]
  {:pre [(s/assert ::specs/config config)]
   :post [(s/assert ::specs/analysis-result %)]}
  (let [analysis-date (Date.)
        id (UUID/randomUUID)]
    {:analysis-time analysis-date
     :id id
     :config config
     :analyses (apply merge
                (for [t (:file-types config)]
                  {t {:files-examined (fs/files-of-type t (:file-search-path config))
                      :rules          (fs/pmd-xml->rule-names (-> config :pmd-rules t))
                      :violations     (map #(assoc % :analysis-id id)
                                           (pmd/violation-seq t config))}}))}))


(comment
  (analyze (fs/read-config-file)))