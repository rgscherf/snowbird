(ns snowbird.custom-rules.apex.apex-filename-must-match-pattern
  (:require [snowbird.utils.core :as utils]
            [clojure.string :as string]))

(def rule-name "ApexFilenameMustMatchPattern")


(def name-patterns
  ^:private
  [#"trigger\.cls"
   #"^triggerhandler_"
   #"^controller_"
   #"^controllerext_"
   #"^class_"
   #"^model_"
   #"^batch_"
   #"^scheduled_"
   #"^ltgcontroller_"
   #"^testfactory_"
   #"_test\.cls"
   #"_longtest\.cls"])


(defn- name-pattern-matches
  "Return a seq of allowable name patterns matched by this file."
  [uc-fname]
  (let [ls-fname (string/lower-case uc-fname)]
    (map #(re-find % ls-fname)
         name-patterns)))


(defn- run-single
  [file-path]
  (let [file-name (utils/name-from-path file-path)]
    (when (->> file-name name-pattern-matches (remove nil?) empty?)
        {:line 00
         :description (str "Lowercase filename '"
                           (string/lower-case file-name)
                           "' does not match any allowed pattern: "
                           name-patterns)
         :rule rule-name
         :file-name file-name
         :file-path file-path})))


(defn run
  [file-paths]
  (->> file-paths
       (map run-single)
       (remove nil?)))

