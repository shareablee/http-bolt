(defproject http-bolt "0.1.0-SNAPSHOT"
  :description "A bolt for making arbitrary HTTP requests."
  :dependencies [[com.shareablee/collection "0.1.5"]
                 [org.clojure/clojure "1.4.0"]
                 [clj-http "0.9.2" :exclusions [cheshire]]
                 [storm "0.9.0.1" :exclusions [org.apache.httpcomponents/httpclient]]]
  :plugins [[s3-wagon-private "1.1.2"]]
  :repositories [["releases"
                  {:url "s3p://shareablee-jar-repo/releases"
                   :username :env/shareablee_aws_access_key
                   :passphrase :env/shareablee_aws_secret_access_key
                   :sign-releases false}]
                 ["snapshots"
                  {:url "s3p://shareablee-jar-repo/snapshots"
                   :username :env/shareablee_aws_access_key
                   :passphrase :env/shareablee_aws_secret_access_key}]])
