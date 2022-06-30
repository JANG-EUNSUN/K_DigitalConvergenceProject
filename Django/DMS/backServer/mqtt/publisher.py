import paho.mqtt.client as mqtt
import paho.mqtt.publish as publisher
from backServer.models import *
from datetime import date
from django.db.models.query import EmptyQuerySet
import time

#mqtt_host_ip='18.216.137.48'
mqtt_host_ip='3.145.35.44'

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("connected OK")
        result,mid=client.subscribe('iot/barcode', 2)
        if result==mqtt.MQTT_ERR_SUCCESS:
            print("subscrib succesful")
        else:
            print("subscribe fail")
    else:
        print("Bad connection Returned code=", rc)


def on_disconnect(client, userdata, flags, rc=0):
    print(str(rc))

def on_message(client, userdata, msg):
    start=time.time()
    book=TestBookList.objects.get(barcode=msg.payload.decode())
    rent=Rent.objects.get(book=book) #대여기록 없을 때 예외처리 필요
    #if rent.returned is None: #반납기록(returned 기록이 없을 때)없을 때
    rent.returned=date.today()
    rent.save()  #반납 처리
    return_msg=book.kdc_class_no+"|"+book.title+"|"+book.img_url
    publisher.single('iot/returnItem',return_msg,hostname=mqtt_host_ip,port=1883)
    # else: #반납기록이 있을 때
    #     publisher.single('iot/returnBook','book is already returned',hostname=mqtt_host_ip,port=1883)
    print(f"{time.time()-start:.5f}초")

def on_publish(client, userdata, mid):
    print("In on_pub callback mid= ", mid)


def startMqtt():
    # 새로운 클라이언트 생성
    client = mqtt.Client()
    # 콜백 함수 설정 on_connect(브로커에 접속), on_disconnect(브로커에 접속중료), on_publish(메세지 발행)
    client.on_connect = on_connect
    client.on_disconnect = on_disconnect
    client.on_publish = on_publish
    client.on_message = on_message
    # address : localhost, port: 1883 에 연결
    client.connect(mqtt_host_ip, 1883, 60)
    client.loop_forever()
    # common topic 으로 메세지 발행
    #
    # client.loop_stop()
    # # 연결 종료
    # client.disconnect()