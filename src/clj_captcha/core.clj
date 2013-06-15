(ns clj-captcha.core
  (:import java.io.ByteArrayOutputStream
           java.awt.image.BufferedImage
           javax.imageio.ImageIO
           [com.octo.captcha.service.image
            ImageCaptchaService
            DefaultManageableImageCaptchaService])
  (:require [noir.request :refer [*request*]]))

(def ^:dynamic *image-captcha-service-instance*
  (delay (DefaultManageableImageCaptchaService.)))

(defn set-image-captcha-service-instance! [inst]
  (alter-var-root #'*image-captcha-service-instance* (constantly inst)))

(defn- image-captcha-service []
  (force *image-captcha-service-instance*))

(defn- session-id []
  (get-in *request* [:cookies "ring-session" :value]))

(defn captcha-challenge-as-jpeg
  ([]
    (captcha-challenge-as-jpeg (session-id)))
  ([captcha-id]
    (captcha-challenge-as-jpeg (image-captcha-service) captcha-id))
  ([^ImageCaptchaService image-captcha-service captcha-id]
    (let [jpeg-outputstream (ByteArrayOutputStream.)
          challenge (.getImageChallengeForID image-captcha-service captcha-id)]
      (ImageIO/write challenge "jpeg" jpeg-outputstream)
      (.toByteArray jpeg-outputstream))))

(defn captcha-response-correc?
  ([]
    (captcha-response-correc? (get-in *request* [:params :captcha])))
  ([captcha-response]
    (captcha-response-correc? (session-id) captcha-response))
  ([captcha-id captcha-response]
    (captcha-response-correc?
      (image-captcha-service) captcha-id captcha-response))
  ([^ImageCaptchaService image-captcha-service captcha-id captcha-response]
    (.validateResponseForID image-captcha-service captcha-id captcha-response)))
