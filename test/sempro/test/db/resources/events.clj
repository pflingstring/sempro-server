(ns sempro.test.db.resources.events)

(def ankneipe
  {:name        "Ankneipe"
   :description "Ankneipe der SS16"
   :date        "2016-04-02T20:15"})

(def abkneipe
  {:name "Abkneipe"
   :description "Abkneipe der SS16"
   :date "2016-08-08T20:15"})

(def kaput-date
  {:name "Impossible"
   :description "Not happening bro"
   :date "2010 12 1"})

(def all-events
  (merge ankneipe
         abkneipe
         ))
