import cv2
import os

#This is a one time execution
folder = './Cards'

def load_images_from_folder(folder):
    for i in (os.scandir(folder)):

        img = cv2.imread(folder+"/"+i.name)
        resized_image = cv2.resize(img,(256,256))
        cv2.imwrite(f'./ProcessedImages/{i.name}', resized_image)

    #Scaling of the image
    #example, the image captured is 300x480, we want to scale it to 100x100
    #this is temporary image for checking
    #

load_images_from_folder(folder)