(ns ysera.client-test
  (:require [ysera.test :refer [deftest error? is is-not is= match=]]
            [ysera.error :refer [error]]))


(deftest equals-1-1
  (is (= 1 1))
  (error? (error "an error!"))
  (is= 1 1)
  (is-not (= 1 2))
  (match= {:a 1 :b 2} {:b 2}))
