(ns sempro.env
  (:require
    [clojure.tools.logging :as log]
    [sempro.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init (fn [] (log/info "\n-=[sempro started successfully using the development profile]=-"))
   :stop (fn [] (log/info "\n-=[sempro has shut down successfully]=-"))
   :middleware wrap-dev})
