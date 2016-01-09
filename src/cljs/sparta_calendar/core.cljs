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

(defonce app-state (atom {:text "Client-side data"}))

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
                 (dom/tr nil
                         (dom/td nil "A tým")
                         (dom/td nil "Příprava")
                         (dom/td nil "AC Sparta Praha")
                         (dom/td nil "FK Ústí nad Labem")
                         (dom/td nil "13. 1. 2016 | 11:00"))))))

(defn root-component [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
               (om/build sample-component data)
               (om/build matches-component data)))))

(om/root root-component app-state
         {:target (.getElementById js/document "root")})