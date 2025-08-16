(ns core
  (:refer-clojure :exclude [next])
  (:require
   [clojure.set :as set]
   [clojure.test :as t]
   [matcher-combinators.clj-test]))

;; c.f. https://kata-log.rocks/gossiping-bus-drivers-kata

(defn stop [routes n]
  {:pre [(<= 0 n)
         (pos? (count routes))]}
  (nth routes (mod n (count routes))))

(defn simulate' [{:keys [t drivers] :as state}]
  (letfn [(drivers-at-stop [s]
            (filter #(= s (stop (:routes %) t)) drivers))
          (merge-gossips [driver others]
            (->> (map :gossips others)
                 (apply set/union)
                 (assoc driver :gossips)))]
    (let [drivers (map (fn [{:keys [routes] :as driver}]
                         (->> (drivers-at-stop (stop routes t))
                              (merge-gossips driver)))
                       drivers)]
      {:drivers drivers
       :all-drivers-known-all-gossips? (every? #(= (count drivers) (count (:gossips %))) drivers)
       :t t})))

(defn simulate [[{:keys [routes] :as driver} :as drivers]]
  (let [drivers (->> drivers
                     (map-indexed #(assoc %2 :gossips #{%1})))]
    (or (->> (simulate' {:t 0 :drivers drivers})
             (iterate #(-> % (update :t inc) simulate'))
             #_(map #(do (clojure.pprint/pprint %) %))
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

  (t/is (= 4
           (simulate [{:routes [:c :a :b :c]}
                      {:routes [:c :b :c :a]}
                      {:routes [:d :b :c :d :e]}]))))