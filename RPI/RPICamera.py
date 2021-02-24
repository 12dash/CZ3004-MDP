from picamera import PiCamera
from picamera.array import PiRGBArray
import cv2
import numpy as np


class RPICamera:
  def __init__(self):
    self.cam = PiCamera()
    self.resolution = (640, 480)
    self.output = PiRGBArray(self.cam)

  
  def capture_image(self):
    #Capture image in BGR format
    self.cam.capture(self.output, "bgr")
    array = self.output.array

    if array is None:
      print("No image found")
    else: 
      print("Image captured")
      print(array)
    
    return array
  
  def stop_connection(self):
    self.cam.close()
    

# Test 
if __name__ == "__main__":
  rpi_camera = RPICamera()
  rpi_camera.capture_image()
  rpi_camera.stop_connection()

