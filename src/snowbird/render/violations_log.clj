(ns snowbird.render.violations-log
  (:require [snowbird.render.utils :as rutils]
            [clojure.string :as string]))

(defn pad-left
  [n s]
  (str (string/join (repeat n " "))
       s))

(defn violation-line
  [vio]
  (str (:rule vio)
       ", ln "
       (:line vio)
       ": "
       (:description vio)))

(defn violation-file-str
  [inp]
  (string/join "\n"
    (flatten
      (for [ft (-> inp :config :file-types)]
        [(string/upper-case (name ft))
         (let [ft-violations (into (sorted-map)
                                   (rutils/transpose-by :file-name
                                                        (-> inp :results ft :violations)))]
           (for [fname (keys ft-violations)]
             [(pad-left 2 fname)
              (map #(pad-left 4 (violation-line %))
                    (sort-by :rule (get ft-violations fname)))]))
         ""]))))

(defn specify
  [result cfg _]
  (let [violations-str (violation-file-str result)
        destination (:destination-path cfg)]
    (do
      (spit destination violations-str)
      {:destination-path destination})))
