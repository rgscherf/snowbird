(ns snowbird.render.print-tech-debt)

(defn filetypes-in-config
  [res]
  (-> res :config :file-types))

;; all files examined
(defn files-examined
  "For a result or specific filetype within result, return all the files examined."
  ([res ft]
   (into #{} (-> res :results ft :files-examined)))
  ([res]
   (apply into #{}
          (for [ft (filetypes-in-config res)]
            (files-examined res ft)))))

;; files with violations
(defn files-with-violations
  "For a result or specific filetype within result, return the files that are tagged in violations."
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
  "Calculate the technical debt ratio. Keys of the file-map are filenames. If there are violations for a key, val will be not-nil.
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
  [result]
  (doall
    (for [ft (filetypes-in-config result)]
      (do
        (println "%% Technical debt for" (name ft) ":")
        (println "   Files with issues:  " (count (files-with-violations result ft)))
        (println "   Total files:        " (count (files-examined result ft)))
        (println "   Debt for filetype:  " (format-debt (tech-debt-ratio result ft)) "%")
        (println ""))))
  (do (println "%% TOTAL TECH DEBT:")
      (println "   Files with issues:" (count (files-with-violations result)))
      (println "   Total files:      " (count (files-examined result)))
      (println "   Overall debt:     " (format-debt (tech-debt-ratio result)) "%")))

