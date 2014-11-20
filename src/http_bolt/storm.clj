(ns http-bolt.storm
  (:require [backtype.storm.clojure :as storm]
            [backtype.storm.log :as sl]
            [clj-http.client :as http]
            [shareablee.collection.map :as cm]
            [shareablee.collection.utils :as cu]
            [http-bolt.fields :as fields])
  (:import (java.net SocketException SocketTimeoutException)
           (org.apache.http.conn ConnectTimeoutException)))

(defn mk-req
  [tuple conf]
  (let [tuple-map (cm/map-keys-1 keyword tuple)]
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
     (let [[res elapsed-ms] (cu/capture-time (http/request (mk-req tuple conf)))]
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
