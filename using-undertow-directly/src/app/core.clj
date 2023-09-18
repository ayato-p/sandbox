(ns app.core
  (:require [clojure.core.async :as async])
  (:import [io.undertow Undertow]
           [io.undertow.server HttpHandler HttpServerExchange]))

(proxy [HttpHandler] []
  (handleRequest [^HttpHandler handler ^HttpServerExchange exhange]))

(reify HttpHandler
  (^void handleRequest [^HttpHandler handler ^HttpServerExchange exchange]
    (.send (.getResponseSender exchange) "Hello, world")))

(comment
  (defn myhandler [f]
    (async/go
      (async/<! (async/timeout 1000))
      (f "Hello, world")))
  (def s (let [server (.. (Undertow/builder)
                          (addHttpListener 8000 "127.0.0.1")
                          (setHandler (reify HttpHandler
                                        (^void handleRequest [^HttpHandler handler ^HttpServerExchange exchange]
                                          (letfn [(f [x]
                                                    (.send (.getResponseSender exchange) x))]
                                            (if (.isInIoThread exchange)
                                              (.dispatch exchange handler)
                                              (myhandler f)))
                                          #_(if (.isInIoThread exchange)
                                              (.dispatch exchange handler)
                                              (do
                                                (Thread/sleep 1000)
                                                (.send (.getResponseSender exchange) "Hello, world"))))))
                          (build))]
           (.start server)
           server)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
