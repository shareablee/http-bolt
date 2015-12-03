(defproject com.shareablee/http-bolt "0.1.5"
  :description "A reusable Storm bolt for making arbitrary HTTP requests."
  :url "https://github.com/shareablee/http-bolt"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-http "2.0.0"]]
  :profiles {:0.10.x
             {:dependencies [[org.apache.storm/storm-core "0.10.0"]
                             [org.clojure/clojure "1.6.0"]]}
             :0.9.x
             {:dependencies [[org.apache.storm/storm-core "0.9.5"]
                             [org.clojure/clojure "1.5.1"]]}})
