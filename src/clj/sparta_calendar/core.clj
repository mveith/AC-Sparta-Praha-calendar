(ns sparta-calendar.core
  (:require [ring.util.response :refer [file-response]]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes GET PUT]]
            [compojure.route :as route]
            [sparta-calendar.parser :as parser]))

(defn index []
  (file-response "public/html/index.html" {:root "resources"}))

(defn generate-response [data & [status]]
  {:status (or status 200)
   :body   (pr-str data)})

(defn get-matches []
  (generate-response (pr-str (parser/load-matches))))

(defroutes routes
           (GET "/" [] (index))
           (GET "/matches" [] (get-matches))
           (route/files "/" {:root "resources/public"}))

(def handler
  (-> routes))
