import config
import tensorflow as tf
from tensorflow.keras.applications import VGG16
from tensorflow.keras.layers import Flatten
from tensorflow.keras.layers import Dense
from tensorflow.keras.layers import Input
from tensorflow.keras.models import Model
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.preprocessing.image import img_to_array
from tensorflow.keras.preprocessing.image import load_img
from sklearn.model_selection import train_test_split
import matplotlib.pyplot as plt
import numpy as np
import cv2
import os

#load the contents of the CSV annotations file
print("[INFO] loading dataset...")
rows = open(config.ANNOTS_PATH).read().strip().split("\n")

#initialise the list of data (images), our target output predictions
#(bounding box coordinations),along with the filenames of the
#individual images
data = []
targets = []
filenames = []

#loop over the rows
for row in rows[1:]:
    #break the row into the filename and bounding box coordinates
    row = row.split(",")
    (fileName, path, size_width, size_height, size_depth,class_name, x_min, y_min, x_max, y_max) = row
    
    # derive the path to the input image, load the image (in OpenCV format)
    # and grab  its dimensions
    imagePath = "../train_images/ProcessedImages/"+fileName
    #print(imagePath)
    image = cv2.imread(imagePath)
    print(image)
    (h,w) = image.shape[:2]

    #scale the bounding box coordinates relative to the spatial
    # dimensions of the input image
    x_min = float(x_min) / w
    y_min = float(y_min) / h
    x_max = float(x_min) / w
    y_max = float(y_max) / h

    #load the image and preprocess it
    image = load_img(imagePath, target_size=(256,256))
    image = img_to_array(image)

    #update out list of data, targets, and filenames
    data.append(image)
    targets.append((x_min, y_min, x_max,y_max))
    filenames.append(fileName)
    #end of the loop

#convert the data and the targets to numpy arrays, scaling the input
#pixel intensifies from the range [0, 255] to [0,1]
data = np.array(data, dtype="float32") / 255.0
targets = np.array(targets, dtype="float32")

#partition the data into training and testing splits using 90%
# of the data for training and the remaining 10% for testing
split = train_test_split(data, targets, filenames, test_size = 0.10, random_state = 42)

#unpack the data split
(trainImages, testImages) = split[:2]
(trainTargets, testTargets) = split[2:4]
(trainFilenames, testFilenames) = split[4:]

#write the testing filenames to disk so that we can use them
# when evaluating /testing our bounding box regressor 
print("[INFO] saving testing filenames...")
##f = open(config.TEST_FILENAMES, "w")
#f.write("\n".join(testFilenames))
#f.close()

# load the VGG16 network, ensuring the read FC layers are left off
vgg = VGG16(weights="imagenet", include_top = False, input_tensor=Input(shape=(256,256,3)))

#freeze all VGG layers so they will *not* be updated during the training process
vgg.trainable = False

#flatten the max-pooling output for VGG
flatten = vgg.output
flatten = Flatten()(flatten)

#construct a fully-connected layer header to output the predicted bounding box coordinates
bboxHead = Dense(128, activation="relu")(flatten)
bboxHead = Dense(64, activation="relu")(bboxHead)
bboxHead = Dense(32, activation="relu")(bboxHead)
bboxHead = Dense(4, activation="sigmoid")(bboxHead)

#construct the model we will fine-tune for bounding box regression
model = Model(inputs=vgg.input, outputs=bboxHead)

#initialise the optimiser, compile the model, and show the model summary
opt = Adam(lr=config.INIT_LR)
model.compile(loss="mse", optimizer=opt)
print(model.summary())

#train the network for bounding box regression
print("[INFO] training bounding box regressor...")
H = model.fit(trainImages, trainTargets, validation_data = (testImages, testTargets)
,  epochs=config.NUM_EPOCHS, verbose=1)

model.evaluate(testImages,testTargets)

#serialise the model to disk
print("[INFO] saving object detector model...")
model.save(config.MODEL_PATH, save_format = "h5")

#plot the model training history
N = config.NUM_EPOCHS
plt.style.use("ggplot")
plt.figure()
plt.plot(np.arange(0, N), H.history["loss"], label="train_loss")
plt.plot(np.arange(0, N), H.history["val_loss"], label="val_loss")
plt.title("Bounding Box Regression Loss on Training Set")
plt.xlabel("Epoch #")
plt.ylabel("Loss")
plt.legend(loc="lower left")
plt.savefig(config.PLOT_PATH)