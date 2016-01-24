(ns sparta-calendar.utils
  (:require [goog.string :as gstring]))

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
                  (get-day-of-week-shortcut (get date-time :day-of-week))
                  (get date-time :day)
                  (get date-time :month)))

(defn get-time-string [date-time]
  (gstring/format "%02d:%02d"
                  (get date-time :hour)
                  (get date-time :minute)))
