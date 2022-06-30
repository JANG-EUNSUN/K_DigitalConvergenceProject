from bluetooth import *
import threading
import RPi.GPIO as gpio
import paho.mqtt.client as mqtt
import time

from device.GM65 import GM65
from device.InfraredRay import InfraredRay
from device.BeltDC import BeltDC
from device.BeltServo import BeltServo
from device.LED import LED
from utils import Host


class MyDevice:
    def __init__(self):
        self.client = mqtt.Client()
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        self.socket = BluetoothSocket(RFCOMM)
        self.connect_bluetooth()
        self.gm65 = GM65(self.client, self.socket)
        self.gm65.start()
        self.infraredRay = None
        self.beltDC = None
        self.beltServo = None
        self.led = None
        self.kdc = None

    def connect_bluetooth(self):
        self.socket.connect(("98:DA:20:03:82:47", 1))
        print("MyDevice - connect_bluetooth() called")

    def mydevice_connect(self):
        try:
            print("MyDevice - mydevice_connect() 서버 연결 중")
            self.client.connect(Host.host, 1883, 60)
            myThread = threading.Thread(target=self.client.loop_forever)
            myThread.start()
        except RuntimeError as error:
            print("mydevice error")
            print(error.args[0])
        finally:
            print("MyDevice - mydevice_connect() finish")

    def on_connect(self, client, userdata, flags, rc):
        print("MyDevice connecting..." + str(rc))
        if rc == 0:
            client.subscribe("iot/#")
        else:
            print("MyDevice connect failed")

    def on_message(self, client, userdata, message):
        msg = message.payload.decode("utf-8")

        # 바코드 번호 인식
        if message.topic == "iot/barcode":
            pass

        # KDC 번호 받아오기
        if message.topic == "iot/returnItem":
            # 컨베이어 벨트 동작
            self.beltDC = BeltDC(self.client, 'start', None)
            self.beltDC.start()
            # 파랑 LED 켜기
            self.led = LED(self.client, "blue")
            self.led.start()
            # 12.15 -> 12 정수형으로 MSG 전환
            msg = msg.split("|")
            # KDC 번호 멤버함수로 저장
            self.kdc = int(msg[0].split(".")[0])

            # 적외선 센서로 물체 접근 확인 중...
            self.infraredRay = InfraredRay(self.client, self.kdc)
            self.infraredRay.start()

        # 적외선 센서에 물체 접근
        if message.topic == "iot/laser":
            # KDC 번호가 0~299 사이면
            if 0 < self.kdc <= 299:
                # 1번 라인 적외선 센서에 접근하면 정상 작동
                if msg == "access/1":
                    # 1초 컨베이어 벨트 멈추기
                    self.beltDC = BeltDC(self.client, "start", "stop")
                    self.beltDC.start()
                    # 파랑 LED 켜기
                    self.led = LED(self.client, "yellow")
                    self.led.start()
                # 2번 라인 적외선 센서에 접근하면 오류
                elif msg == "access/2":
                    # 1초 컨베이어 벨트 멈추기
                    self.beltDC = BeltDC(self.client, "start", "error")
                    self.beltDC.start()
                    # 빨강 LED 켜기
                    self.led = LED(self.client, "red")
                    self.led.start()
            # KDC 번호가 300~599 사이면
            elif 300 <= self.kdc <= 599:
                if msg == "access/2":
                    self.beltDC = BeltDC(self.client, "start", "stop")
                    self.beltDC.start()
            # KDC 번호가 600~999 사이면
            else:
                self.beltDC = BeltDC(self.client, "start", "stop")
                self.beltDC.start()

        # 컨베이어 벨트 현재 상태
        if message.topic == "iot/belt":
            # 안드로이드에서 강제 컨베이어 벨트 동작 멈춤
            if msg == "force/stop":
                self.beltDC = BeltDC(self.client, "force/stop", "force/stop")
                self.beltDC.start()
                self.led = LED(self.client, "yellow")
                self.led.start()
            # 안드로이드에서 강제 컨베이얼 벨트 동작
            elif msg == "force/start":
                self.beltDC = BeltDC(self.client, "force/start", "force/start")
                self.beltDC.start()
                self.led = LED(self.client, "blue")
                self.led.start()
            # 상태로 멈춤 메시지가 날라오면
            if msg == "stop":
                # KDC 번호가 0~299 사이면
                if 0 < self.kdc <= 299:
                    # 1번 라인 차단기(서보모터) 동작
                    self.beltServo = BeltServo(self.client, "1")
                    self.beltServo.start()
                    # 컨베이어 벨트 재동작
                    self.beltDC = BeltDC(self.client, "start", None)
                    self.beltDC.start()
                    # 파랑 LED 켜기
                    self.led = LED(self.client, "blue")
                    self.led.start()
                # KDC 번호가 300~599 사이면
                elif 300 < self.kdc <= 599:
                    # 2번 라인 차단기(서보모터) 동작
                    self.beltServo = BeltServo(self.client, "2")
                    self.beltServo.start()
                    # 컨베이어 벨트 재동작
                    self.beltDC = BeltDC(self.client, "start", None)
                    self.beltDC.start()
                    # 파랑 LED 켜기
                    self.led = LED(self.client, "blue")
                    self.led.start()
                # KDC 번호가 600~999 사이면
                else:
                    # 컨베이어 벨트 재동작
                    self.beltDC = BeltDC(self.client, "start", None)
                    self.beltDC.start()
                    # 파랑 LED 켜기
                    self.led = LED(self.client, "blue")
                    self.led.start()



