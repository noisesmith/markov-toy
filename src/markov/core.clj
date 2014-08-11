(ns markov.core
  (:require [clojure.pprint :as pprint :refer [pprint]]
            [clojure.java.io :as io])
  (:gen-class))

(defn gen-chain
  [base text memory]
  (reduce
   (fn [m ngram]
     (update-in m [(butlast ngram) (last ngram)] (fnil inc 0)))
   base
   (partition (inc memory) 1 (concat [:start] text [:stop]))))

(defn pick
  [probs random]
  (when probs
    (let [total (apply + (vals probs))
          selector (.nextInt random total)]
      (reduce
       (fn [selector [token chance]]
         (if (< selector chance)
           (reduced token)
           (- selector chance)))
       selector
       probs))))

(defn gen-text*
  [markov start random]
  (let [next (pick (get markov start) random)]
    (if (contains? #{nil :stop} next)
      nil
      (cons next
            (lazy-seq
             (gen-text* markov (concat (rest start) [next]) random))))))

(defn gen-text
  [markov start random]
  (apply str (concat (rest start) (gen-text* markov start random))))

(defn -main
  "generates a character-wise markov chain with memory 1 from an input file or
   the Gettysburg address"
  [& [seed memory & input]]
  (let [input (or input [(io/resource "gettysburg.txt")])
        memory (Long. (or memory "1"))
        markov (reduce #(gen-chain % %2 memory)
                       {}
                       (map slurp input))
        seed (try (Long. seed)
                  (catch Exception e (.getTime (java.util.Date.))))
        random (java.util.Random. seed)
        starts (filter #(= [:start]
                           (take 1 %))
                       (keys markov))
        start-index (.nextInt random (count starts))
        start (nth starts start-index)]
    ;; (pprint markov)
    (println (gen-text markov start random))))
