(defproject sempro "0.1.0-SNAPSHOT"

  :description "Semesterprogramm Manager"
  :url "http://fuxenbesteck.de"

  :dependencies
  [
   ;; core
   [org.clojure/tools.logging "0.3.1"]
   [org.clojure/clojure "1.8.0"]

   ;; ring
   [ring "1.4.0" :exclusions [ring/ring-jetty-adapter]]
   [metosin/ring-http-response "0.6.5"]
   [ring-middleware-format "0.7.0"]
   [ring/ring-defaults "0.1.5"]

   ;; luminus
   [luminus-http-kit "0.1.1"]
   [luminus-nrepl "0.1.2"]
   [luminus-log4j "0.1.2"]
   [luminus/config "0.5"]

   ;; DB
   [org.xerial/sqlite-jdbc "3.8.11.1"]
   [migratus "0.8.9"]
   [conman "0.3.0"]

   ;; utils
   [compojure "1.4.0"]
   [bouncer "1.0.0"]
   [mount "0.1.8"]
   [cheshire "5.5.0"]
  ]

  :min-lein-version "2.0.0"
  :uberjar-name "sempro.jar"
  :jvm-opts ["-server"]
  :resource-paths ["resources"]

  :main sempro.core
  :migratus {:store :database}

  :plugins [[lein-environ "1.0.1"]
            [migratus-lein "0.2.1"]]
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
