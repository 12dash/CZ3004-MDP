from tensorflow.keras.preprocessing.image import img_to_array
from tensorflow.keras.preprocessing.image import load_img
from tensorflow.keras.models import load_model
import numpy as np
import mimetypes
import argparse
import imutils
import pickle
import cv2
import os

def makePrediction(img):
    model = load_model("output/detector.h5")
    lb = pickle.loads(open("output/lb.pickle", "rb").read())

    img_temp = img.astype(np.uint8)

    img = img / 255.0
    img = np.expand_dims(img, axis=0)

    (boxPreds, labelPreds) = model.predict(img)
    (startX, startY, endX, endY) = boxPreds[0]

    i = np.argmax(labelPreds, axis=1)
    print(labelPreds[i])
    label = lb.classes_[i][0]

    image = img_temp
    (h, w) = image.shape[:2]
    startX = int(startX * w)
    startY = int(startY * h)
    endX = int(endX * w)
    endY = int(endY * h)
    y = startY - 10 if startY - 10 > 10 else startY + 10
    cv2.putText(image, label, (startX, y), cv2.FONT_HERSHEY_SIMPLEX,0.65, (0, 255, 0), 2)
    cv2.rectangle(image, (startX, startY), (endX, endY),(0, 255, 0), 2)
    cv2.imwrite("Output.jpg", image)

    return label


