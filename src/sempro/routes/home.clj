(ns sempro.routes.home
  (:require [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [ok]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defroutes home-routes
  (GET "/" [] (response/ok "home"))
  (GET "/about" [] (response/ok "about")))
