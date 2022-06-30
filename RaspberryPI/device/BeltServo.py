import threading
import time
import RPi.GPIO as gpio

# 라인 1번 서보 동작
# 핀 번호 12번 90도 -> 180도 (평상시 180도)

# 라인 2번 서보 동작
# 핀 번호 25번 90도 -> 0도 (평상시 0도)

line1_servo_pin = 12
line2_servo_pin = 25
gpio.setmode(gpio.BCM)
gpio.setup(line1_servo_pin, gpio.OUT)
gpio.setup(line2_servo_pin, gpio.OUT)

line1_pwm = gpio.PWM(line1_servo_pin, 50)
line2_pwm = gpio.PWM(line2_servo_pin, 50)


class BeltServo(threading.Thread):
    def __init__(self, client, servo_type):
        super().__init__()
        self.client = client
        self.servo_type = servo_type
        # 초기 서보 설정
        # 라인 1번 서보
        if self.servo_type == "1":
            line1_pwm.start(self.getDutyCycle(180))
            time.sleep(0.3)
            gpio.setup(line1_servo_pin, gpio.IN)
        # 라인 2번 서보
        elif self.servo_type == "2":
            line2_pwm.start(self.getDutyCycle(0))
            time.sleep(0.3)
            gpio.setup(line2_servo_pin, gpio.IN)

    def getDutyCycle(self, degree):
        return 2.5 + degree * (1 / 18)

    def setServo(self, i, servo_pin, pwm):
        gpio.setup(servo_pin, gpio.OUT)
        pwm.ChangeDutyCycle(self.getDutyCycle(i))
        time.sleep(0.3)
        gpio.setup(servo_pin, gpio.IN)

    def run(self):
        print("BletServo - run() called")
        try:
            gpio.setmode(gpio.BCM)
            if self.servo_type == "1":
                self.setServo(135, line1_servo_pin, line1_pwm)
                self.client.publish("iot/breaker", "1/start")
                time.sleep(2.7)
                self.setServo(180, line1_servo_pin, line1_pwm)
                self.client.publish("iot/breaker", "1/stop")
                time.sleep(2.7)
            elif self.servo_type == "2":
                self.setServo(55, line2_servo_pin, line2_pwm)
                self.client.publish("iot/breaker", "2/start")
                time.sleep(2.7)
                self.setServo(0, line2_servo_pin, line2_pwm)
                self.client.publish("iot/breaker", "2/stop")
                time.sleep(2.7)
        except RuntimeError as error:
            print("servo error")
            print(error.args[0])
        finally:
            print("BletServo - run() finish")
