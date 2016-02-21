(ns sparta-calendar.calendar
  (:require [sparta-calendar.utils :as utils]
            [cljs-time.local :as local-time]
            [cljs-time.format :as format]
            [cljs-time.core :as time])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]))

(def br "%0D%0A")

(def custom-formatter (format/formatter "yyyyMMddTHHmmssZ"))

(defn get-date-time-string [date-time]
  (format/unparse custom-formatter date-time))

(defn get-match-summary [match] (str (:home-team match) " - " (:away-team match)))

(defn get-match-calendar-file-content [match]
  (let [
        match-date (utils/get-match-date (:date match))
        start-date (get-date-time-string match-date)
        end-date (get-date-time-string (time/plus match-date (time/hours 2)))
        time-stamp (get-date-time-string (local-time/local-now))
        summary (get-match-summary match)
        location (:home-team match)
        content (str
                  "BEGIN:VCALENDAR" br
                  "VERSION:2.0" br
                  "METHOD:PUBLISH" br
                  "BEGIN:VEVENT" br
                  "DTSTART:" start-date br
                  "DTEND:" end-date br
                  "LOCATION:" location br
                  "UID:" time-stamp br
                  "DTSTAMP:" time-stamp br
                  "SUMMARY:" summary br
                  "DESCRIPTION:" summary br
                  "PRIORITY:5" br
                  "CLASS:PUBLIC" br
                  "END:VEVENT" br
                  "END:VCALENDAR")]
    (str "data:text/calendar;charset=utf-8," content)))

(defn download [match] (let [a (js/window.document.createElement "a")
                             href (get-match-calendar-file-content match)]
                         (set! (.-href a) href)
                         (set! (.-download a) (str (get-match-summary match) ".ics"))
                         (js/document.body.appendChild a)
                         (.click a)
                         (js/document.body.removeChild a)
                         ))
