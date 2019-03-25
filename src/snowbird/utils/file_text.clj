(ns snowbird.utils.file-text
  (:require [clojure.string :as string]))

(def doc-comment-regex #"(?s)/\*\*(.*?)\*/")
(def multiline-comment-regex #"(?s)/\*(.*?)\*/")
(def singleline-comment-regex #"(?m)//.*$")


(defn strip-comments
  "Strip single and multiline comments from a string."
  [file-str]
  (-> file-str
      (string/replace multiline-comment-regex "")
      (string/replace singleline-comment-regex "")))


(defn insert-line-num-tags
  "Add line number tags to file, such that each line reads:
  {{linenum}} ...line contents. There is a space after the second closing %."
  [file-str]
  (string/join "\n"
               (map-indexed #(str "%%" (inc %1) "%% " %2)
                            (string/split-lines file-str))))


(defn- break-single-numbered-line
  "Break a number-tagged line into vector of [line-number line-text]."
  [ln]
  (let [[_ ln-num-str ln-txt](re-find #"%%([0-9]+)%%(.*)" ln)]
    [(Integer/parseInt ln-num-str) ln-txt]))


(defn break-numbered-lines
  "Break a multiline string of number-tagged lines
  into a seq of vectors of [line-number line-text]."
  [file-str]
  (map break-single-numbered-line
       (string/split-lines file-str)))

