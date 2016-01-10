(defproject sparta-calendar "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://mv1893.herokuapp.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :jvm-opts ^:replace ["-Xmx1g" "-server"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3195"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.omcljs/om "0.8.8"]
                 [ring "1.3.2"]
                 [compojure "1.3.1"]
                 [enlive "1.1.6"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [environ "1.0.0"]]

  :min-lein-version "2.0.0"

  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-figwheel "0.2.9"]
            [environ/environ.lein "0.3.1"]]

  :hooks [environ.leiningen.hooks]

  :source-paths ["src/clj" "src/cljs"]
  :resource-paths ["resources"]
  :clean-targets ^{:protect false} ["resources/public/js/out"
                                    "resources/public/js/main.js"]

  :figwheel {:ring-handler sparta-calendar.core/handler}

  :uberjar-name "sparta-calendar.jar"

  ;:profiles {:production {:env {:production true}}})

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/clj" "src/cljs"]
                        :figwheel true
                        :compiler {:output-to "resources/public/js/main.js"
                                   :output-dir "resources/public/js/out"
                                   :main sparta-calendar.core
                                   :asset-path "js/out"
                                   :optimizations :none
                                   :source-map true}}]})
