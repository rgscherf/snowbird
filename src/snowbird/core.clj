(ns snowbird.core
  (:gen-class)
  (:require [clojure.spec.alpha :as s]
            [snowbird.specs.core :as specs]
            [expound.alpha :as expound]
            [snowbird.utils.core :as utils]
            [snowbird.input.file-system :as fs]
            [clojure.data.json :as json]
            [snowbird.analysis.core :as analysis]))

(s/check-asserts true)
;(set! s/*explain-out* expound/printer)

(defn -main
  [& _]
  (println "please use Snowbird as a library!"))

(defn run-inputs
  [config]
  (reduce (fn [acc [f-ns opts]]
            (let [input-fn (utils/resolve-symbol 'specify f-ns)]
              (concat acc (input-fn config opts))))
          []
          (:input config)))

(defn run-renders
  [analysis-result config]
  (println (:render config))
  (reduce (fn [acc [f-ns opts]]
            (let [render-fn (utils/resolve-symbol 'specify f-ns)]
              (merge acc (render-fn analysis-result opts acc))))
          {}
          (:render config)))

(defn analysis-result
  [config]
  (analysis/analyze (run-inputs config) config))

(defn run-snowbird
  [config]
  {:pre [(s/assert ::specs/config config)]}
  (run-renders (analysis-result config) config))


(defn run-snowbird-from-json
  "Run Snowbird from a filemap of {file-name-str => file-contents-str}"
  [config filemap]
  (let [rendered (run-snowbird (assoc config
                                :input-file-map
                                filemap))]
    (fs/remove-directory "./analysis-tmp") ; TODO: do not hardcode this value.
    rendered))

(defn run-snowbird-from-filemap
  [filemap]
  (run-snowbird-from-json (fs/read-default-config) filemap))


(comment
  (let [testfiles (slurp "/Users/rgscherf/projects/snowbird/tech-debt-measurement/classes/Controller_ContactWorkOrderDisplay.cls")]
      (run-snowbird-from-filemap {"Controller_ContactWorkOrderDisplay" testfiles})))

