(ns sempro.utils.error)

(def error-body #(assoc {} :error %))

(defn make-error
  [type]
  #(-> (assoc {} type %)
       (error-body)))

(def not-found     (make-error :not-found))
(def sql-exception (make-error :exception))
(def access-denied (make-error :not-authorized))
(def input-error   (make-error :input-validation))
