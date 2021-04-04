(ns webmain-test
  (:require [clojure.test :refer [are deftest is use-fixtures]]
            [webmain :as web]
            [clojure.data.json :as json]
            [ring.mock.request :as mock]))

(defn prep-data
  [f]
  (reset! webmain/data '())
  (f))

(use-fixtures :each prep-data)

(deftest empty-sorts
  (are [end-point]
       (= {:status  200
           :headers {"Content-Type" "application/json"}
           :body    "[]"}
          (web/myapp (mock/request :get end-point)))
    "/records/email"
    "/records/birthdate"
    "/records/name"))

(deftest post-comma-delim
  (is (= {:status  200
          :headers {"Content-Type" "application/json"}
          :body    (json/write-str '("Curie, Marie, marie.curie@radium.org, orange, 7/7/1867"))}
         (web/myapp (mock/body (mock/request :post "/records") "Curie, Marie, marie.curie@radium.org, orange, 7/7/1867")))))

(deftest post-pipe-delim
  (is (= {:status  200
          :headers {"Content-Type" "application/json"}
          :body    (json/write-str '("Curie | Marie | marie.curie@radium.org | orange | 7/7/1867"))}
         (web/myapp (mock/body (mock/request :post "/records") "Curie | Marie | marie.curie@radium.org | orange | 7/7/1867")))))

(deftest post-ws-delim
  (is (= {:status  200
          :headers {"Content-Type" "application/json"}
          :body    (json/write-str '("Curie Marie marie.curie@radium.org orange 7/7/1867"))}
         (web/myapp (mock/body (mock/request :post "/records") "Curie Marie marie.curie@radium.org orange 7/7/1867")))))

(defn load-data
  []
  (web/myapp (mock/body (mock/request :post "/records") "Röntgen|Wilhelm|xray@mail.com|white|3/27/1845"))
  (web/myapp (mock/body (mock/request :post "/records") "Lorentz | Hendrik | magneto@transform.com | red | 7/18/1853"))
  (web/myapp (mock/body (mock/request :post "/records") "Zeeman | Pieter | zman@doubledutch.nl | blue | 5/25/1865"))
  (web/myapp (mock/body (mock/request :post "/records") "Curie|Marie|marie.curie@radium.org|orange|7/7/1867")))

(deftest get-email-sorted
  (load-data)
  (is (= {:status  200
          :headers {"Content-Type" "application/json"}
          :body    (json/write-str [{:last-name "Lorentz" :first-name "Hendrik" :email "magneto@transform.com" :favorite-color "red" :dob "7/18/1853"}
                                    {:last-name "Curie" :first-name "Marie" :email "marie.curie@radium.org" :favorite-color "orange" :dob "7/7/1867"}
                                    {:last-name "Röntgen" :first-name "Wilhelm" :email "xray@mail.com" :favorite-color "white" :dob "3/27/1845"}
                                    {:last-name "Zeeman" :first-name "Pieter" :email "zman@doubledutch.nl" :favorite-color "blue" :dob "5/25/1865"}])}
         (web/myapp (mock/request :get "/records/email")))))

(deftest get-birthdate-sorted
  (load-data)
  (is (= {:status  200
          :headers {"Content-Type" "application/json"}
          :body    (json/write-str [{:last-name "Röntgen" :first-name "Wilhelm" :email "xray@mail.com" :favorite-color "white" :dob "3/27/1845"}
                                    {:last-name "Lorentz" :first-name "Hendrik" :email "magneto@transform.com" :favorite-color "red" :dob "7/18/1853"}
                                    {:last-name "Zeeman" :first-name "Pieter" :email "zman@doubledutch.nl" :favorite-color "blue" :dob "5/25/1865"}
                                    {:last-name "Curie" :first-name "Marie" :email "marie.curie@radium.org" :favorite-color "orange" :dob "7/7/1867"}])}
         (web/myapp (mock/request :get "/records/birthdate")))))

(deftest get-name-sorted
  (load-data)
  (is (= {:status  200
          :headers {"Content-Type" "application/json"}
          :body    (json/write-str [{:last-name "Curie" :first-name "Marie" :email "marie.curie@radium.org" :favorite-color "orange" :dob "7/7/1867"}
                                    {:last-name "Lorentz" :first-name "Hendrik" :email "magneto@transform.com" :favorite-color "red" :dob "7/18/1853"}
                                    {:last-name "Röntgen" :first-name "Wilhelm" :email "xray@mail.com" :favorite-color "white" :dob "3/27/1845"}
                                    {:last-name "Zeeman" :first-name "Pieter" :email "zman@doubledutch.nl" :favorite-color "blue" :dob "5/25/1865"}])}
         (web/myapp (mock/request :get "/records/name")))))
