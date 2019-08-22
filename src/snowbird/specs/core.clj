(ns snowbird.specs.core
  (:require [clojure.string :as string]
            [clojure.spec.alpha :as s]))

;; a violation-map keys a filename to a seq of violations
(s/def ::violation-map (s/map-of ::file-name ::file-violations))

;; collection of violations for a file
(s/def ::file-violations (s/map-of keyword? (s/coll-of ::violation)))

;; individual violations
(s/def ::violation (s/keys :req-un [::line
                                    ::analysis-id
                                    ::description
                                    ::rule
                                    ::file-name]))
(s/def ::filetype (s/and keyword? #{:apex :js}))
(s/def ::file-name (s/and string? #(string/includes? % ".")))
(s/def ::file-path (and string?
                        #(or (string/includes? % "/")
                             (string/includes? % "\\"))))
(s/def ::analysis-id uuid?)
(s/def ::line number?)

;; config stuff
(s/def ::config (s/keys :req-un [::file-types ::pmd-rules ::input ::render]
                        :opt-un [::whitelist ::custom-rules]))
(s/def ::pmd-rules (s/map-of ::filetype string?))
(s/def ::file-types (s/coll-of ::filetype :type vector?))
(s/def ::snowbird-instruction (s/coll-of (s/tuple symbol? map?)
                                         :kind vector))
(s/def ::input ::snowbird-instruction)
(s/def ::render ::snowbird-instruction)
(s/def ::custom-rules (s/map-of ::filetype
                                (s/coll-of symbol? :kind vector?)))
(s/def ::whitelist (s/map-of ::filetype
                             (s/map-of string?
                                       (s/coll-of string? :kind vector?))))

;; Analysis Results
(s/def ::analysis-result (s/keys :req-un [::analysis-time
                                          ::id
                                          ::config
                                          ::results]))
(s/def ::analysis-time inst?)
(s/def ::id uuid?)
(s/def ::results (s/map-of ::filetype ::filetype-analysis))
(s/def ::filetype-analysis (s/keys :req-un
                                   [::files-examined
                                    ::rules
                                    ::violations]))
(s/def ::files-examined (s/coll-of ::file-name))
(s/def ::rules (s/coll-of string?))
(s/def ::violations (s/coll-of ::violation))
