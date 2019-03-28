(ns snowbird.core
  (:require [clojure.spec.alpha :as s]
            [snowbird.specs.core :as specs]
            [expound.alpha :as expound]
            [snowbird.utils.core :as utils]
            [snowbird.input.file-system :as fs]
            [snowbird.input.cli :as cli]
            [snowbird.analysis.core :as analysis]))

(s/check-asserts true)
(set! s/*explain-out* expound/printer)

(defn run-inputs
  [config]
  (reduce (fn [acc [f-ns opts]]
            (let [input-fn (utils/resolve-symbol 'specify f-ns)]
              (concat acc (input-fn config opts))))
          []
          (:input config)))

(defn run-renders
  [analysis-result config]
  (reduce (fn [acc [f-ns opts]]
            (let [render-fn (utils/resolve-symbol 'specify f-ns)]
              (merge acc (render-fn analysis-result opts acc))))
          {}
          (:render config)))

(defn run-snowbird
  [config]
  {:pre [(s/assert ::specs/config config)]}
  (let [result (analysis/analyze (run-inputs config) config)]
    (run-renders result config)))

(defn -main
  [& _]
  (run-snowbird (fs/read-config-file)))

#_(-main)
