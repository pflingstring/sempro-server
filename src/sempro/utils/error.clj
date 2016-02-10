(ns sempro.utils.error)

(def error-body
  #(assoc {} :error %))

(def input-error
  #(-> (assoc {} :input-validation %)
                      (error-body)))

(def sql-exception
  #(-> (assoc {} :exception %)
       (error-body)))
