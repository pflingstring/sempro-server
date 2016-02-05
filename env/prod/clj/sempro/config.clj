(ns sempro.config
  (:require
    [clojure.tools.logging :as log]
))

(def defaults
  {:init #(log/info "\n-=[sempro started successfully]=-")
   :middleware identity})
