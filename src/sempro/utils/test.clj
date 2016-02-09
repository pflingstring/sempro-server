(ns sempro.utils.test
  (:require
    [ring.mock.request :as m]
    ))

(def wrap-middlewares #(sempro.handler/app %))

(defn ignore-key [data key]
  (assoc data key 'IGNORE))

(defn ignore-headers [request]
  "adds an 'IGNORE symbol for :headers"
  (ignore-key (wrap-middlewares request)
              :headers))

(defn post-req [url body]
  (m/request :post url body))
