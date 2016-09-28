(defproject sempro "0.1.0-SNAPSHOT"

  :description "Semesterprogramm Manager"
  :url "http://fuxenbesteck.de"

  :dependencies
  [
   ;; core
   [org.clojure/clojurescript "1.9.229" :scope "provided"]
   [org.clojure/tools.logging "0.3.1"]
   [org.clojure/clojure "1.8.0"]
   [org.clojure/tools.cli "0.3.5"]

   ;; ring
   [ring "1.5.0" :exclusions [ring/ring-jetty-adapter]]
   [metosin/ring-http-response "0.8.0"]
   [ring-middleware-format "0.7.0"]
   [ring/ring-defaults "0.2.1"]

   ;; luminus
   [luminus-http-kit "0.1.4"]
   [luminus-nrepl "0.1.3"]
   [luminus/config "0.8"]

   ;; DB
   [org.xerial/sqlite-jdbc "3.8.11.2"]
   [migratus "0.8.28"]
   [conman "0.6.0"]

   ;; utils
   [ch.qos.logback/logback-classic "1.1.7"]
   [compojure "1.5.1"]
   [bouncer "1.0.0"]
   [mount "0.1.10"]
   [cheshire "5.6.3"]
   [midje "1.8.3"]
   [buddy "0.10.0"]
   [clj-time "0.12.0"]
   [cprop "0.1.9"]]

  :main sempro.core
  :source-paths   ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s"
  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]

  :min-lein-version "2.0.0"
  :uberjar-name "sempro.jar"
  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :migratus {:store :database :db ~(get (System/getenv) "DATABASE_URL")}

  :plugins [[lein-cprop "1.0.1"]
            [lein-cljsbuild "1.1.4"]
            [migratus-lein "0.4.1"]
            [lein-cloverage "1.0.6"]]

  :profiles
  {
   :uberjar {
     :omit-source true
     :prep-tasks  ["compile" ["cljsbuild" "once" "min"]]
     :cljsbuild {
       :builds {
        :min {
          :source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
          :compiler     { :output-to     "target/cljsbuild/public/js/app.js"
                          :externs       ["react/externs/react.js"]
                          :optimizations :advanced
                          :pretty-print  false
                          :closure-warnings {:externs-validation :off :non-standard-jsdoc :off}}}}}
     :env            {:production true}
     :aot            :all
     :source-paths   ["env/prod/clj"]
     :resource-paths ["env/prod/resources"]}


   :project/dev {
     :dependencies [[prone "1.1.1"]
                    [ring/ring-mock "0.3.0"]
                    [ring/ring-devel "1.5.0"]
                    [pjstadig/humane-test-output "0.8.1"]
                    [binaryage/devtools "0.8.2"]
                    [figwheel-sidecar "0.5.7"]
                    [com.cemerick/piggieback "0.2.1"]
                    [mvxcvi/puget "1.0.0"]]
       :plugins    [[lein-figwheel "0.5.7"]
                    [org.clojure/clojurescript "1.9.229"]]
       :cljsbuild {
        :builds {
         :app {
          :source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
          :compiler     {:main          "sempro.app"
                         :asset-path    "/js/out"
                         :output-to     "target/cljsbuild/public/js/app.js"
                         :output-dir    "target/cljsbuild/public/js/out"
                         :source-map    true
                         :optimizations :none
                         :pretty-print  true}}}}
       :source-paths   ["env/dev/clj"]
       :resource-paths ["env/dev/resources"]
       :repl-options   {:init-ns user}
       :injections     [(require 'pjstadig.humane-test-output)
                        (pjstadig.humane-test-output/activate!)]
       ;;when :nrepl-port is set the application starts the nREPL server on load
       :env {:dev        true
             :port       3000
             :nrepl-port 7000}}


   :project/test {
     :resource-paths ["env/dev/resources" "env/test/resources"]
     :cljsbuild {
       :builds {
        :test {
         :source-paths ["src/cljc" "src/cljs" "test/cljs"]
         :compiler     {:output-to     "target/test.js"
                        :main          "sempro.doo-runner"  ; TODO: check out doo
                        :optimizations :whitespace
                        :pretty-print  true}}}}
     :env {:test       true
           :port       3001
           :nrepl-port 7001}}


   :dev  [:project/dev :profiles/dev]
   :test [:project/test :profiles/test]
   :profiles/dev  {}
   :profiles/test {}}

  :figwheel {
             :http-server-root "public"
             :nrepl-port       7002
             :css-dirs         ["resources/public/css"]
             :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]})
