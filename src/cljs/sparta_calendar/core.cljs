(ns ^:figwheel-always sparta-calendar.core
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]))

(enable-console-print!)

(println "Ready...")

(defonce app-state (atom {:text  "Client-side data"
                          :match {
                                  :team "A tým"
                                  :event "Příprava"
                                  :home-team "AC Sparta Praha"
                                  :away-team "FK Ústí nad Labem"
                                  :term "13. 1. 2016 | 11:00"}}))

(defn get-server-side-data [{:keys [data on-complete]}]
  (let [xhr (XhrIo.)]
    (events/listen xhr goog.net.EventType.COMPLETE
                   (fn [e]
                     (on-complete (reader/read-string (.getResponseText xhr)))))
    (. xhr
       (send "data" "GET" (when data (pr-str data))))))

(defn sample-component [data owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (get-server-side-data
        {:on-complete #(om/transact! data :text (fn [_] %))}))
    om/IRender
    (render [_]
      (dom/div nil (dom/h2 nil (get data :text))))))

(defn match-component [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/tr nil
              (dom/td nil (get data :team))
              (dom/td nil (get data :event))
              (dom/td nil (get data :home-team))
              (dom/td nil (get data :away-team))
              (dom/td nil (get data :term))))))


(defn matches-component [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/table nil
                 (dom/thead nil (dom/tr nil
                                        (dom/th nil "Tým")
                                        (dom/th nil "Soutěž")
                                        (dom/th nil "Domácí")
                                        (dom/th nil "Hosté")
                                        (dom/th nil "Termín")))
                 (om/build match-component (get data :match))))))

(defn root-component [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
               (om/build sample-component data)
               (om/build matches-component data)))))

(om/root root-component app-state
         {:target (.getElementById js/document "root")})