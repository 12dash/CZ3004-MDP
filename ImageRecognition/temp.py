import os 
from PIL import Image

img_path = os.listdir("./output")
new_im = Image.new('RGB', (256*5,256))
x_offset = 0
for i in range(len(img_path)):
    img_temp = Image.open(f"output/{img_path[i]}")    
    new_im.paste(img_temp,(x_offset,0))
    x_offset += img_temp.size[0]
new_im.save("final.jpg")