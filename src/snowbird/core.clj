(ns snowbird.core
  (:require [clojure.spec.alpha :as s]
            [snowbird.specs.core :as specs]
            [expound.alpha :as expound]
            [snowbird.input.file-system :as fs]
            [snowbird.input.cli :as cli]
            [snowbird.analysis.core :as analysis]))

(s/check-asserts true)
(set! s/*explain-out* expound/printer)

(defn- inst
  [specification cfg]
  (-> cfg specification :instruction))

(defn run-snowbird
  [config]
  {:pre [(s/assert ::specs/config config)]}
  (do
    (require (inst :input config) (inst :render config))
    (let [input-fn (ns-resolve (inst :input config) 'specify)
          render-fn (ns-resolve (inst :render config) 'specify)
          result (analysis/analyze (input-fn config) config)]
      (render-fn result))))

(defn -main
  [& _]
  (run-snowbird (fs/read-config-file)))
