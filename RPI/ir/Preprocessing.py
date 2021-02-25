import cv2
import os
import pandas as pd
import glob
from xml.etree import ElementTree
'''
This is a one time-execution for rescaling of the training data.
'''

def xml_parse(file_path):
    fileName = None
    path = None
    size_width = None
    size_height = None
    size_depth  = None
    class_name = None
    x_min = None
    y_min = None
    x_max = None
    y_max = None
    print(file_path)
    data = ElementTree.parse(file_path).getroot()

    for item in data:
        if (item.tag == "filename") :
            fileName = item.text
        elif(item.tag == 'path'):
            path = item.text
        elif (item.tag == 'size'):
            for i in item:
                if (i.tag == "width") :
                    size_width = i.text
                elif (i.tag == "height") :
                    size_height = i.text
                elif (i.tag == "depth") :
                    size_depth = i.text
        elif (item.tag =='object'):
            for i in item:
                if (i.tag == 'name'):
                    class_name = i.text
                elif (i.tag == "bndbox"):
                    for k in i:
                        if (k.tag == "xmin"):
                            x_min = k.text
                        elif ( k.tag == "ymin"):
                            y_min = k.text
                        elif ( k.tag == "xmax"):
                            x_max = k.text
                        elif ( k.tag == "ymax"):    
                            y_max = k.text
    return fileName,path,size_width,size_height,size_depth,class_name,x_min,y_min,x_max,y_max


def xml_load(folder):
    fileName_l = []
    path_l = []
    size_width_l = []
    size_height_l = []
    size_depth_l  = []
    class_name_l = []
    x_min_l = []
    y_min_l = []
    x_max_l = []
    y_max_l = []

    
    columns = ['fileName','path','size_width','size_height','size_depth','class_name','x_min','y_min','x_max','y_max']
    df = pd.DataFrame(columns = columns)
    
    for i in os.scandir(folder):
        fileName,path,size_width,size_height,size_depth,class_name,x_min,y_min,x_max,y_max = xml_parse(i)
        fileName_l.append(fileName)
        path_l.append(path)
        size_width_l.append(size_width)
        size_height_l.append(size_height)
        size_depth_l.append(size_depth)
        class_name_l.append(class_name)
        x_min_l.append(x_min)
        y_min_l.append(y_min)
        x_max_l.append(x_max)
        y_max_l.append(y_max)
 

    df['fileName'] = fileName_l
    df['path'] = path_l
    df['size_width'] = size_width_l
    df['size_height'] = size_height_l
    df['size_depth'] = size_depth_l
    df['class_name'] = class_name_l
    df['x_min'] = x_min_l
    df['y_min'] = y_min_l
    df['x_max'] = x_max_l
    df['y_max'] = y_max_l

    df.to_csv('Annotations.csv',index = False)
    print(df)

def load_images_from_folder(folder):
    for i in (os.scandir(folder)):
        img = cv2.imread(folder+"/"+i.name)#For resizing of the image.
        resized_image = cv2.resize(img,(256,256))
        cv2.imwrite(f'../train_images/ProcessedImages/{i.name}', resized_image)

#load_images_from_folder('./Cards')

if __name__=="__main__":
    xml_load('../train_images/AnnotatedImages')
    

