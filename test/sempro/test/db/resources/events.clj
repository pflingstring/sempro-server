(ns sempro.test.db.resources.events)

;; valid events
(def ankneipe
  {:name        "Ankneipe"
   :description "Ankneipe der SS16"
   :date        "2016-04-02T20:15"})

(def abkneipe
  {:name "Abkneipe"
   :description "Abkneipe der SS16"
   :date "2016-08-08T20:15"})

(def party
  {:name "All you need is love"
   :description "and dates that pass the tests"
   :date "2015-10-10T10:10"})


;; invalid events
(def kaput-date
  {:name "Impossible"
   :description "Not happening bro"
   :date "2010 12 1"})

(def kaput-name
  {:description "Good description but no name"
   :date "2010-11-30T11:33"})

(def kaput-description
  {:name "A name is no good without description"
   :date "2000-11-01T22:22"})
