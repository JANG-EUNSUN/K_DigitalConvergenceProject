import RPi.GPIO as GPIO
import time
import threading
from gpiozero import RGBLED

# 빨강은 16핀, 초록은 20핀, 파랑은 21핀 지정
pins = RGBLED(red=16, green=20, blue=21)
pin = (16, 20, 21)


class RGBled(threading.Thread):
    def __init__(self, client, rgb_pins, kdc):
        super().__init__()
        self.client = client
        self.rgb_pins = rgb_pins
        self.kdc = kdc

    def run(self):
        print("RGBled - run() called")
        try:
            self.client.publish("iot/RGB", str(self.kdc))
            GPIO.setmode(GPIO.BCM)
            pins.color = (self.rgb_pins[0] / 255, self.rgb_pins[1] / 255, self.rgb_pins[2] / 255)
            rgb_pub_msg = str(self.rgb_pins[0]) + "/" + str(self.rgb_pins[1]) + "/" + str(self.rgb_pins[2])
            self.client.publish("iot/RGB_val", rgb_pub_msg)
            time.sleep(3)
            self.client.publish("iot/RGB", "stop")
        except RuntimeError as error:
            print("rgb led error")
            print(error.args[0])
        finally:
            print("RGBled - run() finish")
            pins.off()



