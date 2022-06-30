import threading
import time
import RPi.GPIO as gpio

led_pin_blue = 24
led_pin_red = 23
led_pin_yellow = 18

gpio.setmode(gpio.BCM)
gpio.setup(led_pin_blue, gpio.OUT)
gpio.setup(led_pin_red, gpio.OUT)
gpio.setup(led_pin_yellow, gpio.OUT)


class LED(threading.Thread):
    def __init__(self, client, status):
        super().__init__()
        self.client = client
        self.status = status

    def run(self):
        try:
            print("LED - run() called")
            if self.status == "blue":
                self.client.publish("iot/led", "blue")
                gpio.setmode(gpio.BCM)
                # 파랑불 켜기
                gpio.setup(led_pin_blue, gpio.OUT)
                gpio.output(led_pin_blue, gpio.HIGH)
                # 빨강불 끄기
                gpio.setup(led_pin_red, gpio.OUT)
                gpio.output(led_pin_red, gpio.LOW)
                # 노란불 끄기
                gpio.setup(led_pin_yellow, gpio.OUT)
                gpio.output(led_pin_yellow, gpio.LOW)
            elif self.status == "red":
                self.client.publish("iot/led", "red")
                gpio.setmode(gpio.BCM)
                # 빨강불 켜기
                gpio.setup(led_pin_red, gpio.OUT)
                gpio.output(led_pin_red, gpio.HIGH)
                # 파랑불 끄기
                gpio.setup(led_pin_blue, gpio.OUT)
                gpio.output(led_pin_blue, gpio.LOW)
                # 노란불 끄기
                gpio.setup(led_pin_yellow, gpio.OUT)
                gpio.output(led_pin_yellow, gpio.LOW)
            elif self.status == "yellow":
                self.client.publish("iot/led", "yellow")
                gpio.setmode(gpio.BCM)
                # 빨강불 끄기
                gpio.setup(led_pin_red, gpio.OUT)
                gpio.output(led_pin_red, gpio.LOW)
                # 파랑불 끄기
                gpio.setup(led_pin_blue, gpio.OUT)
                gpio.output(led_pin_blue, gpio.LOW)
                # 노란불 켜기
                gpio.setup(led_pin_yellow, gpio.OUT)
                gpio.output(led_pin_yellow, gpio.HIGH)
        except RuntimeError as error:
            print("led error")
            print(error.args[0])
        finally:
            print("LED - run() finish")
