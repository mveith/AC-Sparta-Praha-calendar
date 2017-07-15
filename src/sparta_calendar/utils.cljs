(ns sparta-calendar.utils
  (:require [goog.string :as gstring]
            [goog.string.format]
            [cljs-time.core :as time]
            [cljs-time.local :as local-time]
            [cljs.reader :as reader]
            [cognitect.transit :as t]))

(defn parse-response [xhr]
  (def r (t/reader :json))
  (reader/read-string (get (t/read r (.getResponseText xhr)) "body")))

(defn get-day-of-week-shortcut [day-of-week]
  (case day-of-week
    1 "PO"
    2 "ÚT"
    3 "ST"
    4 "ČT"
    5 "PÁ"
    6 "SO"
    7 "NE"))

(defn get-date-string [date-time]
  (gstring/format "%s %02d. %02d."
                  (get-day-of-week-shortcut (:day-of-week date-time))
                  (:day date-time)
                  (:month date-time)))

(defn get-time-string [date-time]
  (gstring/format "%02d:%02d"
                  (:hour date-time)
                  (:minute date-time)))

(defn get-roundend-day-date [date-time] (time/date-time (time/year date-time) (time/month date-time) (time/day date-time)))

(defn get-rounded-hours-date [date-time] (time/date-time (time/year date-time) (time/month date-time) (time/day date-time) (time/hour date-time)))

(defn get-match-date [term]
  (local-time/to-local-date-time (time/date-time (:year term) (:month term) (:day term) (:hour term) (:minute term))))

(defn get-days [now match-date]
  (time/in-days (time/interval (get-roundend-day-date now) (get-roundend-day-date match-date))))

(defn get-hours [now match-date]
  (time/in-hours (time/interval (get-rounded-hours-date now) (get-rounded-hours-date match-date))))

(defn remaining-time [term now]
  (let [match-date (get-match-date term)
        days (get-days now match-date)
        hours (get-hours now match-date)]
    (if (= days 0)
      (if (= hours 0) "Za chvíli" (str hours " h"))
      (if (= days 1) "Zítra" (str days " d")))))
