(ns sempro.core
  (:require
    [luminus.repl-server :as repl]
    [luminus.http-server :as http]
    [mount.core :as mount]
    [clojure.tools.logging :as log]
    [clojure.tools.cli :refer [parse-opts]]
    [sempro.config :refer [env]]
    [sempro.handler :refer [app]]
    [sempro.db.migrations :as migrations])
  (:gen-class))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(mount/defstate ^{:on-reload :noop}
                http-server
                :start
                (http/start
                  (-> env
                      (assoc :handler app)
                      (update :port #(or (-> env :options :port) %))))
                :stop
                (http/stop http-server))

(mount/defstate ^{:on-reload :noop}
                repl-server
                :start
                (when-let [nrepl-port (env :nrepl-port)]
                  (repl/start {:port nrepl-port}))
                :stop
                (when repl-server
                  (repl/stop repl-server)))

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (cond
    (some #{"migrate" "rollback"} args)
    (do
      (mount/start #'sempro.config/env)
      (migrations/migrate args (select-keys env [:database-url]))
      (System/exit 0))
    :else
    (start-app args)))
