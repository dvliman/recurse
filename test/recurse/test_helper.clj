(ns recurse.test-helper
  (:require
   [integrant.core :as ig]
   [ring.mock.request :as mock]
   [recurse.main :as main]
   [clojure.walk :as walk]
   [cheshire.core :as json]))

(def ^:dynamic *system* nil)

(def ^:dynamic *test-config*
  (dissoc main/config :recurse.main/server))

(defn bootstrap [test]
  (binding [*test-config* (assoc-in *test-config* [:recurse.db/db :filepath]
                            (format "db-%s.edn" (random-uuid)))]
    (binding [*system* (ig/init *test-config*)]
      (test))))

(defn run-request [request]
  (let [handler  (:recurse.handler/handler *system*)
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
