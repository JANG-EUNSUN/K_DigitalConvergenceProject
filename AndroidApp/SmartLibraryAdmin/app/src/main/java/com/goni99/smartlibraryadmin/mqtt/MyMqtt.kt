package com.goni99.smartlibraryadmin.mqtt

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MyMqtt(context: Context, url: String) {

    var mqttClient: MqttAndroidClient =
        MqttAndroidClient(context, url, MqttClient.generateClientId())

    fun mySetCallback(callback: (topic:String, message: MqttMessage) -> Unit){
        mqttClient.setCallback(object: MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.d("mymqtt", "connectionLost")

            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d("mymqtt", "messageArrived")
                callback(topic!!, message!!)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d("mymqtt", "deliveryComplete")
            }
        })
    }


    // mqtt 통신을 하기 위해 브로커 서버와 연결, 연결이 끝난 후 콜벡메소드 설정
    fun connect(topic: Array<String>) {
        val mqttConnectOptions = MqttConnectOptions()
        mqttClient.connect(mqttConnectOptions, null,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("mymqtt", "브로커 접속 성공...")
                    topic.map {
                        subscribeTopic(it)
                    }
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken?,
                    exception: Throwable?
                ) {
                    Log.d("mymqtt", "브로커 접속 실패...")
                }
            })
    }

    // 토픽을 subscribe 등록하기 위해서 메소드 구현
    private fun subscribeTopic(topic:String, qos:Int = 0){
        mqttClient.subscribe(topic, qos, null, object: IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("mymqtt", "토픽: $topic subscribe 성공")

            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d("mymqtt", "토픽: $topic subscribe 실패")
            }
        })
    }
    // qos : 메시지 품질
    fun publish(topic: String, payload: String, qos: Int = 0) {
        if (mqttClient.isConnected === false) {
            mqttClient.connect()
        }
        val message = MqttMessage()
        message.payload = payload.toByteArray()
        message.qos = qos
        mqttClient.publish(topic, message, qos, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("mymqtt", "토픽: $topic publish 메시지 전송 성공")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d("mymqtt", "토픽: $topic publish 메시지 전송 실패")
            }
        })
    }


}