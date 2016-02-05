(ns sempro.routes.home
  (:require [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [cheshire.core :as json]
))

(defn to-json
  [string]
  (json/generate-string string))

(defn create-response
  "json should be a map"
  [json]
  (ring.util.response/content-type
    (response/ok (to-json json))
    "application/json"))

(defroutes home-routes
  (GET "/" [] (create-response {:body "home"}))
  (GET "/about" [] (create-response {:message "about"})))
