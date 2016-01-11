(ns sparta-calendar.parser
  (:require
    [net.cgrand.enlive-html :as enlive]))

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

(defn get-td-content [td] (let [content (:content td)
                                child-content (get-child-content content)]
                            (first (if (nil? child-content)
                                     content
                                     child-content))))

(defn create-match [tr]
  (let [cells (enlive/select tr [:td])]
    (if (not (empty? cells))
      {
       :team      (get-td-content (nth cells 0))
       :event     (get-td-content (nth cells 2))
       :home-team (get-td-content (nth cells 4))
       :away-team (get-td-content (nth cells 8))
       :term      (get-td-content (nth cells 9))})
    )
  )

(defn create-matches [] (let [matches (-> (download-html) (get-rows) (enlive/select [:tr]))]
                          (map create-match matches)))

(defn load-matches []
  (filter (fn [x] (not (nil? x))) (create-matches)))
