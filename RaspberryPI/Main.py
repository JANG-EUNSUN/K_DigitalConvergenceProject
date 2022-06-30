from MyMqtt import MyMqtt
from MyDevice import MyDevice
import time
import RPi.GPIO as gpio
from bluetooth import *

if __name__ == '__main__':
    socket = BluetoothSocket(RFCOMM)
    try:
        myMqtt = MyMqtt()
        myMqtt.mymqtt_connect()
        myDevice = MyDevice()
        myDevice.mydevice_connect()
        while True:
            time.sleep(1)
    except RuntimeError as error:
        print("main error")
        print(error.args[0])
    finally:
        print("Main exit")
        gpio.cleanup()
        socket.close()
