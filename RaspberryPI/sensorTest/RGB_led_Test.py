import RPi.GPIO as GPIO
import time
from gpiozero import RGBLED


# 빨강은 21, 초록은 20핀, 파랑은 16핀 지정
pins = RGBLED(red=21,green=20,blue=16)
pin = (16,20,21)

GPIO.setmode(GPIO.BCM)

pins.color = (0, 0, 1)
time.sleep(2)
pins.off()