(ns core
  (:require [clojure.test :as t]
            [matcher-combinators.clj-test]))

(defn stop [routes n]
  (nth routes n))

(defn simulate [drivers]
  (letfn []))


(t/deftest stop-test
  (t/is (= :x (stop [:x] 0))))

(t/deftest simulate-test
  #_(t/is (match? :never
                  (simulate [{:routes [:x]}
                             {:routes [:y]}]))))

