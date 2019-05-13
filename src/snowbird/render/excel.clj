(ns snowbird.render.excel
  (:require [dk.ative.docjure.spreadsheet :as dj]
            [snowbird.core :as sbird]
            [clojure.string :as string]
            [snowbird.input.file-system :as fs])
  (:import (org.apache.poi.hssf.usermodel HSSFSheet HSSFCell)))

(defn violations-at
  [results file rule]
  (filter #(and (= rule (:rule %))
                (= file (:file-name %)))
          (:violations results)))

(defn workbook-entries
  [results]
  (first
    (concat
      (for [ft (:file-types (:config results))]
        (let [apx (-> results :results ft)
              fnames (-> apx :files-examined sort)
              rnames (-> apx :rules sort)]
          (list
            (-> ft name string/capitalize)
            (into [] (cons
                      (into [] (cons "" rnames))
                      (for [f fnames]
                        (into [] (concat [f] (map #(count (violations-at apx f %)) rnames))))))))))))


(defn format-wb
  "(Messily) format a workbook with the following rules on each sheet:
  - Header cells should have a small font and be hcentered.
  - Other cells, if numeric, should have a large font and centered.
  - Else, small font and hleft."
  [wb]
  (doall
    (for [sheet (dj/sheet-seq wb)]
      (let [rows (dj/row-seq sheet)
            header (first rows)
            content-rows (rest rows)]
        (do
          (dj/set-row-style! header (dj/create-cell-style! wb {:halign :center
                                                               :font {:size 8
                                                                      #_#_:bold true
                                                                      :italic true}}))
          (doall (for [r content-rows]
                   (doall
                     (for [c (dj/cell-seq r)]
                        (dj/set-cell-style! c (if (try (.getNumericCellValue c)
                                                       (catch IllegalStateException _ nil))
                                                  (dj/create-cell-style! wb {:halign :center
                                                                              :font {:size 14}})
                                                  (dj/create-cell-style! wb {:halign :left
                                                                              :font {:size 8}})))))))
          (doall
            (for [col-idx (-> header dj/cell-seq count range)]
             (.autoSizeColumn sheet col-idx))))))))


;(defn try-parse-int
;  [maybe-int]
;  (if (integer? maybe-int)
;    maybe-int
;    (try
;      (Integer/parseInt maybe-int)
;      (catch NumberFormatException n nil))))

(defn specify
  "Marshal, format, and save data to a workbook."
  [results {:keys [destination-file]} _]
  (let [wb (apply dj/create-workbook (workbook-entries results))]
    (format-wb wb)
    (dj/save-workbook! destination-file wb)))

(comment
  (specify result {:destination-file "apex_sheet.xlsx"} :none)
  (+ 1 1)
  (Integer/parcc)
  (Integer/parseInt "hello")
  (def result
    (sbird/analysis-result (fs/read-default-config)))
  (workbook-entries result))

