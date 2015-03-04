(defproject com.shareablee/http-bolt "0.1.3"
  :description "A reusable Storm bolt for making arbitrary HTTP requests."
  :url "https://github.com/shareablee/http-bolt"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.9.2" :exclusions [cheshire]]]
  :profiles {:dev {:dependencies [[org.apache.storm/storm-core "0.9.3"]]}})
