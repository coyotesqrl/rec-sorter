(ns webmain
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [compojure.core :refer [defroutes GET POST]]
            [multisorter :refer [map-multisort]]
            [org.httpkit.server :refer [run-server]]
            [sorter :refer [line->rec]])
  (:import (java.text SimpleDateFormat)))

(def data (atom '()))

(defn- sort->json
  "Using the sort criteria provided, sorts the current dataset and returns it as JSON."
  [s]
  (tap> {:data @data})
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (json/write-str (map-multisort s (map line->rec @data)))})

(defroutes myapp
  (GET "/records/email" []
    (sort->json [{:key :email :dir 1 :txfn str/upper-case}]))
  (GET "/records/birthdate" []
    (sort->json [{:key :dob :dir 1 :txfn #(.parse (SimpleDateFormat. "M/d/yyyy") %)}]))
  (GET "/records/name" []
    (sort->json [{:key :last-name :dir 1 :txfn str/upper-case}
                 {:key :first-name :dir 1 :txfn str/upper-case}]))
  (POST "/records" req
    (with-open [rdr (io/reader (:body req))]
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/write-str (swap! data conj (slurp rdr)))})))

(defn -main []
  (run-server myapp {:port 4000}))
