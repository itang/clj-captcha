(ns clj-captcha.core-test
  (:use clojure.test
        clj-captcha.core))

(def session-id "someid")

(defn true-or-false? [x]
  (or (true? x) (false? x)))

(deftest captcha-challenge-as-jpeg-test
  (is (not (nil? (captcha-challenge-as-jpeg session-id)))))

(deftest captcha-response-correc?-test
  (is (true-or-false? (captcha-response-correc? session-id "Xssdssd"))))
