(ns snowbird.specs
  (:require [clojure.spec.alpha :as s]))

(s/def ::violation-map (s/map-of string? ::file-violations))
(s/def ::file-violations (s/map-of keyword? (s/coll-of ::violation)))
(s/def ::violation (s/keys :req-un [::line ::description ::rule]))
(s/def ::filetype #(or (= % ".cls")
                       (= % ".js")))

(s/def ::file-types (s/coll-of ::filetype :type vector?))
(s/def ::config
  (s/keys :req-un [::file-types ::file-search-path ::apex-rules]))
