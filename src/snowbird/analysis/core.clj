(ns snowbird.analysis.core
  (:require [snowbird.analysis.pmd :as pmd]
            [snowbird.input.file-system :as fs])
  (:import java.util.Date
           java.util.UUID))

(defn analyze
  [config]
  (let [analysis-date (Date.)
        id (UUID/randomUUID)]
    {:analysis-date analysis-date
     :id id
     :config config
     :file-types (apply merge
                  (for [t (:file-types config)]
                    {t {:files-examined (fs/files-of-type t (:file-search-path config))
                        :rules          (fs/pmd-xml->rule-names (-> config :pmd-rules t))
                        :violations     (map #(assoc % :analysis-id id)
                                             (pmd/violation-seq t config))}}))}))

