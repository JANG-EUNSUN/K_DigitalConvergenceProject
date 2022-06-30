import paho.mqtt.client as mqtt
import threading

from device.RGBled import RGBled
from utils import Host


class MyMqtt:
    def __init__(self):
        self.client = mqtt.Client()
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        self.rgbled = None
        self.kdc = None
        self.servo = None

    def mymqtt_connect(self):
        try:
            print("MyMqtt - mymqtt_connect() 서버 연결 중")
            self.client.connect(Host.host, 1883, 60)
            myThread = threading.Thread(target=self.client.loop_forever)
            myThread.start()
        except RuntimeError as error:
            print("mymqtt error")
            print(error.args[0])
        finally:
            print("MyMqtt - mymqtt_connect() finish")

    def on_connect(self, client, userdata, flags, rc):
        print("MyMqtt connecting..." + str(rc))
        if rc == 0:
            client.subscribe("iot/#")
        else:
            print("MyMqtt connect failed")

    def setRGB(self, r, g, b):
        rgb_val = [None, None, None]
        rgb_val[0] = r
        rgb_val[1] = g
        rgb_val[2] = b
        return rgb_val

    def on_message(self, client, userdata, message):
        msg = message.payload.decode("utf-8")
        if message.topic == "iot/returnItem":
            print("MyMqtt - on_message / topic :" + message.topic + " / msg :" + msg)
            msg = int(msg.split("|")[0].split(".")[0])
            self.kdc = msg
            rgb_val = []
            if 0 < msg < 100:
                # 46 46 148
                rgb_val = self.setRGB(46, 46, 148)
            elif 100 <= msg < 200:
                # 253 201 10
                rgb_val = self.setRGB(253, 201, 10)
            elif 200 <= msg < 300:
                # 237 27 36
                rgb_val = self.setRGB(237, 27, 36)
            elif 300 <= msg < 400:
                # 34 33 31
                rgb_val = self.setRGB(34, 33, 31)
            elif 400 <= msg < 500:
                # 11 148 68
                rgb_val = self.setRGB(11, 148, 68)
            elif 500 <= msg < 600:
                # 129 130 134
                rgb_val = self.setRGB(129, 130, 134)
            elif 600 <= msg < 700:
                # 237 1 138
                rgb_val = self.setRGB(237, 1, 138)
            elif 700 <= msg < 800:
                # 0 173 239
                rgb_val = self.setRGB(0, 173, 239)
            elif 800 <= msg < 900:
                # 241 89 42
                rgb_val = self.setRGB(241, 89, 42)
            elif 900 <= msg < 1000:
                # 142 198 63
                rgb_val = self.setRGB(142, 198, 63)
            self.rgbled = RGBled(self.client, rgb_val, self.kdc)
            self.rgbled.start()

