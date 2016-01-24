(ns sparta-calendar.core
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.string :as gstring]
            [goog.string.format])
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

(defn get-date-string [date-time]
  (gstring/format "%02d. %02d." (get date-time :day) (get date-time :month)))
(defn get-time-string [date-time]
  (gstring/format "%02d:%02d" (get date-time :hour) (get date-time :minute)))

(defn get-location-ico [match] (if (get match :is-home-match)
                                 (dom/img #js {:src "css/home.png" :alt "home" :width 74 :height 74})
                                 (dom/img #js {:src "css/bus.png" :alt "away" :width 74 :height 74})))

(defn sparta-content []
  (dom/img #js {:src "css/sparta.png" :alt "ACS" :width 70 :height 80 :className "logo"}))

(defn get-home-team [match] (if (get match :is-home-match) (sparta-content) (get match :home-team)))
(defn get-away-team [match] (if (get match :is-home-match) (get match :away-team) (sparta-content)))

(defn match-component [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "match"}
               (dom/div #js {:className "date"}
                        (dom/div #js {:className "day"} (get-date-string (get data :date)))
                        (dom/div #js {:className "time"} (get-time-string (get data :date)))
                        (dom/div #js {:className "remaining"} "...")
                        )
               (dom/div #js {:className "main"}
                        (dom/div #js {:className "team"} (get data :team))
                        (dom/div #js {:className "event"} (get data :event))
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
