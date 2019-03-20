(ns snowbird.analysis.custom-rules.no-dml-in-controller
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [snowbird.utils.core :as utils]))

(def dml-regex #"\s+(insert|delete|upsert|update)\s+")
(def allowed-file-pattern-regex #"(model_|test\.cls|testfactory\.cls)")
(def rule-name "NoDmlInController")

(defn- run-single
  [file-path]
  (with-open [f (io/reader file-path)]
    (-> (->> (line-seq f)
             (map (fn [n l] {:line n
                             :file-path file-path
                             :file-name (utils/name-from-path file-path)
                             :rule rule-name
                             :description (str " " (string/trim l) " ")})
                             ;; ^^ so that we still catch DML as first token
                  (range 5000))
             (filter #(re-find dml-regex (:description %))))
        doall
        seq)))

(defn run
  [file-paths]
  (->> file-paths
       (remove #(re-find allowed-file-pattern-regex (string/lower-case %)))
       (map run-single)
       (remove nil?)))


