(ns snowbird.custom-rules.apex.no-dml-in-controller
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [snowbird.utils.file-text :as ft]
            [snowbird.utils.core :as utils]))

(def dml-regex #"\s+(insert|delete|upsert|update)\s+")
(def allowed-file-pattern-regex #"(model_|test\.cls|testfactory\.cls)")
(def rule-name "NoDmlInController")

(defn- run-single
  [file-path]
  (->> file-path
       slurp
       ft/insert-line-num-tags
       ft/strip-comments
       ft/break-numbered-lines
       (map (fn [[line-num line-txt]] {:line line-num
                                       :file-path file-path
                                       :file-name (utils/name-from-path file-path)
                                       :rule rule-name
                                       :description (str " " (string/trim line-txt) " ")}))
                                       ;; ^^ so that we still catch DML as first token
       (filter #(re-find dml-regex (:description %)))))


(defn run
  [file-paths]
  (->> file-paths
       (remove #(re-find allowed-file-pattern-regex (string/lower-case %)))
       (map run-single)
       (remove nil?)
       flatten))
