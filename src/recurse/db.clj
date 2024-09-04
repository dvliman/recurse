(ns recurse.db
  (:require
   [integrant.core :as ig]
   [clojure.edn :as edn]
   [clojure.java.io :as io]))

(defmethod ig/init-key ::db [_ {:keys [filepath] :as opts}]
  (let [data (into {}
                   (comp (partition-all 2)
                         (map (fn [[k v]]
                                [(edn/read-string k)
                                 (edn/read-string v)])))
                   (try
                     (line-seq (io/reader filepath))
                     (catch Exception _ [])))
        backup-filepath    (str filepath ".backup")
        compacted-filepath (str filepath ".compact")]
    (with-open [out (io/writer compacted-filepath)]
      (doseq [[k v] data]
        (.write out (prn-str k))
        (.write out (prn-str v))))
    (.renameTo (io/file filepath) (io/file backup-filepath))
    (.renameTo (io/file compacted-filepath) (io/file filepath))
    {:filepath filepath
     :data (atom data)}))

(defmethod ig/halt-key! ::db [_ _]
  {})

(defn write [db params]
  (with-open [out (io/writer (:filepath db) :append true)]
    (doseq [[k v] params]
      (.write out (prn-str k))
      (.write out (prn-str v))))
  (swap! (:data db) merge params)
  db)
