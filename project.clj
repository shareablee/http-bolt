(defproject com.shareablee/http-bolt "0.1.2"
  :description "A reusable Storm bolt for making arbitrary HTTP requests."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-http "0.9.2" :exclusions [cheshire]]
                 [storm "0.9.0.1" :exclusions [org.apache.httpcomponents/httpclient]]])
