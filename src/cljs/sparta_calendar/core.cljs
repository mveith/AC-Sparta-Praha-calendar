(ns sparta-calendar.core
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sparta-calendar.utils :as utils]
            [sparta-calendar.filter :as filter]
            [sparta-calendar.calendar :as calendar]
            [cljs-time.local :as local-time])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]))

(enable-console-print!)

(println "Ready...")

(defonce app-state (atom {:matches     []
                          :all-matches []
                          :now         (local-time/local-now)}))

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

(defn get-location-ico [match] (if (:is-home-match match)
                                 (dom/img #js {:src "css/home.png" :alt "home" :width 74 :height 74})
                                 (dom/img #js {:src "css/bus.png" :alt "away" :width 74 :height 74})))

(defn sparta-content []
  (dom/img #js {:src "css/sparta.png" :alt "ACS" :width 70 :height 80 :className "logo"}))

(defn get-home-team [match]
  (if (:is-home-match match)
    (sparta-content)
    (:home-team match)))

(defn get-away-team [match]
  (if (:is-home-match match)
    (:away-team match)
    (sparta-content)))

(defn match-component [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "match"}
               (dom/div #js {:className "date" :onClick #(calendar/download data)}
                        (dom/div #js {:className "day"} (utils/get-date-string (:date data)))
                        (dom/div #js {:className "time"} (utils/get-time-string (:date data)))
                        (dom/div #js {:className "remaining"} (utils/remaining-time (:date data) (local-time/local-now)))
                        )
               (dom/div #js {:className "main"}
                        (dom/div #js {:className "team"} (:team data))
                        (dom/div #js {:className "event"} (:event data))
                        (dom/div #js {:className "teams"}
                                 (dom/div #js {:className "teamsPart"} (get-home-team data))
                                 (dom/div #js {:className "teamsPart"} "vs.")
                                 (dom/div #js {:className "teamsPart"} (get-away-team data))))
               (dom/div #js {:className "location"} (get-location-ico data))))))

(defn matches-component [data owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (load-matches
        {:on-complete #((om/transact! data :matches (fn [_] %))
                        (om/transact! data :all-matches (fn [_] %)))})

      (om/set-state! owner :interval
                     (js/setInterval
                       #(om/update! data :now (local-time/local-now))
                       (* 60 1000))))

    om/IWillUnmount
    (will-unmount [_]
      (js/clearInterval (om/get-state owner :interval)))

    om/IRender
    (render [_]
      (apply dom/div #js {:className "matches"}
             (om/build-all match-component (:matches data))))))

(defn main []
  (om/root
    (fn [data owner]
      (reify
        om/IRender
        (render [_]
          (dom/div nil
                   (om/build title-component data)
                   (om/build filter/filters-component data)
                   (om/build matches-component data)))))
    app-state
    {:target (. js/document (getElementById "app"))}))
