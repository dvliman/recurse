(ns recurse.handler
  (:require
   [recurse.db :as db]
   [clojure.walk :as walk]
   [integrant.core :as ig]
   [muuntaja.core]
   [reitit.coercion.malli :as malli]
   [reitit.dev.pretty :as pretty]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as ring-coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.util.response :as response]))

(defn handle-set [{:keys [db params]}]
  (let [data (:data db)]
    (response/response @(:data (db/write db (swap! data merge params))))))

(defn handle-get [{:keys [db params]}]
  (let [key (keyword (:key params))]
    (if-let [value (get @(:data db) key)]
      (response/response {key value})
      (response/not-found {key :not-found}))))

(def routes
  [["/set" {:get {:parameters
                  {:query [:fn {:error/message "params required"}
                           (fn [params] (and (map? params) (seq params)))]}
                  :handler handle-set}}]
   ["/get" {:get {:parameters
                  {:query [:and
                           [:map [:key string?]]
                           [:fn {:error/message "key required"}
                            (fn [{:keys [key]}] (seq key))]]}
                  :handler handle-get}}]])

(defmethod ig/init-key ::handler [_ opts]
  (ring/ring-handler
   (ring/router
    routes
    {:conflicts nil
     :exception pretty/exception
     :data {:coercion malli/coercion
            :muuntaja muuntaja.core/instance
            :middleware [parameters/parameters-middleware
                         (fn [handler]
                           (fn [request]
                             (handler (assoc request :db (:db opts)))))
                         (fn [handler]
                           (fn [request]
                             (handler (update request :params walk/keywordize-keys))))
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         muuntaja/format-request-middleware
                         ring-coercion/coerce-exceptions-middleware
                         ring-coercion/coerce-request-middleware
                         ring-coercion/coerce-response-middleware]}})
   (ring/routes
    (ring/redirect-trailing-slash-handler)
    (ring/create-default-handler))))
