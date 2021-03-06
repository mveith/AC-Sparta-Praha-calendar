(ns sparta-calendar.core
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sparta-calendar.utils :as utils]
            [sparta-calendar.filter :as filter]
            [sparta-calendar.calendar :as calendar]
            [cljs-time.local :as local-time]
            [cljs.reader :as reader])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]))

(enable-console-print!)

(println "Ready...")

(def matches
  (reader/read-string js/matchesData))

(defonce app-state (atom {:matches     matches
                          :all-matches matches}))

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

(defn is-future-match [match]
  (> (utils/get-match-date (:date match)) (local-time/local-now)))

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
    om/IRender
    (render [_]
      (apply dom/div #js {:className "matches"}
             (om/build-all match-component (filter is-future-match (:matches data)))))))

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
  {:target (. js/document (getElementById "app"))})