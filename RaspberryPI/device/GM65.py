from bluetooth import *
import threading
import time


class GM65(threading.Thread):
    def __init__(self, client, socket):
        super().__init__()
        self.client = client
        self.socket = socket

    # 아두이노(블루투스) 바코드 번호 데이터 받기
    def receive_message(self):
        print("GM65 - receive_message() called")
        data = self.socket.recv(1024)
        # 바코드 저장 배열
        barcode = []

        for d in data:
            barcode.append(chr(d))

        # 받아온 블루투스 배열의 마지막 빈칸 제거
        barcode = barcode[:-1]
        barcode = "".join(barcode)
        return barcode

    def run(self):
        print("GM65 - run() called")
        while True:
            time.sleep(0.1)
            try:
                while True:
                    barcode = self.receive_message()
                    print("전달된 바코드 넘버 : " + barcode)
                    self.client.publish("iot/barcode", barcode)
                    time.sleep(0.5)
            except RuntimeError as error:
                print(error.args[0])
            finally:
                print("GM65 - run() finished")