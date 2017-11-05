(ns ads-txt-crawler.crawl
  (:require [ads-txt-crawler.domains :as d]
            [ads-txt-crawler.database :as data]
            [ads-txt-crawler.process :as p]))

(defn print-record [domain record]
  (let [{:keys [exchange-domain account-id account-type tag-id]} record]
    (println (format "%s,%s,%s,%s,%s" domain exchange-domain account-id account-type tag-id))))

(defn crawl
  "Crawl a list of domains and process them with the output-fn.
  The output-fn should accept a single parameter which is the return map from process/process-to-map

  The domains list can be generated from a file from the domains/domains function.
  Domains from other sources should be pre-processed with domains/clean-domain-name."
  [domains output-fnc]
  (doseq [d domains]
    (let [data (p/process-to-map d)]
      (if-let [err (:error data)]
        (.println *err* (:message data))
        (output-fnc data)))))

(defn print-results
  "Print the results to stdout"
  [data]
  (let [domain (:domain data)
        records (:records data)]
    (doseq [record records]
      (print-record domain record))))

(defn save-results
  "Returns a function that can be called from crawl to save the results to a database."
  [dbname]
  (fn
    [data]
    (let [domain (:domain data)
          records (:records data)]
      (doseq [record records]
        (data/save-record domain record dbname)))))
