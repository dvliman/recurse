(ns recurse.handler-test
  (:require
   [recurse.test-helper :as th]
   [clojure.test :refer [deftest is use-fixtures testing]]))

(use-fixtures :once th/bootstrap)

(deftest handle-get-test
  (testing "key required"
    #_(is true)
    (prn (th/handle-get ""))))
