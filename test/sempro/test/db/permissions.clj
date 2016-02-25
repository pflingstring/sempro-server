(ns sempro.test.db.permissions
  (:require
    [sempro.test.db.resources.events :as eres]
    [sempro.test.db.resources.users  :as ures]
    [sempro.utils.test  :as u]
    [sempro.models.user :as um]
    [clojure.java.jdbc  :as jdbc]
    [sempro.db.core     :as db]
    [midje.sweet :refer :all]
  ))

(conman.core/with-transaction [t-conn db/conn]
  (jdbc/db-set-rollback-only! t-conn)

  (let [rand  (second (um/create ures/user-rand))
        harry (second (um/create ures/user-harry))
        gimli (second (um/create ures/gimli-user))
        rand-token  (u/get-token {:email (:email rand)  :pass "elmindreda"})
        harry-token (u/get-token {:email (:email harry) :pass "expeliarmus"})
        gimli-token (u/get-token {:email (:email gimli) :pass "mithril"})
        rand-get  (u/create-request rand-token  :get)
        harry-get (u/create-request harry-token :get)
        gimli-get (u/create-request gimli-token :get)
        rand-post  (u/create-request rand-token  :post)
        harry-post (u/create-request harry-token :post)
        gimli-post (u/create-request gimli-token :post)]

    (fact "..:: PERMISSIONS ::.."
      (let [ankneipe (merge {:id 1} eres/ankneipe {:info ""}
                            {:can_read  (:email rand) :can_write (:email rand)})
            abkneipe (merge {:id 2} eres/abkneipe {:info ""}
                            {:can_read  (:email harry) :can_write (:email harry)})
            party    (merge {:id 3} eres/party {:info ""}
                            {:can_read  (:email gimli) :can_write (:email gimli)})]

        (fact "create events"
          (rand-post  "/events" eres/ankneipe)
          (harry-post "/events" eres/abkneipe)
          (gimli-post "/events" eres/party)

          (fact "everyone has acces to own event only"
            (rand-get "/events/1")  => (u/ok-response ankneipe)
            (harry-get "/events/2") => (u/ok-response abkneipe)
            (gimli-get "/events/3") => (u/ok-response party)

            (fact "only rand has access"
              (harry-get "/events/1") => (u/access-denied "Access to /events/1 is not authorized")
              (gimli-get "/events/1") => (u/access-denied "Access to /events/1 is not authorized"))

            (fact "only harry has access"
              (rand-get  "/events/2") => (u/access-denied "Access to /events/2 is not authorized")
              (gimli-get "/events/2") => (u/access-denied "Access to /events/2 is not authorized"))

            (fact "only gimli has access"
              (rand-get  "/events/3") => (u/access-denied "Access to /events/3 is not authorized")
              (harry-get "/events/3") => (u/access-denied "Access to /events/3 is not authorized"))))


        (fact "peremission manipulation"
          (fact "rand gives read permissions"
            (let [ankneipe (->> #(str % " " (:email gimli) " " (:email harry))
                                 (update ankneipe :can_read))]

              (rand-post "/events/1/permissions/add"
                         {:readers (str (:email gimli) " " (:email harry))
                          :writers ""}) => (u/ok-response {:added true})

              (harry-get "/events/1") => (u/ok-response ankneipe)
              (gimli-get "/events/1") => (u/ok-response ankneipe)
              (harry-post "/events/1/delete" nil) => (u/access-denied "Access to /events/1/delete is not authorized")
              (gimli-post "/events/1/delete" nil) => (u/access-denied "Access to /events/1/delete is not authorized")))

          (fact "harry gives read permissions"
            (let [abkneipe (->> #(str % " " (:email rand) " " (:email gimli))
                                 (update abkneipe :can_read))]

              (harry-post "/events/2/permissions/add"
                          {:readers (str (:email rand) " " (:email gimli))
                           :writers ""}) => (u/ok-response {:added true})

              (rand-get  "/events/2") => (u/ok-response abkneipe)
              (gimli-get "/events/2") => (u/ok-response abkneipe)
              (rand-post  "/events/2/delete" nil) => (u/access-denied "Access to /events/2/delete is not authorized")
              (gimli-post "/events/2/delete" nil) => (u/access-denied "Access to /events/2/delete is not authorized")
              (rand-post  "/events/2/update" ankneipe) => (u/access-denied "Access to /events/2/update is not authorized")
              (gimli-post "/events/2/update" ankneipe) => (u/access-denied "Access to /events/2/update is not authorized")))

          (fact "gimli gives read permissions"
            (let [party (->> #(str % " " (:email rand) " " (:email harry))
                              (update party :can_read))]

              (gimli-post "/events/3/permissions/add"
                          {:readers (str (:email rand) " " (:email harry))
                           :writers ""}) => (u/ok-response {:added true})

              (rand-get  "/events/3") => (u/ok-response party)
              (harry-get "/events/3") => (u/ok-response party)
              (rand-post  "/events/3/delete" nil) => (u/access-denied "Access to /events/3/delete is not authorized")
              (harry-post "/events/3/delete" nil) => (u/access-denied "Access to /events/3/delete is not authorized")
              (rand-post  "/events/3/update" ankneipe) => (u/access-denied "Access to /events/3/update is not authorized")
              (harry-post "/events/3/update" ankneipe) => (u/access-denied "Access to /events/3/update is not authorized")))
          ))
  )))
