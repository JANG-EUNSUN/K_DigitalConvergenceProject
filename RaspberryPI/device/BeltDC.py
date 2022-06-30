import threading
import time
import RPi.GPIO as gpio

from device.LED import LED

gpio.setmode(gpio.BCM)
ENA = 26
IN1 = 19
IN2 = 13

gpio.setup(ENA, gpio.OUT)
gpio.setup(IN1, gpio.OUT)
gpio.setup(IN2, gpio.OUT)

pwm = gpio.PWM(ENA, 100)


class BeltDC(threading.Thread):
    def __init__(self, client, status, event):
        super().__init__()
        self.client = client
        self.status = status
        self.event = event
        pwm.start(0)

    def setMotor(self):
        print("BletDC - setMotor() called")
        # 모터 시작
        print("BletDC - 벨트 동작")
        gpio.setmode(gpio.BCM)
        gpio.setup(ENA, gpio.OUT)
        gpio.setup(IN1, gpio.OUT)
        gpio.setup(IN2, gpio.OUT)
        if self.status == "force/stop":
            pwm.ChangeDutyCycle(0)
            gpio.output(IN1, gpio.LOW)
            gpio.output(IN2, gpio.LOW)
            self.client.publish("iot/belt", "android/stop")
        elif self.status == "force/start":
            pwm.ChangeDutyCycle(75)
            gpio.output(IN1, gpio.LOW)
            gpio.output(IN2, gpio.HIGH)
            self.client.publish("iot/belt", "android/start")
        else:
            if self.event == "stop":
                print("BletDC - 벨트 멈춤")
                pwm.ChangeDutyCycle(0)
                gpio.output(IN1, gpio.LOW)
                gpio.output(IN2, gpio.LOW)
                self.client.publish("iot/belt", "stop")
                time.sleep(1)
            elif self.event == "error":
                print("BletDC - 벨트 접근 오류")
                pwm.ChangeDutyCycle(0)
                gpio.output(IN1, gpio.LOW)
                gpio.output(IN2, gpio.LOW)
                self.client.publish("iot/belt", "error")
                time.sleep(1)
            else:
                pwm.ChangeDutyCycle(75)
                gpio.output(IN1, gpio.LOW)
                gpio.output(IN2, gpio.HIGH)
                self.client.publish("iot/belt", "start")

    def run(self):
        try:
            print("BletDC - run() called")
            self.setMotor()
        except RuntimeError as error:
            print("dc error")
            print(error.args[0])
        finally:
            print("BletDC - run() finish")
            pass