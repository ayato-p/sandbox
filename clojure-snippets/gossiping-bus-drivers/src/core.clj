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

(defn simulator [drivers]
  (let [seq-of-routes (map :routes drivers)]
    (fn simulate' [{:keys [t drivers] :as state}]
      (let [current-stops (stops seq-of-routes t)]
        {:drivers drivers
         :all-drivers-known-all-gossips? (apply = current-stops)
         :t t}))))

(defn simulate [[{:keys [routes]} :as drivers]]
  (let [simulate' (simulator drivers)]
    (or (->> (simulate' {:t 0})
             (iterate (comp simulate' #(update % :t inc)))
             (take 480)
             (filter :all-drivers-known-all-gossips?)
             first
             :t)
        :never)))

(t/deftest stop-test
  (t/are [expected routes n] (= expected (stop routes n))
    :x [:x] 0
    :y [:x :y] 1
    :y [:x :y] 3
    :x [:x :y] 4))

(t/deftest simulate-test
  (t/is (= :never
           (simulate [{:routes [:x]}
                      {:routes [:y]}])))

  (t/is (= 0
           (simulate [{:routes [:x]}
                      {:routes [:x]}])))

  (t/is (= 3
           (simulate [{:routes [:x :y]}
                      {:routes [:y :x :z]}])))

  (t/is (= 5
           (simulate [{:routes [:c :a :b :c]}
                      {:routes [:c :b :c :a]}
                      {:routes [:d :b :c :d :e]}]))))