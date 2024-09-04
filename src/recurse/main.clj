(ns recurse.main
  (:require
   [integrant.core :as ig]
   [integrant.repl :as repl]
   [recurse.db]
   [recurse.handler]
   [ring.adapter.jetty :as jetty]))

(defmethod ig/init-key ::server [_ {:keys [handler] :as opts}]
  (jetty/run-jetty handler (dissoc opts :handler)))

(defmethod ig/halt-key! ::server [_ server]
  (.stop server))

(def config
  {:recurse.db/db {:filepath "db.edn"}
   :recurse.handler/handler {:db (ig/ref :recurse.db/db)}
   :recurse.main/server {:handler (ig/ref :recurse.handler/handler)
                         :port 4000
                         :join? false}})

(defn -main []
  (repl/set-prep! (constantly config))
  (repl/go))
