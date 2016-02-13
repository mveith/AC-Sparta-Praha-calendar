(ns sparta-calendar.filter
  (:require [cljs.reader :as reader]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]))

(defn get-teams [matches]
  (distinct (map (fn [m] (:team m)) matches)))

(defn filter-matches-by-team-name [team-name matches]
  (filter (fn [m] (= (:team m) team-name)) matches))

(defn update-visible-matches [state visible-matches]
  (om/update! state :matches (pr-str visible-matches)))

(defn team-filter-component [data owner]
  (reify
    om/IRender
    (render [_]
      (let [state (:state data)
            matches (reader/read-string (:all-matches state))
            team-name (:team data)]
        (dom/button #js {
                         :className "teamFilter"
                         :onClick   (fn [e] (update-visible-matches state (filter-matches-by-team-name team-name matches)))
                         } team-name)))))

(defn filters-component [data owner]
  (reify
    om/IRender
    (render [_]
      (let [teams (get-teams (reader/read-string (:all-matches data)))]
        (apply dom/div #js {:className "teamFilters"}
               (om/build-all team-filter-component (map (fn [t] {:team t :state data}) teams)))))))

