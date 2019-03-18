(ns snowbird.specs.core
  (:require [clojure.string :as string]
            [clojure.spec.alpha :as s]))

;; a violation-map keys a filename to a seq of violations
(s/def ::violation-map (s/map-of ::file-name ::file-violations))

;; collection of violations for a file
(s/def ::file-violations (s/map-of keyword? (s/coll-of ::violation)))

;; individual violations
(s/def ::violation (s/keys :req-un [::line ::description ::rule ::file-name ::file-path]))
(s/def ::filetype (s/and keyword? #{:apex :js}))
(s/def ::file-name (s/and string? #(string/includes? % ".")))
(s/def ::file-path string?)

;; config stuff
(s/def ::config (s/keys :req-un [::file-types ::file-search-path ::pmd-rules]))
(s/def ::pmd-rules (s/map-of ::filetype string?))
(s/def ::file-types (s/coll-of ::filetype :type vector?))
