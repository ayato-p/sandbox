(ns core
  (:require [clojure.test :as t]
            [matcher-combinators.clj-test]
            [matcher-combinators.test :refer [match?]]))

(def level1-elements
  [[:l1-x {:value 1}]
   [:l1-y {:value 2}]
   [:l1-z {:value 3}]])

(def level2-elements
  [[:l2-1 {:value 11, :l1 :l1-x}]
   [:l2-2 {:value 21, :l1 :l1-y}]
   [:l2-3 {:value 22, :l1 :l1-y}]
   [:l2-4 {:value 31, :l1 :l1-z}]])

(def level3-elements
  [[:l3-A {:value 111, :l2 :l2-1}]
   [:l3-B {:value 112, :l2 :l2-1}]
   [:l3-C {:value 211, :l2 :l2-2}]
   [:l3-D {:value 212, :l2 :l2-2}]
   [:l3-E {:value 221, :l2 :l2-3}]])

(defn build-tree [l1-elms l2-elms l3-elms]
  {:name :top
   :children
   (for [[l1-name _] l1-elms]
     {:name l1-name
      :children
      (for [[l2-name {:keys [l1]}] l2-elms
            :when (= l1-name l1)]
        {:name l2-name
         :children (for [[l3-name {:keys [l2]}] l3-elms
                         :when (= l2-name l2)]
                     {:name l3-name})})})})

(t/deftest build-tree-test
  (t/is (match? {:name :top
                 :children [{:name :l1-x
                             :children [{:name :l2-1
                                         :children [{:name :l3-A}
                                                    {:name :l3-B}]}]}
                            {:name :l1-y
                             :children [{:name :l2-2
                                         :children [{:name :l3-C}
                                                    {:name :l3-D}]}
                                        {:name :l2-3
                                         :children [{:name :l3-E}]}]}
                            {:name :l1-z
                             :children [{:name :l2-4}]}]}
                (build-tree level1-elements level2-elements level3-elements))))

(defn build-tree' [l1-elms l2-elms l3-elms]
  (letfn [(l1-node [[l1-name _]]
            {:name l1-name
             :children (keep #(l2-node l1-name %) l2-elms)})
          (l2-node [l1-name [l2-name {:keys [l1]}]]
            (when (= l1-name l1)
              {:name l2-name
               :children (keep #(l3-node l2-name %) l3-elms)}))
          (l3-node [l2-name [l3-name {:keys [l2]}]]
            (when (= l2-name l2)
              {:name l3-name}))]
    {:name :top
     :children (map l1-node l1-elms)}))

(t/deftest build-tree'-test
  (t/is (match? {:name :top
                 :children [{:name :l1-x
                             :children [{:name :l2-1
                                         :children [{:name :l3-A}
                                                    {:name :l3-B}]}]}
                            {:name :l1-y
                             :children [{:name :l2-2
                                         :children [{:name :l3-C}
                                                    {:name :l3-D}]}
                                        {:name :l2-3
                                         :children [{:name :l3-E}]}]}
                            {:name :l1-z
                             :children [{:name :l2-4}]}]}
                (build-tree' level1-elements level2-elements level3-elements))))

(defn map-cps [coll f k]
  (if (seq coll)
    (let [[item & rest-items] coll]
      (f item
         (fn [processed-item]
           (map-cps rest-items
                    f
                    (fn [processed-rest]
                      (k (cons processed-item processed-rest)))))))
    (k [])))

(map-cps (range 10)
         (fn [x k]
           (k (* x 10)))
         println)

(defn build-tree-cps [l1-elements l2-elements l3-elements k]
  (let [grouped-l2 (group-by (comp :l1 second) l2-elements)
        grouped-l3 (group-by (comp :l2 second) l3-elements)]
    (letfn [(build-l1-node-cps [[name _] k]
              (map-cps (get grouped-l2 name)
                       build-l2-node-cps
                       (fn [children]
                         (k {:name name
                             :children children}))))
            (build-l2-node-cps [[name _]  k]
              (map-cps (get grouped-l3 name)
                       build-l3-node-cps
                       (fn [children]
                         (k {:name name
                             :children children}))))
            (build-l3-node-cps [[name _] k]
              (k {:name name}))]
      (map-cps l1-elements
               build-l1-node-cps
               (fn [children]
                 (k {:name :top
                     :children children}))))))

(t/deftest build-tree-cps-test
  (t/is (match? {:name :top
                 :children [{:name :l1-x
                             :children [{:name :l2-1
                                         :children [{:name :l3-A}
                                                    {:name :l3-B}]}]}
                            {:name :l1-y
                             :children [{:name :l2-2
                                         :children [{:name :l3-C}
                                                    {:name :l3-D}]}
                                        {:name :l2-3
                                         :children [{:name :l3-E}]}]}
                            {:name :l1-z
                             :children [{:name :l2-4}]}]}
                (build-tree-cps level1-elements level2-elements level3-elements identity))))
