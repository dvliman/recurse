(ns recurse.db
  (:require
   [integrant.core :as ig]
   [clojure.edn :as edn]
   [clojure.java.io :as io]))

(defmethod ig/init-key ::db [_ {:keys [filepath] :as opts}]
  {:filepath filepath
   :data (atom (edn/read-string (slurp (io/file filepath))))})

(defmethod ig/halt-key! ::db [_ _]
  {})

(defn write [db params]
  (spit (:filepath db) params)
  {:filepath (:filepath db)
   :data (atom params)})
