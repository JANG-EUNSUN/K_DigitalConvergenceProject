import RPi.GPIO as gpio
import time

gpio.setmode(gpio.BCM)
ENA = 26
IN1 = 19
IN2 = 13


gpio.setup(ENA, gpio.OUT)
gpio.setup(IN1, gpio.OUT)
gpio.setup(IN2, gpio.OUT)

pwm = gpio.PWM(ENA, 100)


try:
  pwm.start(0)
  while True:
    # print("전진?")
    # pwm.ChangeDutyCycle(40)
    # gpio.output(IN1, gpio.HIGH)
    # gpio.output(IN2, gpio.LOW)
    # time.sleep(2)
    print("후진?")
    pwm.ChangeDutyCycle(40)
    gpio.output(IN1, gpio.LOW)
    gpio.output(IN2, gpio.HIGH)
    time.sleep(2)
    # print("정지")
    # gpio.output(IN1, gpio.LOW)
    # gpio.output(IN2, gpio.LOW)
    # time.sleep(2)
except:
  gpio.cleanup()