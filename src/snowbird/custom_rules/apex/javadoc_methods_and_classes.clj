(ns snowbird.custom-rules.apex.javadoc-methods-and-classes
  (:require [instaparse.core :as insta]
            [clojure.string :as string]))

;; RUN will take, in its options map, a vector of tags that must exist in documentation comments.

(def testclass (slurp "./testclass.cls"))
(def doc-comment-regex #"(?s)/\*\*(.*?)\*/")
(def whitespace (insta/parser "whitespace = #'\\s+'"))

(defn comment-parser-str
  "Make an instaparse parser for javadoc-style comments, tagging the given fields.
  Any fields not selected will be dropped from the parse.
  Returns a seq of vector pairs, of [:tag str-content]."
  [required-fields]
  (let [field-docparts (string/join " "
                                    (drop-last (interleave required-fields
                                                           (repeat "|"))))
        field-line     (map (fn [s] (str s " = <'" s " '> CONTENT")) required-fields)]
    (string/join "\n"
                 (concat
                   ["<S> = <'/**'> LN+ <'*/'>"
                    "<LN> = <DROPLINE> | DOCPART"
                    "DROPLINE = CONTENT"
                    (str "<DOCPART> = WS* <'|@'> (" field-docparts ")")
                    "<CONTENT> = <WS> #'.*'"
                    "WS = #'\\s*'"]
                   field-line))))


(defn comment-parser
  "Make an instaparse parser, based on a seq of given fields.
  See docstring for parser-str."
  [req-fields]
  (insta/parser (comment-parser-str req-fields)
                :auto-whitespace whitespace))

(def unified-parser (comment-parser ["class" "method" "modified" "description" "modifiedBy" "created" "createdBy"]))

(defn field-map
  "Merge vector of tag-content pairs into a map of tag=>content."
  [parsed]
  (reduce merge {} parsed))


(defn comment-texts-in-file
  "Get a sequence of documentation comments (text) in the given string."
  [file-text]
  (map first (re-seq doc-comment-regex file-text)))


(defn comment-maps-in-file
  "Get a sequence of parsed, mapped documentation comments for a file text."
  [parser file-text]
  (map #(-> % parser field-map)
       (comment-texts-in-file file-text)))

#_(comment-maps-in-file unified-parser testclass)


(def method-regex #"\s*(global?) \w+ \w+\s?(\w*)")

(def decl-parser
  (insta/parser
    "<S> = (<DROPLINE> | DECL)+
     <DECL> =  (METHOD-DECL | CONSTRUCTOR-DECL | CLASS-DECL) <'{'?> <'}'?>
     METHOD-DECL = ACCESS TYPE NAME WS? <'('> METHODVARDECL* <')'>
     CLASS-DECL = ACCESS SHARING <'class'> NAME IMPLEMENTS?
     CONSTRUCTOR-DECL = ACCESS NAME <'('> METHODVARDECL* <')'>
     METHODVARDECL = TYPE NAME <','?>
     NAME = #'\\w+'
     TYPE = #'(\\w|\\.|<|>)+'
     SHARING = 'with sharing'?
     IMPLEMENTS = <'implements'> TYPE
     ACCESS = 'private' | '' | 'global' | 'public'
     DROPLINE = #'.*\n'
     WS = #'\\s*'"
    :auto-whitespace whitespace))

(comment
  (insta/parse topline "global class Batch_OpenCaseReminderTask implements Database.Batchable<sObject>{")
  (map #(insta/parse topline %)
       ["         global void execute(Database.BatchableContext BC, List<Batch_Logger__c> scope) {"
        "void execute(Database.BatchableContext BC, List<Batch_Logger__c> scope) {"
        "private void execute(Database.BatchableContext BC, List<Batch_Logger__c> scope) {"
        "global void execute(Database.BatchableContext BC, List<Batch_Logger__c> scope) {"])

  (insta/parse topline testclass))

(insta/parse decl-parser testclass)
