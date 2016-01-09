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

(defn get-matches [] (generate-response (pr-str [{
                                                  :team      "A tým"
                                                  :event     "Příprava"
                                                  :home-team "AC Sparta Praha"
                                                  :away-team "FK Ústí nad Labem"
                                                  :term      "13. 1. 2016 | 11:00"}
                                                 {
                                                  :team      "A tým"
                                                  :event     "Test"
                                                  :home-team "AC Sparta Praha"
                                                  :away-team "bla bla"
                                                  :term      "14. 1. 2016 | 15:00"}])))

(defroutes routes
           (GET "/" [] (index))
           (GET "/matches" [] (get-matches))
           (route/files "/" {:root "resources/public"}))

(def handler
  (-> routes))
