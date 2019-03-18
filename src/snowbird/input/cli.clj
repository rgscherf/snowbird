(ns snowbird.input.cli
  (:require [clojure.tools.cli :as cli]
            [clojure.string :as string]))

(def bluebird-cli-opts
  [["-s" "--search-path PATH" "Relative or absolute path to source directory. If using this option, you must also set FILETYPE"]
   ["-r" "--ruleset RULES" "xml rules file to use for FILETYPE."
    :validate [#(string/ends-with? % ".xml")]]
   ["-f" "--file-type FILETYPE" "Filetype to check. Want to check more than one filetype at once? Look into using a bluebird_config.edn file!"
    :parse-fn keyword
    :validate [#{:apex :js}]]])

(defn conform-cli-config
  [{:keys [search-path ruleset file-type]}]
  (merge
    {}
    (if file-type {:file-types [file-type]} nil)
    (if search-path {:file-search-path search-path} nil)
    (if (and file-type ruleset)
      {:pmd-rules {file-type ruleset}} nil)))

(defn config-from-cli-args
  [args]
  (let [in-conf (cli/parse-opts args bluebird-cli-opts)]
    (if (:errors in-conf)
      (->> (:errors in-conf) (string/join "\n") Exception. throw)
      (conform-cli-config (:options in-conf)))))


