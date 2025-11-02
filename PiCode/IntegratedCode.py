from picamera2 import Picamera2
from ultralytics import YOLO
import time
from flask import Flask, jsonify
import threading

import serial


SERIAL_PORT = "/dev/ttyACM0"  #Port for ADAfeatherboard serial output
BAUDRATE = 115200
TIMEOUT = 1  # seconds

# ----------------------
# SETING UP SERIAL CONNECTION (for temp sensing)
# ----------------------
try:
    ser = serial.Serial(SERIAL_PORT, BAUDRATE, timeout=TIMEOUT)
    print(f"Connected to {SERIAL_PORT} at {BAUDRATE} baud.")
except serial.SerialException as e:
    print(f"Error opening serial port {SERIAL_PORT}: {e}")
    exit(1)

app = Flask(__name__)
latest_output = "Waiting for detections..."


# ---------------
# object detection (using ultralytics already installed)
# tO access yolo models go through miniforge3 path (can be seen in home)
# ---------------
@app.route("/")
def index():
    return f"<pre>{latest_output}</pre>"

def run_server():
   
    app.run(host="0.0.0.0", port=5000, debug=False, threaded=True)

server_thread = threading.Thread(target=run_server, daemon=True)
server_thread.start()


picam2 = Picamera2()
picam2.preview_configuration.main.size = (160, 160) #sets resolution for camera
picam2.preview_configuration.main.format = "RGB888"
picam2.preview_configuration.align()
picam2.configure("preview")
picam2.start()

model = YOLO("yolov11n.pt")
model.to("cpu")

CLOSE_THRESH = 0.4 #determing how far away they are
last_time = time.time()
output_lines = ["temperature", "person"]

@app.route("/data", methods = ["GET"])
def get_data():
    response = {
        "temperature": output_lines[0],
        "person": output_lines[1]
        }
    return jsonify(response)
while True:
   
    if ser.in_waiting > 0:
            line = ser.readline().decode('utf-8', errors='replace').strip()
            if line:
                #troubleshooting  prints to terminal
                print(f"Received: {line}")
                output_lines[0] = f"{line}"
            else:
                time.sleep(0.01)
           
    frame = picam2.capture_array()
    frame_h, frame_w = frame.shape[:2]

    results = model.predict(frame, verbose=False, stream=True)

   

    now = time.time()
    fps = 1 / (now - last_time)
    last_time = now

    for r in results:
        for box in r.boxes:
            cls_id = int(box.cls[0])
            label = model.names[cls_id]

            if label != "person":
                output_lines[1] = f"Person detected: False, NA"
                continue
            #Following code classifies people in their locations
            x1, y1, x2, y2 = box.xyxy[0].cpu().numpy().astype(int)
            bbox_w = x2 - x1
            bbox_h = y2 - y1
            center_x = x1 + bbox_w // 2

            position = "Right" if center_x < frame_w / 2 else "Left"
            distance = "Close" if (bbox_h / frame_h) > CLOSE_THRESH else "Far"

            output_lines[1] = f"Person detected: {position}, {distance}"
            print(f"Person detected: {position}, {distance}")

    #output_lines.append(f"FPS: {fps:.1f}")
   
    # Update latest_output for Flask
    latest_output = "\n".join(output_lines)