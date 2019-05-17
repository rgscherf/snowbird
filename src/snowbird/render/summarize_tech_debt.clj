(ns snowbird.render.summarize-tech-debt
  (:require [clojure.string :as string]))

(defn filetypes-in-config
  [res]
  (-> res :config :file-types))

;; all files examined
(defn files-examined
  "For a result or specific filetype within result,
  return all the files examined."
  ([res ft]
   (into #{} (-> res :results ft :files-examined)))
  ([res]
   (apply into #{}
          (for [ft (filetypes-in-config res)]
            (files-examined res ft)))))

;; files with violations
(defn files-with-violations
  "For a result or specific filetype within result,
  return the files that are tagged in violations."
  ([res ft]
   (->> (get-in res [:results ft :violations])
        (map :file-name)
        (into #{})))
  ([res]
   (into #{}
         (apply concat
                (for [ft (filetypes-in-config res)]
                  (files-with-violations res ft))))))

(defn tech-debt-ratio
  "Calculate the technical debt ratio. Keys of the file-map are filenames.
  If there are violations for a key, val will be not-nil.
  Therefore, to find files-with-violations, remove any val that is nil."
  ([res ft]
   (float (/ (count (files-with-violations res ft))
             (count (files-examined res ft)))))
  ([res]
   (float (/ (-> res files-with-violations count)
             (-> res files-examined count)))))


(defn format-debt
  "From a double, get a nicely formatted percentage with two decimal places."
  [^double d]
  (double (/ (Math/round (* d 10000)) 100)))

(defn specify
  [result {:keys [print]} _]
  (let [summary (string/join "\n"
                 (flatten
                   [(doall
                      (for [ft (filetypes-in-config result)]
                        (list
                          (str "%% Technical debt for " (name ft) ":")
                          (str "   Files with issues:  " (count (files-with-violations result ft)))
                          (str "   Total files:        " (count (files-examined result ft)))
                          (str "   Debt for filetype:  " (format-debt (tech-debt-ratio result ft)) "%")
                          (str ""))))
                    (str "%% TOTAL TECH DEBT:")
                    (str "   Files with issues: " (count (files-with-violations result)))
                    (str "   Total files:       " (count (files-examined result)))
                    (str "   Overall debt:      " (format-debt (tech-debt-ratio result)) "%")]))]
    (if print (println summary))
    {::debt-summary summary}))


