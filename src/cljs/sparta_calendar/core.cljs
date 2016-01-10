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
      (dom/div #js {:className "title"}
               (dom/h2 nil "AC SPARTA PRAHA - následující zápasy")))))

(defn match-component [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "match"}
               (dom/label nil
                          (get data :term)
                          " | "
                          (get data :team)
                          " | "
                          (get data :event))
               (dom/br nil)
               (dom/label #js {:className "teams"}
                          (get data :home-team)
                          " - "
                          (get data :away-team))
               )

      )))

(defn matches-component [data owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (load-matches
        {:on-complete #(om/transact! data :matches (fn [_] %))}))
    om/IRender
    (render [_]
      (apply dom/div #js {:className "matches"}
             (om/build-all match-component (reader/read-string (get data :matches)))))))

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
