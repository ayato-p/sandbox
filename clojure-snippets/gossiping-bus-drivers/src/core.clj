(ns core
  "c.f. https://kata-log.rocks/gossiping-bus-drivers-kata"
  (:refer-clojure :exclude [next])
  (:require
   [clojure.test :as t]
   [matcher-combinators.clj-test]
   [matcher-combinators.matchers :as m]))

(defn make-driver [routes]
  (let [gossip (random-uuid)]
    {:routes (vec routes)
     :gossips #{gossip}}))

(defn stop [{:keys [routes]} n]
  (->> (mod n (count routes))
       (get routes)))

(defn simulate' [{:keys [t drivers] :as state}]
  (letfn [(drivers-at-stop [s]
            (filter #(= s (stop % t)) drivers))
          (merge-gossips [driver others]
            (->> (mapcat :gossips others)
                 (update driver :gossips into)))
          (next [drivers]
            (map #(->> (drivers-at-stop (stop % t))
                       (merge-gossips %))
                 drivers))]
    (update state :drivers next)))

(defn simulate [[{:keys [routes] :as driver} :as drivers]]
  (let [all-drivers-known-all-gossips?
        (fn [{:keys [drivers]}]
          (every? #(= (count drivers)
                      (count (:gossips %)))
                  drivers))]
    (or (->> (simulate' {:t 0 :drivers (vec drivers)})
             (iterate #(-> % (update :t inc) simulate'))
             #_(map #(do (clojure.pprint/pprint %) %))
             (take 480)
             (drop-while (complement all-drivers-known-all-gossips?))
             first
             :t)
        :never)))

(t/deftest make-driver-test
  (t/is (match? {:routes [:x :y] :gossips (m/set-equals [uuid?])}
                (make-driver [:x :y]))))

(t/deftest stop-test
  (t/are [expected routes n] (= expected (stop routes n))
    :x {:routes [:x]} 0
    :y {:routes [:x :y]} 1
    :y {:routes [:x :y]} 3
    :x {:routes [:x :y]} 4))

(t/deftest simulate-test
  (t/is (= :never
           (simulate [{:routes [:x] :gossips #{1}}
                      {:routes [:y] :gossips #{2}}])))

  (t/is (= 0
           (simulate [{:routes [:x] :gossips #{1}}
                      {:routes [:x] :gossips #{2}}])))

  (t/is (= 3
           (simulate [{:routes [:x :y] :gossips #{1}}
                      {:routes [:y :x :z] :gossips #{2}}])))

  (t/is (= 4
           (simulate [{:routes [:c :a :b :c] :gossips #{1}}
                      {:routes [:c :b :c :a] :gossips #{2}}
                      {:routes [:d :b :c :d :e] :gossips #{3}}])))

  (t/is (= 56
           (simulate [{:routes [0 1 2 3 4 5 6 7 8 9 10] :gossips #{1}}
                      {:routes [2 3 1] :gossips #{2}}
                      {:routes [9 8 8] :gossips #{3}}
                      {:routes [12 11 10] :gossips #{4}}]))))

