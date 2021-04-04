(ns sorter
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [multisorter :refer [map-multisort]])
  (:import (java.text SimpleDateFormat)))

; todo: more tests

;
; The set of valid whitespace/delimiter characters
;
(def ws-chars #"[\s | , | \|]+")

(defn line->rec
  "Reads an input string of five delimited fields and converts to a user record map."
  [line]
  (zipmap [:last-name :first-name :email :favorite-color :dob]
          (str/split line ws-chars)))

(defn- input->recs
  "Takes an input file and reads it in, converting each of its lines to a user record."
  [file]
  (->> file
       io/file
       io/reader
       line-seq
       (map str/trim)
       (remove empty?)
       (map line->rec)))

(defn email-then-lname-sort
  "Sorts user records by email, descending; then, if there are collisions, by last name, ascending."
  [coll]
  (map-multisort [{:key :email :dir -1 :txfn str/upper-case}
                  {:key :last-name :dir 1 :txfn str/upper-case}]
                 coll))

(defn dob-sort
  "Sorts user records by birth date, ascending."
  [coll]
  (map-multisort [{:key :dob :dir 1 :txfn #(.parse (SimpleDateFormat. "M/d/yyyy") %)}]
                 coll))

(defn last-name-sort
  "Sorts user records by last name, descending."
  [coll]
  (map-multisort [{:key :last-name :dir -1 :txfn str/upper-case}]
                 coll))

(defn -main
  []
  (prn "Input file?")
  (let [f (read-line)]
    (prn "Sort option? [1|2|3]")
    (let [x     (read-line)
          input (input->recs f)]
      (pp/pprint (cond
                   (= "1" x) (email-then-lname-sort input)
                   (= "2" x) (dob-sort input)
                   (= "3" x) (last-name-sort input)
                   :else (println "Unsupported option"))))))
