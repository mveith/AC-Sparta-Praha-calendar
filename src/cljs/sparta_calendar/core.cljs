(ns sparta-calendar.core
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]))

(enable-console-print!)

(println "Ready...")

(defonce app-state (atom {:matches []}))


(defn load-matches [{:keys [data on-complete]}]
  (let [xhr (XhrIo.)]
    (events/listen xhr goog.net.EventType.COMPLETE
                   (fn [e]
                     (on-complete (reader/read-string (.getResponseText xhr)))))
    (. xhr
       (send "matches" "GET" (when data (pr-str data))))))

(defn title-component [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
               (dom/h2 nil "Zápasy ACS")))))

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
    om/IWillMount
    (will-mount [_]
      (load-matches
        {:on-complete #(om/transact! data :matches (fn [_] %))}))
    om/IRender
    (render [_]
      (dom/table nil
                 (dom/thead nil (dom/tr nil
                                        (dom/th nil "Tým")
                                        (dom/th nil "Soutěž")
                                        (dom/th nil "Domácí")
                                        (dom/th nil "Hosté")
                                        (dom/th nil "Termín")))
                 (apply dom/tbody nil
                        (om/build-all match-component (reader/read-string (get data :matches))))))))

(defn main []
  (om/root
    (fn [data owner]
      (reify
        om/IRender
        (render [_]
          (dom/div nil
                   (om/build title-component data)
                   (om/build matches-component data)))))
    app-state
    {:target (. js/document (getElementById "app"))}))
