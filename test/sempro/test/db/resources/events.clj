(ns sempro.test.db.resources.events)

(def ankneipe
  {:name        "Ankneipe"
   :description "Ankneipe der SS16"
   :date        "2016-04-02"})

(def abkneipe
  {:name "Abkneipe"
   :description "Abkneipe der SS16"
   :date "2016-08-08"})

(def all-events
  (merge ankneipe
         abkneipe
         ))
