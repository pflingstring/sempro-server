(ns sempro.test.db.resources.users)

(def user-harry
  {:first_name "Harry"
   :last_name  "Potter"
   :email "hurry@hogwards.express"
   :pass  "expeliarmus"
   :role nil
   :phone nil})

(def user-rand
  {:first_name "Rand"
   :id 5
   :last_name  "al Thor"
   :email "dragon@reborn.com"
   :pass "elmindreda"
   :role nil
   :phone nil})

(def gimli-user
  {:first_name "Gimli"
   :last_name "son of Gloin"
   :email "gimli@my.axe"
   :pass "mithril"
   :role nil
   :phone nil})

(def kaput-email
  {:first_name "Helmut"
   :last_name  "Kaput"
   :email "kaput@email"
   :pass  "kaputemail"})

(def kaput-name
  {:first_name "ArtooDetoo"
   :last_name  "R2-D2"
   :email "beep@leep.com"
   :pass "superencryption"})

(def kaput-pass
  {:first_name "Noob"
   :last_name  "Lol"
   :email "pass@word.me"
   :pass "short"})

(def hashed-password-user
  {:first_name "Hash"
   :last_name "Pass"
   :email "hash@my.mail"
   :pass "HASHmyPASSword"})
