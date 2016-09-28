(ns sempro.layout
  (:require [selmer.parser :as parser]
            [ring.util.http-response :refer [content-type ok]]))

(declare ^:dynamic *app-context*)
(parser/set-resource-path!  (clojure.java.io/resource "templates"))

(defn render
  "renders the HTML template located relative to resources/templates"
  [template & [params]]
  (content-type
    (ok
      (parser/render-file
        template
        (assoc params
          :page template)))
    "text/html; charset=utf-8"))
