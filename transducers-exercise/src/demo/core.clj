(ns demo.core
  (:require [clojure.core.async :as async]
            [clojure.java.io :as io]
            [clojure.xml :as xml]
            [tick.core :as t]
            [cuerdas.core :as str]
            [clojure.core :as c]))

(defmulti parse-nta-data :tag)

(defmethod parse-nta-data :default
  [_])

(defmethod parse-nta-data :corporation
  [{:keys [content]}]
  (into {} (map parse-nta-data content)))

(defmacro def-parse-nta-corporation-element [tag & [f]]
  (let [f (or f identity)]
    `(defmethod parse-nta-data ~tag
       [{:keys [~'content]}]
       [~(str/keyword tag) (~f (first ~'content))])))

(def-parse-nta-corporation-element :sequenceNumber)
(def-parse-nta-corporation-element :corporateNumber)
(def-parse-nta-corporation-element :name)
(def-parse-nta-corporation-element :updateDate)
(def-parse-nta-corporation-element :process)
(def-parse-nta-corporation-element :latest)
(def-parse-nta-corporation-element :prefectureCode)
(def-parse-nta-corporation-element :prefectureName)
(def kind
  {"101" :国の機関
   "201" :地方公共団体
   "301" :株式会社
   "302" :有限会社
   "303" :合名会社
   "304" :合資会社
   "305" :合同会社
   "399" :その他の設立登記法人
   "401" :外国会社等
   "499" :その他})
(def-parse-nta-corporation-element :kind kind)
(def-parse-nta-corporation-element :prefectureCode)
(def-parse-nta-corporation-element :prefectureName)



(comment
  (let [r (Runtime/getRuntime)]
    (prn (.maxMemory r))
    (prn (.totalMemory r)))


  (declare 'zenkoku-all-01-xml
           'zenkoku-all-02-xml
           'zenkoku-all-03-xml
           'zenkoku-all-04-xml
           'zenkoku-all-05-xml
           'zenkoku-all-06-xml)

    ;; Load xml files
  (defn load-xml [filename]
    (with-open [stream (io/input-stream (io/resource filename))]
      (doall (xml/parse stream))))

  (doseq [index #_(range 1 7) (range 1 2)]
    (eval
     `(def ~(symbol (str "zenkoku-all-0" index "-xml"))
        (load-xml (format "houjin_bangou/00_zenkoku_all_20230731_0%d.xml" ~index))))))

(defn map' [f coll]
  (reduce (fn [r x]
            (conj r (f x)))
          []
          coll))

(defn filter' [pred coll]
  (reduce (fn [r x]
            (if (pred x)
              (conj r x)
              r))
          []
          coll))

(defn mapxf [f]
  (fn [rf]
    (fn
      ([acc] (rf acc))
      ([acc x]
       (rf acc (f x))))))

(defn filterxf [pred]
  (fn [rf]
    (fn
      ([acc] (rf acc))
      ([acc x]
       (if (pred x)
         (rf acc x)
         acc)))))

(comment
  (parse-nta-data (first (drop 41 (:content zenkoku-all-01-xml))))

  (count (:content zenkoku-all-01-xml))

  (let [xs (->> (:content zenkoku-all-01-xml)
                (map parse-nta-data)
                (map #(update % :update-date t/date))
                (take 200))]
    (reduce (fn [result x]
              (if (t/< #time/date "2023-01-01" (:update-date x))
                (conj result x)
                result))
            []
            xs))

  (defn naive-find-data [xmldata]
    (->> (:content xmldata)
         (naive-map parse-nta-data)
         (naive-map #(update % :update-date t/date))
         (naive-filter #(t/< #time/date "2020-01-01" (:update-date %)))
         (naive-filter #(= :株式会社 (:kind %)))
         (naive-map (juxt :sequence-number :corporate-number :name :kind :update-date))
         (take 20)))
  (time (first (naive-find-data zenkoku-all-01-xml)))

  (defn find-data-xf [xmldata]
    (let [xf (comp (mapxf parse-nta-data)
                   (mapxf #(update % :update-date t/date))
                   (filterxf #(t/< #time/date "2020-01-01" (:update-date %)))
                   (filterxf #(= :株式会社 (:kind %)))
                   (mapxf (juxt :corporate-number :name :kind :update-date))
                   (take 20))]
      (transduce xf conj (:content xmldata))))
  (time (find-data-xf zenkoku-all-01-xml))

  (= (naive-find-data zenkoku-all-01-xml)
     (find-data-xf zenkoku-all-01-xml)))