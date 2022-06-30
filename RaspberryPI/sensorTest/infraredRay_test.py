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
while True:
  try:
    status1 = gpio.input(pin_line1)
    status2 = gpio.input(pin_line2)
    print("1번라인 레이저 :", status1)
    print("2번라인 레이저 :", status2)
    time.sleep(0.01)
  except RuntimeError as error:
    print(error.args[0])
  finally:
    pass