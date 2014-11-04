(ns http-bolt.storm-test
  (:require [backtype.storm.clojure :as storm]
            [backtype.storm.testing :as st]
            [clojure.test :refer [deftest is testing]]
            [clj-http.client :as http]
            [http-bolt.storm :refer [http-bolt]])
  (:import (java.net SocketException SocketTimeoutException)))

(storm/defspout mock-spout
  ["meta" "url" "opts"]
  [conf context collector]
  nil)

(defn mock-topology
  []
  (storm/topology
   {"mock-spout" (storm/spout-spec mock-spout)}
   {"http" (storm/bolt-spec {"mock-spout" :shuffle} http-bolt)}))

(defn mock-tuples
  []
  {"mock-spout" [[{:foo "bar"} "http://example.com" {}]]})

(deftest test-response
  (testing "A SocketException is thrown"
    (st/with-simulated-time-local-cluster [cluster]
      (with-redefs [http/request (fn [req] {:status 418})]
        (let [r (st/complete-topology cluster (mock-topology) :mock-sources (mock-tuples))]
          (is (st/ms= [[{:foo "bar"} "http://example.com" {}]]
                      (st/read-tuples r "mock-spout")))
          (is (st/ms= [[{:foo "bar"} "response" {:status 418}]]
                      (st/read-tuples r "http"))))))))

(deftest test-socket-error
  (testing "A SocketException is thrown"
    (st/with-simulated-time-local-cluster [cluster]
      (with-redefs [http/request (fn [req] (throw (SocketException.)))]
        (let [r (st/complete-topology cluster (mock-topology) :mock-sources (mock-tuples))]
          (is (st/ms= [[{:foo "bar"} "http://example.com" {}]]
                      (st/read-tuples r "mock-spout")))
          (is (st/ms= [[{:foo "bar"} "socket_error" nil]]
                      (st/read-tuples r "http"))))))))

(deftest test-socket-timeout
  (testing "A SocketException is thrown"
    (st/with-simulated-time-local-cluster [cluster]
      (with-redefs [http/request (fn [req] (throw (SocketTimeoutException.)))]
        (let [r (st/complete-topology cluster (mock-topology) :mock-sources (mock-tuples))]
          (is (st/ms= [[{:foo "bar"} "http://example.com" {}]]
                      (st/read-tuples r "mock-spout")))
          (is (st/ms= [[{:foo "bar"} "socket_timeout" nil]]
                      (st/read-tuples r "http"))))))))
