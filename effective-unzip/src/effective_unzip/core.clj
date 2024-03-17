(ns effective-unzip.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [tick.core :as t])
  (:import [com.univocity.parsers.csv CsvFormat CsvParser CsvParserSettings]
           [java.io ByteArrayOutputStream]
           [java.util.zip ZipFile ZipInputStream])
  (:gen-class))

(def large-zip-file
  (fs/file "/tmp/very-large.zip"))

;; java.io.Fileのシーケンスを返す
(defn zip-files [])

;; java.io.File(ZIPファイル)を受け取り、
;; 任意の日付の範囲内に収まる行データを各CSVファイルから抽出する
;; それらをまとめて単一のシーケンスとして返す
#_(let [months (->> (iterate #(t/>> % (t/of-months 1)) (t/year-month "2023-03"))
                  (take-while #(t/<= % (t/year-month "2024-02")))
                  (map (partial t/format (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM")))
                  set)
      pred (fn pred [l]
             (contains? months (.substring (get l 2) 0 7)))]
  (defn process-zip-file-1 [zip] 
    (with-open [zis (ZipInputStream. (io/input-stream zip))]
      (loop [entry (.getNextEntry zis)
             rows []]
        (if (nil? entry)
          rows
          (let [rows (let [os (ByteArrayOutputStream.)
                           _ (io/copy zis os)]
                       (with-open [rdr (io/reader (.toByteArray os))]
                         (->> (csv/read-csv rdr :separator \|)
                              (filter pred)
                              (into rows))))]
            (recur (.getNextEntry zis) rows)))))))

#_(let [months (->> (iterate #(t/>> % (t/of-months 1)) (t/year-month "2023-03"))
                  (take-while #(t/<= % (t/year-month "2024-02")))
                  (map (partial t/format (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM")))
                  set)
      pred (fn pred [l]
             (contains? months (.substring (get l 2) 0 7)))]
  (defn process-zip-file-2 
    ([zip]
     (let [zip (cond-> zip (instance? java.io.File zip) (ZipFile.))]
       (process-zip-file-2 zip (enumeration-seq (.entries zip)))))
    ([zip [entry :as entries]]
     (when entry
       (lazy-cat (with-open [rdr (io/reader (.getInputStream zip entry))]
                   (->> (csv/read-csv rdr :separator \|)
                        (filter pred)
                        doall))
                 (process-zip-file-2 zip (rest entries)))))))


(defn read-csv [^java.io.Reader rdr]
  (let [parser (let [settings (CsvParserSettings.)
                     _ (.setDelimiter ^CsvFormat (.getFormat settings) \|)]
                 (CsvParser. settings))
        read-line (fn read-line []
                    (if-let [line (.parseNext parser)]
                      (cons line (lazy-seq (read-line)))
                      (.stopParsing parser)))]
    (.beginParsing parser rdr)
    (read-line)))

(let [months (->> (iterate #(t/>> % (t/of-months 1)) (t/year-month "2023-03"))
                  (take-while #(t/<= % (t/year-month "2024-02")))
                  (map (partial t/format (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM")))
                  set)
      pred (fn pred [l]
             (contains? months (subs (get l 2) 0 7)))]
  (defn process-zip-file-3
    ([zip]
     (let [zip (cond-> zip (instance? java.io.File zip) (ZipFile.))]
       (process-zip-file-3 zip (enumeration-seq (.entries zip)))))
    ([zip [entry :as entries]]
     (when entry
       (lazy-cat (with-open [rdr (io/reader (.getInputStream zip entry))]
                   (->> (read-csv rdr)
                        (filter pred)
                        doall))
                 (process-zip-file-3 zip (rest entries)))))))

  
;; 受け取った行データをデータベースに挿入する 
(defn insert [rows]
  (println "Insert" (count rows)))
  

(comment

  (time
   (do
     (println "Process zip file 1")
     (count (process-zip-file-1 large-zip-file)))) 

  (time
   (count (process-zip-file-2 large-zip-file)))

  (time
   (do 
     (println "Process zip file 3")
     (count (process-zip-file-3 large-zip-file))))
  
  ;; 

  )
  


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
