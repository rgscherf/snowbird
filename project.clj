(defproject snowbird "0.2.14"
  :description "Core library for configurable static analysis as a service (CSAAAS)."
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [commons-io "2.6"]
                 [expound "0.7.2"]
                 [clj-http "3.9.1"]
                 [instaparse "1.4.10"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.clojure/tools.cli "0.4.1"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/data.csv "0.1.4"]]

  :main snowbird.core
  :aot [snowbird.core]
  :repl-options {:init-ns snowbird.core})
