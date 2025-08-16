(ns core
  (:refer-clojure :exclude [next])
  (:require [clojure.test :as t]
            [matcher-combinators.clj-test]))

(defn stop [routes n]
  {:pre [(<= 0 n)
         (pos? (count routes))]}
  (nth routes (mod n (count routes))))

(defn stops [seq-of-routes n]
  (map #(stop % n) seq-of-routes))

(defn simulate [[{:keys [routes]} :as drivers]]
  (let [seq-of-routes (map :routes drivers)]
    (letfn [(next [{:keys [t]}]
              {:stops (stops seq-of-routes t)
               :t (inc t)})]
      (or (->> (iterate next {:t 0 :stops (stops seq-of-routes 0)})
               (take 480)
               (filter (comp #(apply = %) :stops))
               first
               :t)
          :never))))

(t/deftest stop-test
  (t/are [expected routes n] (= expected (stop routes n))
    :x [:x] 0
    :y [:x :y] 1
    :y [:x :y] 3
    :x [:x :y] 4))

(t/deftest simulate-test
  (t/is (match? :never
                (simulate [{:routes [:x]}
                           {:routes [:y]}])))

  (t/is (match? 0
                (simulate [{:routes [:x]}
                           {:routes [:x]}]))))

