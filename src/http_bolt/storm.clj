(ns http-bolt.storm
  (:require [backtype.storm.clojure :as storm]
            [backtype.storm.log :as sl]
            [clj-http.client :as http]
            [http-bolt.fields :as fields])
  (:import (java.net SocketException SocketTimeoutException)
           (org.apache.http.conn ConnectTimeoutException)))


(defn map-keys-1
  "Transforms the top level keys in the given map to keywords, unless
  you provide f, then that function will be used as the transformer."
  [f m]
  (reduce-kv #(assoc %1 (f %2) %3) {} m))

(defn capture-time-fn
  "Returns a tuple of how much time it took (in ms) for the thunk to
  execute, and the return value of the function."
  [f]
  (let [start (System/currentTimeMillis)]
    [(f) (/ (double (- (System/currentTimeMillis) start)) 1000.0)]))

(defmacro capture-time
  "Returns a tuple of how much time it took (in ms) the code to
  execute and the value of the last expression."
  [& body]
  `(capture-time-fn (fn [] ~@body)))

(defn mk-req
  [tuple conf]
  (let [tuple-map (map-keys-1 keyword tuple)]
    (merge {:method :get} ;; overwritable defaults
           (:opts tuple-map)
           {:url (:url tuple-map)
            :throw-exceptions? false
            :socket-timeout (get conf "HTTP_BOLT_SOCKET_TIMEOUT" 10000)
            :conn-timeout (get conf "HTTP_BOLT_SOCKET_ERROR" 10000)})))

(storm/defbolt http-bolt
  fields/out-fields
  {:prepare true}
  [conf context collector]
  (storm/bolt-execute
   [tuple]
   (try
     ;; Converting the headers from a HeaderMap object to a clojure
     ;; hashmap so storm does not throw a serialization error
     (let [[http-res elapsed-ms] (capture-time (http/request (mk-req tuple conf)))
           res (update-in http-res [:headers] (partial into {}))]
       (sl/log-message "HTTP Bolt took " elapsed-ms "ms.")
       (storm/emit-bolt! collector [(:meta tuple) "response" res] :anchor tuple)
       (storm/ack! collector tuple))
     (catch ConnectTimeoutException e
       (sl/log-error e "HTTP Bolt caught ConnectTimeoutException (see below).")
       (storm/emit-bolt! collector [(:meta tuple) "connection_timeout" nil] :anchor tuple)
       (storm/ack! collector tuple))
     (catch SocketTimeoutException e
       (sl/log-error e "HTTP Bolt caught SocketTimeoutException (see below).")
       (storm/emit-bolt! collector [(:meta tuple) "socket_timeout" nil] :anchor tuple)
       (storm/ack! collector tuple))
     (catch SocketException e
       (sl/log-error e "HTTP Bolt caught SocketException (see below).")
       (storm/emit-bolt! collector [(:meta tuple) "socket_error" nil] :anchor tuple)
       (storm/ack! collector tuple)))))
