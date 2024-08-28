(ns recurse.test-helper
  (:require
   [integrant.core :as ig]
   [ring.mock.request :as mock]
   [recurse.main :as main]))

(def system nil)

(def test-config (dissoc main/config :recurse.main/server))

(defn bootstrap []
  (prn "calling bootstrap")
  (alter-var-root #'system (constantly test-config))
  (alter-var-root #'system (fn [sys]
                             (ig/init sys))))

(defn run-request [request]
  (let [handler  (:recurse.handler/handler system)
        response (handler request)]
    response))

(defn handle-set [params]
  (-> (mock/request :get "/set")
      (mock/query-string params)
      run-request))

(defn handle-get [key]
  (-> (mock/request :get "/get")
      (mock/query-string {:key key})
      run-request))
