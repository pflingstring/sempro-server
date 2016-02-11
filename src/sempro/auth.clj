(ns sempro.auth
  (:require
    [buddy.sign.jws :as jws]
    [buddy.core.keys :as keys]))

(def private-key (keys/private-key "ec-privatekey.pem"))
(def public-key  (keys/public-key  "ec-publickey.pem"))
(def sign-alg {:alg :es256})

(defn sign-token [user-id]
  "user-id must be a map
  signs the map and returns it as a string"
  (jws/sign user-id private-key sign-alg))

(defn unsign-token [token]
  "token must be a string
  unsigns the map and returns the original map"
  (jws/unsign token public-key sign-alg))
