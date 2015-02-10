(defproject com.shareablee/http-bolt "0.1.2"
  :description "A reusable Storm bolt for making arbitrary HTTP requests."
  :url "https://github.com/shareablee/http-bolt"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-http "0.9.2" :exclusions [cheshire]]
                 [storm "0.9.0.1" :exclusions [org.apache.httpcomponents/httpclient]]])
