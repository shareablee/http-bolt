(ns http-bolt.storm-test
  (:require [backtype.storm.testing :as st]
            [clj-http.client :as http]
            [clojure.test :refer :all]
            [http-bolt.storm :refer :all])
  (:import (backtype.storm.task IOutputCollector OutputCollector)
           (java.net SocketException SocketTimeoutException)
           (org.apache.http.conn ConnectTimeoutException)))

(deftype MockOutputCollector [state]
  IOutputCollector
  (ack
    [collector tuple]
    (swap! state conj [:ack]))
  (emit
    [collector stream-id anchors tuple]
    (swap! state conj [:emit stream-id tuple])
    '() ;; supposed to return a list of task-ids
    )
  (emitDirect
    [collector task-id stream-id anchors tuple]
    (swap! state conj [:emit-direct task-id stream-id tuple]))
  (fail
    [collector tuple]
    (swap! state conj [:fail])))

(defn mk-collector
  []
  (let [state (atom [])]
    [state (OutputCollector. (->MockOutputCollector state))]))

(defn invoke-bolt
  [conf tuple fields bolt-fn & args]
  (let [[state collector] (mk-collector)]
    (doto (if (seq args)
            (apply bolt-fn args)
            (bolt-fn))
      (.prepare conf nil collector)
      (.execute (st/test-tuple tuple :fields fields))
      (.cleanup))
    @state))

(defn invoke-http-bolt
  []
  (invoke-bolt
   {}
   [{:foo "bar"} "http://example.com" {}]
   ["meta" "url" "opts"]
   http-bolt*))

(deftest test-response
  (with-redefs [http/request (fn [req] {:status 418})]
    (let [state (invoke-http-bolt)]
      (is (= [[:emit
               "default"
               [{:foo "bar"}
                "response"
                {:headers {}, :status 418}]]
              [:ack]]
             state)))))

(deftest test-socket-error
  (with-redefs [http/request (fn [req] (throw (SocketException.)))]
    (let [state (invoke-http-bolt)]
      (is (= [[:emit
               "default"
               [{:foo "bar"} "socket_error" nil]]
              [:ack]]
             state)))))

(deftest test-socket-timeout
  (with-redefs [http/request (fn [req] (throw (SocketTimeoutException.)))]
    (let [state (invoke-http-bolt)]
      (is (= [[:emit
               "default"
               [{:foo "bar"} "socket_timeout" nil]]
              [:ack]]
             state)))))

(deftest test-connection-timeout
  (with-redefs [http/request (fn [req] (throw (ConnectTimeoutException.)))]
    (let [state (invoke-http-bolt)]
      (is (= [[:emit
               "default"
               [{:foo "bar"} "connection_timeout" nil]]
              [:ack]]
             state)))))
