(ns sempro.config
  (:require [clojure.tools.logging :as log]
            [sempro.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init #(log/info "\n-=[sempro started successfully using the development profile]=-")
   :middleware wrap-dev})
