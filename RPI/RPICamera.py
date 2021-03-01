from picamera import PiCamera
from picamera.array import PiRGBArray
import cv2 as cv
import numpy as np
from datetime import datetime


class RPICamera:
  def __init__(self):
    self.cam = PiCamera()
    self.cam.resolution = (256, 256)
    self.output = PiRGBArray(self.cam)
    self.image_path = "/home/pi/workspace/CZ3004-MDP/RPI/images/"

  
  def capture_image(self):
    #Capture image in RGB format
    self.cam.capture(self.output, "rgb")
    array = self.output.array

    if array is None:
      print("No image found")
    else: 
      print("Image captured")
      print(array)
      
      #Save image
      cv.imwrite(f"{self.image_path}{str(datetime.now().strftime('%Y-%m-%d~%H:%M:%S'))}.jpg", array)
      print(f"Image saved to {self.image_path}")

    self.output.truncate(0) #clear buffer
    
    return array
  
  def stop_connection(self):
    self.cam.close()
    

# Test 
if __name__ == "__main__":
  rpi_camera = RPICamera()
  rpi_camera.capture_image()
  rpi_camera.stop_connection()

