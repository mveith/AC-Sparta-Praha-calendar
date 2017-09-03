(defproject sparta-calendar "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [org.clojure/core.async "0.3.442" :exclusions [org.clojure/tools.reader]]
                 [org.omcljs/om "0.8.8"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]]

  :plugins [[lein-figwheel "0.5.10"]
            [lein-cljsbuild "1.1.5" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src"]

                :figwheel     {:on-jsload "sparta-calendar.core/main"
                               :open-urls ["http://localhost:3449/index.html"]}

                :compiler     {:main                 sparta-calendar.core
                               :asset-path           "js/compiled/out"
                               :output-to            "resources/public/js/compiled/app.js"
                               :output-dir           "resources/public/js/compiled/out"
                               :source-map-timestamp true
                               :preloads             [devtools.preload]}}
               {:id           "min"
                :source-paths ["src"]
                :compiler     {:output-to     "resources/public/js/compiled/app.js"
                               :main          sparta-calendar.core
                               :optimizations :advanced
                               :pretty-print  false}}]}

  :figwheel   {:css-dirs ["resources/public/css"]}

  :profiles {:dev {:dependencies  [[binaryage/devtools "0.9.2"]
                                   [figwheel-sidecar "0.5.10"]
                                   [com.cemerick/piggieback "0.2.1"]]
                   :source-paths  ["src" "dev"]
                   :repl-options  {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                                     :target-path]}})
