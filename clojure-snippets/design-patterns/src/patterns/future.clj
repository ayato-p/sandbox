(ns patterns.future
  (:require [clojure.string :as str]))

(defprotocol Data
  (get-content [this]))

(defprotocol IFuture
  (set-real-data [this obj]))

(deftype FutureData [^:volatile-mutable real-data
                     ^:volatile-mutable ready]
  IFuture
  (set-real-data [this obj]
    (locking this
      (when-not (.-ready this)
        (set! (.-real-data this) obj)
        (set! (.-ready this) true)
        (.notifyAll this))))

  Data
  (get-content [this]
    (locking this
      (while (not (.-ready this))
        (println "waiting for real-data")
        (.wait this))
      (get-content (.-real-data this)))))

(defn new-future-data []
  (FutureData. nil false))

(deftype RealData [content]
  Data
  (get-content [_]
    content))

(defn new-real-data [cnt c]
  (println "making RealData %s, %s BEGIN" cnt c)
  (dotimes [_ cnt]
    (Thread/sleep 100))
  (println "making RealData %s, %s END" cnt c)
  (RealData. (str/join "" (repeat cnt c))))

(defn request [cnt c]
  (let [future-data (new-future-data)]
    (println "Started thread for" c)
    (.start
     (Thread.
      (fn []
        (set-real-data future-data (new-real-data cnt c)))))
    future-data))

(comment
  (let [data1 (request 10 'a)
        data2 (request 20 'b)]
    (println "Waiting for threads to complete...")
    (Thread/sleep 3000)  ; ワーカースレッドが完了するまで十分に待機
    (println "Getting content for data1..."
             (get-content data1))
    (println "Getting content for data2..."
             (get-content data2))))

