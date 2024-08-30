(ns recurse.test-helper
  (:require
   [integrant.core :as ig]
   [ring.mock.request :as mock]
   [recurse.main :as main]
   [clojure.walk :as walk]
   [cheshire.core :as json]))

(defonce system nil)

(def test-config (dissoc main/config :recurse.main/server))

(defn bootstrap [test]
  (alter-var-root #'system (constantly test-config))
  (alter-var-root #'system (fn [sys]
                             (ig/init sys)))
  (test))

(defn run-request [request]
  (let [handler  (:recurse.handler/handler system)
        response (handler request)]
    (update response :body (comp walk/keywordize-keys json/decode slurp))))

(defn handle-set [params]
  (-> (mock/request :get "/set")
      (mock/query-string params)
      run-request))

(defn handle-get [key]
  (-> (mock/request :get "/get")
      (mock/query-string {:key key})
      run-request))
