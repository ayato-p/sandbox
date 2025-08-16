(ns core
  (:require [clojure.test :as t]
            [matcher-combinators.clj-test]))

(defn stop [routes n]
  {:pre [(<= 0 n)
         (pos? (count routes))]}
  (nth routes (mod n (count routes))))

(defn simulate [drivers]
  (letfn []))

(t/deftest stop-test
  (t/are [expected routes n] (= expected (stop routes n))
    :x [:x] 0
    :y [:x :y] 1
    :y [:x :y] 3
    :x [:x :y] 4))

(t/deftest simulate-test
  #_(t/is (match? :never
                  (simulate [{:routes [:x]}
                             {:routes [:y]}]))))

