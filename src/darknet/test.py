from darkflow.net.build import TFNet
import cv2

options = {"model": "cfg/yolo.cfg", "load": "yolo.weights", "threshold": 0.1, "gpu": 1.0}

tfnet = TFNet(options)

imgcv = cv2.imread("./data/dog.jpg")
result = tfnet.return_predict(imgcv)
print(result)
