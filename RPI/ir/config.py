import os

#define the base path to the input dataset and then use it to derive
# the path to the images directory and annotation xml file
BASE_PATH = "../train_images"
IMAGES_PATH = os.path.sep.join([BASE_PATH, "ProcssedImages"])
#path containing bounding box annotations in xml
ANNOTS_PATH = os.path.sep.join(["./","Annotations.csv"])

#define the path to the base output directory
BASE_OUTPUT = "output"

#define the path to the output serialised model, model trainin plot
# and testing image filenames
MODEL_PATH = os.path.sep.join([BASE_OUTPUT, "detector.h5"])
PLOT_PATH = os.path.sep.join([BASE_OUTPUT, "plot.png"])
# a text file of image filenames selected for our testing set
TEST_FILENAMES = os.path.sep.join([BASE_OUTPUT, "test_images.txt"])

#initialise our initial  learning rate, number of espochs to train
#for, and the btach size
INIT_LR = 1e-4
NUM_EPOCHS = 1