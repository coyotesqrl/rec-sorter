(ns multisorter-test
  (:require [clojure.test :refer [are deftest is]]
            [clojure.string :as str]
            [multisorter :refer [map-multisort]]))

(deftest single-incremental-int
  (are [res input sorts]
       (= res (map-multisort sorts input))
    '({:a 1 :b 74} {:a 2 :b 6} {:a 3 :b 6}) '({:a 2 :b 6} {:a 3 :b 6} {:a 1 :b 74}) [{:key :a :dir 1}]
    '({:a 1 :b 74} {:a 2 :b 6} {:a 3 :b 6}) '({:a 3 :b 6} {:a 2 :b 6} {:a 1 :b 74}) [{:key :a :dir 1}]
    '({:a 1 :b 74} {:a 2 :b 6} {:a 3 :b 6}) '({:a 2 :b 6} {:a 1 :b 74} {:a 3 :b 6}) [{:key :a :dir 1}]
    '({:a 1 :b 74} {:a 2 :b 6} {:a 3 :b 6}) '({:a 1 :b 74} {:a 2 :b 6} {:a 3 :b 6}) [{:key :a :dir 1}]))

(deftest single-decremental-int
  (are [res input sorts]
       (= res (map-multisort sorts input))
    '({:a 3 :b 6} {:a 2 :b 6} {:a 1 :b 74}) '({:a 2 :b 6} {:a 3 :b 6} {:a 1 :b 74}) [{:key :a :dir -1}]
    '({:a 3 :b 6} {:a 2 :b 6} {:a 1 :b 74}) '({:a 3 :b 6} {:a 2 :b 6} {:a 1 :b 74}) [{:key :a :dir -1}]
    '({:a 3 :b 6} {:a 2 :b 6} {:a 1 :b 74}) '({:a 2 :b 6} {:a 1 :b 74} {:a 3 :b 6}) [{:key :a :dir -1}]
    '({:a 3 :b 6} {:a 2 :b 6} {:a 1 :b 74}) '({:a 1 :b 74} {:a 2 :b 6} {:a 3 :b 6}) [{:key :a :dir -1}]))

(deftest double-increment-decrement-int
  (are [res input sorts]
       (= res (map-multisort sorts input))
    '({:a 1 :b 6} {:a 2 :b 6} {:a 2 :b 5} {:a 2 :b 4} {:a 3 :b 74}) '({:a 2 :b 5} {:a 2 :b 4} {:a 3 :b 74} {:a 2 :b 6} {:a 1 :b 6}) [{:key :a :dir 1} {:key :b :dir -1}]
    '({:a 1 :b 6} {:a 2 :b 6} {:a 2 :b 5} {:a 2 :b 4} {:a 3 :b 74}) '({:a 2 :b 4} {:a 2 :b 5} {:a 3 :b 74} {:a 2 :b 6} {:a 1 :b 6}) [{:key :a :dir 1} {:key :b :dir -1}]
    '({:a 1 :b 6} {:a 2 :b 6} {:a 2 :b 5} {:a 2 :b 4} {:a 3 :b 74}) '({:a 3 :b 74} {:a 2 :b 6} {:a 2 :b 5} {:a 2 :b 4} {:a 1 :b 6}) [{:key :a :dir 1} {:key :b :dir -1}]
    '({:a 1 :b 6} {:a 2 :b 6} {:a 2 :b 5} {:a 2 :b 4} {:a 3 :b 74}) '({:a 2 :b 5} {:a 2 :b 6} {:a 1 :b 6} {:a 2 :b 4} {:a 3 :b 74}) [{:key :a :dir 1} {:key :b :dir -1}]
    '({:a 1 :b 6} {:a 2 :b 6} {:a 2 :b 5} {:a 2 :b 4} {:a 3 :b 74}) '({:a 2 :b 5} {:a 3 :b 74} {:a 2 :b 4} {:a 2 :b 6} {:a 1 :b 6}) [{:key :a :dir 1} {:key :b :dir -1}]))

(deftest double-with-txf
  (is (= '({:a "6/18/2010" :b "hello"}
           {:a "1/3/2008" :b "ahello"}
           {:a "1/3/2008" :b "Hello"}
           {:a "11/23/1975" :b "ahello"}
           {:a "11/23/1975" :b "bhello"}
           {:a "11/23/1975" :b "chello"}
           {:a "11/23/1975" :b "hello"})
         (map-multisort [{:key :a :dir -1 :txfn #(.parse (java.text.SimpleDateFormat. "M/d/yyyy") %)}
                         {:key :b :dir 1 :txfn str/upper-case}]
                        '({:a "1/3/2008" :b "Hello"}
                          {:a "11/23/1975" :b "hello"}
                          {:a "1/3/2008" :b "ahello"}
                          {:a "11/23/1975" :b "bhello"}
                          {:a "11/23/1975" :b "ahello"}
                          {:a "6/18/2010" :b "hello"}
                          {:a "11/23/1975" :b "chello"})))))
