(ns myloan.core)

(def total 30000000M)

(def rate (/ 0.5M 100))

(def repayment-years 35)

(defn pmt [total rate repayment-count]
  (let [precision 20
        x (-> (* total rate)
              (.divide 12M precision java.math.RoundingMode/HALF_UP)
              (* (.pow (+ 1M (.divide rate 12M precision java.math.RoundingMode/HALF_DOWN)) repayment-count)))
        y (- (.pow
              (+ 1
                 (.divide rate 12M precision java.math.RoundingMode/HALF_UP))
              repayment-count)
             1)]
    (.divide x y precision java.math.RoundingMode/HALF_UP)))

(pmt total rate (* repayment-years 12))
