(ns sparta_calendar.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [sparta_calendar.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'sparta_calendar.core-test))
    0
    1))
