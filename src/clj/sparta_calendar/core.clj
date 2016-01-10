(ns sparta-calendar.core
  (:require [ring.util.response :refer [file-response]]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes GET PUT]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [sparta-calendar.parser :as parser]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]))

(defn index []
  (file-response "public/html/index.html" {:root "resources"}))

(defn generate-response [data & [status]]
  {:status (or status 200)
   :body   (pr-str data)})

(defn get-matches []
  (generate-response (pr-str (parser/load-matches))))

(defroutes app
           (GET "/" [] (index))
           (GET "/matches" [] (get-matches))
           (route/files "/" {:root "resources/public"}))

(defroutes routes
           (GET "/" [] (index))
           (GET "/matches" [] (get-matches))
           (route/files "/" {:root "resources/public"}))

(def handler
  (-> routes))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

