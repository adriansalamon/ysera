(ns ysera.test
  #?(:clj (:require [clojure.test]
                    [net.cgrand.macrovich :as macros]))
  #?(:cljs (:require [cljs.test]))
  #?(:cljs (:require-macros [net.cgrand.macrovich :as macros]
                            [ysera.test :refer [deftest testing is is-not is= error?]]))
  ;(:require [clojure.test]
  ;          #?(:clj [net.cgrand.macrovich :refer [case deftime]]
  ;             :cljs [net.cgrand.macrovich :refer-macros [case deftime]])
  ;          #?(:cljs [ysera.test :refer [deftest testing is is-not is= error?]]))
  ;#?(:clj  (:require [clojure.test]
  ;                   [net.cgrand.macrovich :as macros])
  ;   :cljs (:require-macros [net.cgrand.macrovich :as macros]
  ;                          [ysera.test :refer [deftest testing is is-not is= error?]]))
  )

(macros/deftime

  (defmacro is= [actual expected]
    `(do
       (let [actual# ~actual
             expected# ~expected
             equal# (= actual# expected#)]
         (when-not equal#
           (println "Actual:\t\t" actual# "\nExpected:\t" expected#))
         (macros/case :clj (clojure.test/is (= actual# expected#))
                      :cljs (cljs.test/is (= actual# expected#))))))


  (defmacro match= [actual expected]
    `(do
       (let [actual# ~actual
             expected# ~expected
             mismatches# (find-mismatches actual# expected#)
             equal# (empty? mismatches#)]
         (when-not equal#
           (println "Mismatches found:")
           (doseq [mismatch# mismatches#]
             (println " - Key:" (:key mismatch#)
                      "\n   Actual:  \t" (:actual mismatch#)
                      "\n   Expected:\t" (:expected mismatch#))))
         (macros/case :clj (clojure.test/is equal#)
                      :cljs (cljs.test/is equal#)))))

  (defmacro deftest [name & body]
    `(do
       (macros/case :clj (clojure.test/deftest ~name ~@body)
                    :cljs (cljs.test/deftest ~name ~@body))))

  (defmacro testing [string & body]
    `(do
       (macros/case :clj (clojure.test/testing ~string ~@body)
                    :cljs (cljs.test/testing ~string ~@body))))

  (defmacro is [form]
    `(do
       (macros/case :clj (clojure.test/is ~form)
                    :cljs (cljs.test/is ~form))))

  (defmacro is-not [form]
    `(do
       (macros/case :clj (clojure.test/is (not ~form))
                    :cljs (cljs.test/is (not ~form)))))

  (defmacro error? [actual]
    `(do ~(macros/case :clj `(try (do ~actual
                                      (println "An error was expected.")
                                      (clojure.test/is false ~(str "An error was expected:" actual)))
                                  (catch Exception e#
                                    (clojure.test/is true)))
                       :cljs `(try (do ~actual
                                       (println "An error was expected.")
                                       (cljs.test/is false ~(str "An error was expected: " actual)))
                                   (catch js/Object e#
                                     (cljs.test/is true)))))))


(defn find-mismatches
  "Finds keys in expected that are not equal to the corresponding keys in actual."
  [actual expected]
  (reduce (fn [acc key]
            (let [actual-val (get actual key)
                  expected-val (get expected key)]
              (if (= actual-val expected-val)
                acc
                (conj acc {:key key, :actual actual-val, :expected expected-val}))))
          []
          (keys expected)))