(ns recurse.handler-test
  (:require
   [clojure.test :refer [deftest is testing use-fixtures]]
   [recurse.test-helper :as th]))

(use-fixtures :each th/bootstrap)

(deftest handle-get-set
  (testing "key required"
    (let [response (th/handle-get "")]
      (is (= 400 (:status response)))
      (is (= ["key required"] (-> response :body :humanized)))))

  (testing "key not found"
    (let [key (str (random-uuid))
          response (th/handle-get key)]
      (is (= 404 (:status response)))
      (is (= {(keyword key) "not-found"} (:body response)))))

  (testing "set nothing"
    (let [response (th/handle-set {})]
      (is (= 400 (:status response)))
      (is (= ["params required"] (-> response :body :humanized))))))

(deftest set-after-get
  (testing "get what you set"
    (let [key   (str (random-uuid))
          value (str (random-uuid))]
      (is (= 200 (:status (th/handle-set {key value}))))
      (is (= {(keyword key) value} (:body (th/handle-get key))))))

  (testing "set twice, should overwrite"
    (let [key (str (random-uuid))
          value1 (str (random-uuid))
          value2 (str (random-uuid))]
      (is (= 200 (:status (th/handle-set {key value1}))))
      (is (= {(keyword key) value1} (:body (th/handle-get key))))
      (is (= 200 (:status (th/handle-set {key value2}))))
      (is (= {(keyword key) value2} (:body (th/handle-get key)))))))
