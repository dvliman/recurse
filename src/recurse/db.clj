(ns recurse.db
  (:require
   [integrant.core :as ig]))

(defmethod ig/init-key ::db [_ _]
  (atom {}))

(defmethod ig/halt-key! ::db [_ conn]
  (reset! conn {}))
