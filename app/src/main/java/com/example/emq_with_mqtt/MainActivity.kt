package com.example.emq_with_mqtt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MainActivity : AppCompatActivity() {

   private lateinit var mqttClient: MqttAndroidClient

   companion object{
        val TAG = MainActivity::class.java.simpleName
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btConnect.setOnClickListener {
            connect()
        }

        btSubscribe.setOnClickListener {
            subscribe("viper")
        }

        btUnSubscribe.setOnClickListener {
            unsubscribe("viper")
        }

        btPublish.setOnClickListener {
            publish("viper","hello viper")
        }

        btDisconnect.setOnClickListener {
            disconnect()
        }

    }

    //连接MQTT服务器
    fun connect(){
        val serverURI = "tcp://broker.emqx.io:1883"
        mqttClient = MqttAndroidClient(this,serverURI,"kotlin_client")
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d(TAG, "Receive message: ${message.toString()} from topic: $topic")
            }

            override fun connectionLost(cause: Throwable?) {
                Log.d(TAG, "connection lost: ${cause.toString()}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }
        })

        val options = MqttConnectOptions()
        try {
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Connection success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Connection failure")
                }
            })
        }catch (e:MqttException){
            e.printStackTrace()
        }

    }


    //创建订阅
    fun subscribe(topic: String,qos: Int = 1 ){

        try{
            mqttClient.subscribe(topic,qos,null,object: IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "subscribe to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "failure to subscribe $topic")
                }
            })
        }catch (e:MqttException){
            e.printStackTrace()
        }
    }


    //取消订阅
    fun unsubscribe(topic: String){

        try{
            mqttClient.unsubscribe(topic,null,object: IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Unsubscribe to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "failure to unsubscribe $topic")
                }
            })
        }catch (e:MqttException){
            e.printStackTrace()
        }
    }


    //发布消息
    fun publish(topic: String,msg: String,qos: Int = 1,retained: Boolean = false){
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "$msg publish to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to publish $msg to $topic")
                }
            })

        }catch (e:MqttException){
            e.printStackTrace()
        }
    }

    //断开MQTT连接
    fun disconnect(){
        try {
            mqttClient.disconnect(null,object: IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Disconnected")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "failure to disconnect")
                }
            })
        }catch (e:MqttException){
            e.printStackTrace()
        }
    }
}