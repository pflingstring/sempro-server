(defproject sempro "0.1.0-SNAPSHOT"

  :description "Semesterprogramm Manager"
  :url "http://fuxenbesteck.de"

  :dependencies
  [
   ;; core
   [org.clojure/tools.logging "0.3.1"]
   [org.clojure/clojure "1.8.0"]

   ;; ring
   [ring "1.5.0" :exclusions [ring/ring-jetty-adapter]]
   [metosin/ring-http-response "0.8.0"]
   [ring-middleware-format "0.7.0"]
   [ring/ring-defaults "0.2.1"]

   ;; luminus
   [luminus-http-kit "0.1.4"]
   [luminus-nrepl "0.1.3"]
   [luminus-log4j "0.1.5"]
   [luminus/config "0.8"]

   ;; DB
   [org.xerial/sqlite-jdbc "3.8.11.2"]
   [migratus "0.8.28"]
   [conman "0.6.0"]

   ;; utils
   [compojure "1.5.1"]
   [bouncer "1.0.0"]
   [mount "0.1.10"]
   [cheshire "5.6.3"]
   [midje "1.8.3"]
   [buddy "0.10.0"]
   [clj-time "0.12.0"]]

  :min-lein-version "2.0.0"
  :uberjar-name "sempro.jar"
  :jvm-opts ["-server"]
  :resource-paths ["resources"]

  :main sempro.core
  :migratus {:store :database}

  :plugins [[lein-environ "1.0.1"]
            [migratus-lein "0.2.1"]
            [lein-cloverage "1.0.6"]]
  :profiles
  {:uberjar {:omit-source true
             :env {:production true}
             :aot :all
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}
   :dev           [:project/dev :profiles/dev]
   :test          [:project/test :profiles/test]
   :project/dev  {:dependencies [[prone "1.0.1"]
                                 [ring/ring-mock "0.3.0"]
                                 [ring/ring-devel "1.4.0"]
                                 [pjstadig/humane-test-output "0.7.1"]
                                 [mvxcvi/puget "1.0.0"]]
                  
                  
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]
                  ;;when :nrepl-port is set the application starts the nREPL server on load
                  :env {:dev        true
                        :port       3000
                        :nrepl-port 7000}}
   :project/test {:env {:test       true
                        :port       3001
                        :nrepl-port 7001}}
   :profiles/dev {}
   :profiles/test {}})
