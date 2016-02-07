(ns sempro.utils.test
  (:require
    [ring.mock.request :as m]
    ))

(def wrap-middlewares #(sempro.handler/app %))

(defn ignore-headers [request]
  "adds an 'IGNORE symbol for :headers"
  (assoc (wrap-middlewares request)
         :headers 'IGNORE))

(defn post-req [url body]
  (m/request :post url body))
