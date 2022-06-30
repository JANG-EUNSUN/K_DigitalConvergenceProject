import time
import RPi.GPIO as gpio

# 1번 라인
servo_pin = 12
# 2번 라인
# servo_pin = 25
gpio.setmode(gpio.BCM)
gpio.setup(servo_pin, gpio.OUT)
pwm = gpio.PWM(servo_pin, 50)


def getDutyCycle(degree):
    return 2.5 + degree * (1 / 18)


try:
    # 1번 스타트
    pwm.start(getDutyCycle(180))
    # 2번 스타트
    # pwm.start(getDutyCycle(0))
    gpio.setup(servo_pin, gpio.IN)
    while True:
        # 1번 라인
        gpio.setup(servo_pin, gpio.OUT)
        pwm.ChangeDutyCycle(getDutyCycle(135))
        print("서보 135 동작")
        time.sleep(0.3)
        gpio.setup(servo_pin, gpio.IN)
        time.sleep(1.2)

        gpio.setup(servo_pin, gpio.OUT)
        pwm.ChangeDutyCycle(getDutyCycle(180))
        print("서보 180 동작")
        time.sleep(0.3)
        gpio.setup(servo_pin, gpio.IN)
        time.sleep(1.2)

        # 2번 라인
        # gpio.setup(servo_pin, gpio.OUT)
        # pwm.ChangeDutyCycle(getDutyCycle(55))
        # print("서보 55 동작")
        # time.sleep(0.3)
        # gpio.setup(servo_pin, gpio.IN)
        # time.sleep(1.2)

        # gpio.setup(servo_pin, gpio.OUT)
        # pwm.ChangeDutyCycle(getDutyCycle(0))
        # print("서보 0 동작")
        # time.sleep(0.3)
        # gpio.setup(servo_pin, gpio.IN)
        # time.sleep(1.2)


except:
    gpio.cleanup()