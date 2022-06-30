import RPi.GPIO as GPIO
import time

led_pin = 18
# GPIO 핀을 어떤 방법으로 엑세스할 것인지 모드를 설정
GPIO.setmode(GPIO.BCM)
# GPIO 핀이 입력 용인지 출력 용인지 설정
GPIO.setup(led_pin, GPIO.OUT)

# GPIO 핀에 출력하는 작업
GPIO.output(led_pin, GPIO.HIGH)  # 24번으로 출력 led on
time.sleep(1)  # 단위 : second
GPIO.output(led_pin, GPIO.LOW)  # 24번으로 출력 led off
# GPIO 핀 초기화
GPIO.cleanup()