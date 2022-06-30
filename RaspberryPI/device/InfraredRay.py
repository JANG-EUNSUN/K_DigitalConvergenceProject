import RPi.GPIO as gpio
import time
import threading

pin_line1 = 2
pin_line2 = 3

gpio.setmode(gpio.BCM)
gpio.setup(pin_line1, gpio.IN)
gpio.setup(pin_line2, gpio.IN)


# 적외선 인식 X - 1 호출
# 적외선 인식 O - 0 호출
class InfraredRay(threading.Thread):
    def __init__(self, client, kdc):
        super().__init__()
        self.client = client
        self.kdc = kdc

    def run(self):
        try:
            print("InfraredRay - run() called")
            gpio.setmode(gpio.BCM)
            gpio.setup(pin_line1, gpio.IN)
            while True:
                status1 = gpio.input(pin_line1)
                status2 = gpio.input(pin_line2)
                print("1번라인 레이저 :", status1)
                print("2번라인 레이저 :", status2)
                if status1 == 0:
                    if 0 < self.kdc <= 299:
                        # 물체 접근
                        print("InfraredRay line 1 motion 감지")
                        self.client.publish("iot/laser", "1")
                        self.client.publish("iot/laser", "access/1")
                        time.sleep(1)
                        self.client.publish("iot/laser", "finish/1")
                        break
                    else:
                        self.client.publish("iot/laser", "1")
                        time.sleep(0.5)
                        self.client.publish("iot/laser", "finish/1")
                if status2 == 0:
                    if 300 < self.kdc <= 599:
                        # 물체 접근
                        print("InfraredRay line 2 motion 감지")
                        self.client.publish("iot/laser", "2")
                        self.client.publish("iot/laser", "access/2")
                        time.sleep(1)
                        self.client.publish("iot/laser", "finish/2")
                        break
                    elif 0 < self.kdc <= 299:
                        self.client.publish("iot/laser", "2")
                        self.client.publish("iot/laser", "access/2")
                        time.sleep(1)
                        self.client.publish("iot/laser", "finish/2")
                        break
                    else:
                        self.client.publish("iot/laser", "2")
                        time.sleep(1)
                        self.client.publish("iot/laser", "finish/2")
                        break
                time.sleep(0.01)

        except RuntimeError as error:
            print("laser error")
            print(error.args[0])
        finally:
            print("InfraredRay - run() finish")
