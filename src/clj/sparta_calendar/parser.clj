(ns sparta-calendar.parser
  (:require
    [net.cgrand.enlive-html :as enlive]
    [clj-time.core :as time]
    [clj-time.format :as format]
    ))

(defn download-html [] (->
                         "http://www.sparta.cz/srv/www/cs/football/match/viewNextMatches.do"
                         (slurp :encoding "UTF-8")
                         java.io.StringReader.
                         enlive/html-resource
                         ))

(defn get-rows [html]
  (->
    html
    (enlive/select [:body :table])
    (first)
    ))

(defn get-child-content [child]
  (:content (first child)))

(defn get-td-content [td]
  (let [content (:content td) child-content (get-child-content content)]
    (first (if (nil? child-content)
             content
             child-content))))

(def date-time-formatter
  (format/formatter "dd. MM. yyyy | HH:mm"))

(defn parse-date [date-time-string]
  (let [date (format/parse date-time-formatter date-time-string)]
    {
     :day         (time/day date)
     :month       (time/month date)
     :year        (time/year date)
     :hour        (time/hour date)
     :minute      (time/minute date)
     :day-of-week (time/day-of-week date)
     }))

(defn starts-with? [string pattern]
  (and
    (>= (count string) (count pattern))
    (= (subs string 0 (count pattern)) pattern)))

(defn home-match? [home-team-name]
  (starts-with? home-team-name "AC Sparta Praha"))

(defn create-match [tr]
  (let [cells (enlive/select tr [:td])]
    (if (not (empty? cells))
      {
       :team          (get-td-content (nth cells 0))
       :event         (get-td-content (nth cells 2))
       :home-team     (get-td-content (nth cells 4))
       :away-team     (get-td-content (nth cells 8))
       :date          (parse-date (get-td-content (nth cells 9)))
       :is-home-match (home-match? (get-td-content (nth cells 4)))
       })
    )
  )

(defn create-matches [] (let [matches (-> (download-html) (get-rows) (enlive/select [:tr]))]
                          (map create-match matches)))

(defn load-matches []
  (filter (fn [x] (not (nil? x))) (create-matches)))
