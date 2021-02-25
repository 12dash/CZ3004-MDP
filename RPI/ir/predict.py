import config
from tensorflow.keras.preprocessing.image import img_to_array
from tensorflow.keras.preprocessing.image import load_img
from tensorflow.keras.models import load_model
import numpy as np
import mimetypes
import argparse
import imutils
import cv2
import os

#construct the argument parser and parse the arguments
ap = argparse.ArgumentParser()
ap.add_argument("-i", "--input", required=True, help="path to input image/text file of image filenames")
args = vars(ap.parse_args())

# determine the input file type , but asssume that we're working with single input image
filetype = mimetypes.guess_type(args["input"])[0]
imgPaths = [args["input"]]

#if the file type is a text file, then we need to process *mutiple* images
if "text/plain" == filetype:
    #load the filenames in our testing file and initialise our list of image paths
    filenames = open(args["input"]).read().stip().split("\n")
    imgPaths = []

    #loop over the filenames 
    for f in filenames:
        #construct the full path to the image filename and then update our image path list
        p = os.path.sep.join([config.IMAGES_PATH, f])
        imgPaths.append(p)

#load our trained bounding box regressor from disk
print("[INFO] loading object detector...")
model = load_model(config.MODEL_PATH)

#loop over the images that we will be testing using our bounding box regression model
for imgpath in imgPaths:
    #load the input image (in keras format) from disk and preprocess
    #it, scaling the pixel intensifies to the range [0,1]
    image = load_img(imgpath, target_size = (256,256))
    print(img_to_array(image)) # This will be the input from RPI
    image = img_to_array(image) /255.0
    image = np.expand_dims(image, axis=0)

    #make bounding box predictions on the input image
    preds = model.predict(image)[0]
    (x_min, y_min, x_max, y_max) = preds

    #load the input image (in opencv format), rezise it such that it fits in out main screen, and grab its dimenstions
    image = cv2.imread(imgpath)
    image = imutils.resize(image, width = 600)
    (h,w) = image.shape[:2]

    #scale the predicted bounding box coordinates based on the image dimensions
    x_min = int(x_min * w)
    y_min = int(y_min * h)
    x_max = int(x_max * w)
    y_max = int(y_max * h)

    #draw the predicted bounding box on the image
    cv2.rectangle(image, (x_min,y_min), (x_max, y_max), (0, 255, 0), 2)

    #show the output image
    cv2.imshow("Output", image)
    cv2.waitKey(0)