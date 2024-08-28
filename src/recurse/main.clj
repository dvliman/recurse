(ns recurse.main
  (:require
   [recurse.db]
   [recurse.handler]
   [integrant.core :as ig]
   [integrant.repl :as repl]
   [ring.adapter.jetty :as jetty]))

(defmethod ig/init-key ::server [_ {:keys [handler] :as opts}]
  (jetty/run-jetty handler (dissoc opts :handler)))

(defmethod ig/halt-key! ::server [_ server]
  (.stop server))

(def config
  {:recurse.db/db {}
   :recurse.handler/handler {:db (ig/ref :recurse.db/db)}
   :recurse.main/server {:handler (ig/ref :recurse.handler/handler)
                         :port 4000
                         :join? false}})

(defn -main []
  (repl/set-prep! (constantly config))
  (repl/go))
