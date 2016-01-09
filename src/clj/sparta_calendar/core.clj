(ns sparta-calendar.core
  (:require [ring.util.response :refer [file-response]]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes GET PUT]]
            [compojure.route :as route]))

(defn index []
  (file-response "public/html/index.html" {:root "resources"}))

(defn generate-response [data & [status]]
  {:status (or status 200)
   :body   (pr-str data)})

(defn get-data [] (generate-response "Server-side data..."))

(defroutes routes
           (GET "/" [] (index))
           (GET "/data" [] (get-data))
           (route/files "/" {:root "resources/public"}))

(def handler
  (-> routes))
