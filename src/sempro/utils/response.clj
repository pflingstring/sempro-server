(ns sempro.utils.response
  (:require
    [ring.util.http-response :as response]
    [cheshire.core :as json]))

(defn to-json
  [string]
  (json/generate-string string))

(defn create-response
  "json should be a map
  response-type should be one response
  from ring.util.http-request"
  [response-type json ]
  (ring.util.response/content-type
    (response-type (to-json json))
    "application/json"))