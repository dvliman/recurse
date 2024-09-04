(ns recurse.db
  (:require
   [integrant.core :as ig]
   [clojure.edn :as edn]
   [clojure.java.io :as io]))

(defmethod ig/init-key ::db [_ {:keys [filepath] :as opts}]
  {:filepath filepath
   :data (atom (into {}
                 (comp (partition-all 2)
                   (map (fn [[k v]]
                          [(edn/read-string k)
                           (edn/read-string v)])))
                 (try
                   (line-seq (io/reader filepath))
                   (catch Exception _ []))))})

(defmethod ig/halt-key! ::db [_ _]
  {})

(defn write [db params]
  (with-open [out (io/writer (:filepath db) :append true)]
    (doseq [[k v] params]
      (.write out (prn-str k))
      (.write out (prn-str v))))
  (swap! (:data db) merge params)
  db)
