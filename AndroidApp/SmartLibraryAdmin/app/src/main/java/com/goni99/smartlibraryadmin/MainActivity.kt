package com.goni99.smartlibraryadmin

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.goni99.smartlibraryadmin.databinding.ActivityMainBinding
import com.goni99.smartlibraryadmin.model.ReturnBook
import com.goni99.smartlibraryadmin.mqtt.MyMqtt
import com.goni99.smartlibraryadmin.recyclerview.BarcodeRecyclerViewAdapter
import com.goni99.smartlibraryadmin.utils.API
import com.goni99.smartlibraryadmin.utils.Constants.TAG
import com.goni99.smartlibraryadmin.utils.MQTT
import okhttp3.*
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    val subTopic = "iot/#"
    var myMqtt : MyMqtt? = null

    private var sortBox1Counting = 0
    private var sortBox2Counting = 0
    private var sortBox3Counting = 0
    private var barcodeNum:String? = ""
    private var kdcNum:Int? = 0
    private var bookTitle:String? = ""
    private var bookThumbnail:String? = ""

    private var tts:TextToSpeech? = null

    private lateinit var adapter: BarcodeRecyclerViewAdapter
    private lateinit var returnBookList: ArrayList<ReturnBook>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        returnBookList = ArrayList<ReturnBook>()

        myMqtt = MyMqtt(this, MQTT.SERVER_URI)
        myMqtt?.mySetCallback(::onReceived)
        myMqtt?.connect(arrayOf(subTopic))

        adapter = BarcodeRecyclerViewAdapter()
        adapter.setBarcodeList(returnBookList)
        binding.bookReturnStatusRecyclerView.adapter = adapter
        binding.bookReturnStatusRecyclerView.layoutManager = LinearLayoutManager(this)

        thread {
            val url = API.SERVER_URI + "/api/startBarcode/"

            val client = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.DAYS)
                .readTimeout(1, TimeUnit.DAYS)
                .writeTimeout(1, TimeUnit.DAYS)
                .build()

            val request = Request
                .Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d(TAG, "Error : ${e}")
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d(TAG, "code : ${response.code}")
                    Log.d(TAG, response.body!!.string())                }
            })


        }

        binding.topAppBar.setNavigationOnClickListener {1
            binding.mainLayout.visibility = View.VISIBLE
            binding.dataViewLayout.visibility = View.INVISIBLE
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.sensor_data_menu -> {
                    binding.mainLayout.visibility = View.INVISIBLE
                    binding.dataViewLayout.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }

        binding.toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            when (checkedId){
                R.id.force_belt_start_btn -> {
                    myMqtt?.publish("iot/belt", "force/start")
                    binding.conveyorBeltStatusSwitch.isChecked = true
                    binding.conveyorBeltStatusTextView.text = "작동 중"
                }
                R.id.force_belt_stop_btn -> {
                    myMqtt?.publish("iot/belt", "force/stop")
                    binding.conveyorBeltStatusSwitch.isChecked = false
                    binding.conveyorBeltStatusTextView.text = "멈춤"
                }
            }
        }
        binding.dataResetButton.setOnClickListener {
            Toast.makeText(this, "분류 카트 데이터 리셋",Toast.LENGTH_SHORT).show()
            sortBox1Counting = 0
            sortBox2Counting = 0
            sortBox3Counting = 0
            binding.sortBox1CountTextView.text = sortBox1Counting.toString()
            binding.sortBox2CountTextView.text = sortBox2Counting.toString()
            binding.sortBox3CountTextView.text = sortBox3Counting.toString()
        }

        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.KOREAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.d(TAG, "해당 언어는 지원하지 않습니다.")
            }
        } else {
            Log.d(TAG, "TTS init 실패")
        }
    }

    private fun speakOut(){
        tts!!.speak("도서 반납을 시작해주세요", TextToSpeech.QUEUE_FLUSH, null, "")

    }
    private fun errorSpeakOut(){
        tts!!.speak("오류 발생 오류 발생", TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun onReceived(topic:String, message: MqttMessage) {
        val msg = String(message.payload)
        Log.d("mymqtt", "onReceived topic : $topic, msg : $msg")

        when (topic){
            "iot/barcode" -> {
                barcodeNum = msg
            }
            "iot/belt" -> {
                if (msg == "start"){
                    binding.conveyorBeltStatusSwitch.isChecked = true
                    binding.conveyorBeltStatusTextView.text = "작동 중"
                } else if (msg == "stop"){
                    binding.conveyorBeltStatusSwitch.isChecked = false
                    binding.conveyorBeltStatusTextView.text = "멈춤"
                } else if (msg == "error"){
                    sortBox1Counting -= 1
                    binding.sortBox1CountTextView.text = sortBox1Counting.toString()
                    errorSpeakOut()
                    thread {
                        runOnUiThread {
                            binding.errorLayout.visibility = View.VISIBLE
                        }
                        Thread.sleep(2000)
                        runOnUiThread {
                            binding.errorLayout.visibility = View.INVISIBLE
                        }
                    }
                }

            }
            "iot/RGB_val" -> {
                val RGBlist = msg.split("/")
                binding.rgbLedColorRData.text = RGBlist[0]
                binding.rgbLedColorGData.text = RGBlist[1]
                binding.rgbLedColorBData.text = RGBlist[2]
            }
            "iot/RGB" -> {
                if (msg == "stop"){
                    binding.RGBLight.setImageResource(R.drawable.ic_baseline_highlight_24)
                } else {
                    val kdc = msg.toInt()
                    when(kdc){
                        in 0 until 100 -> {
                            binding.RGBLight.setImageResource(R.drawable.light_000)
                            binding.rgbLedColorStatusImageView.setImageResource(R.drawable.adb_000)
                        }
                        in 100 until 200 -> {
                            binding.RGBLight.setImageResource(R.drawable.light_100)
                            binding.rgbLedColorStatusImageView.setImageResource(R.drawable.adb_100)
                        }
                        in 200 until 300 -> {
                            binding.RGBLight.setImageResource(R.drawable.light_200)
                            binding.rgbLedColorStatusImageView.setImageResource(R.drawable.adb_200)
                        }
                        in 300 until 400 -> {
                            binding.RGBLight.setImageResource(R.drawable.light_300)
                            binding.rgbLedColorStatusImageView.setImageResource(R.drawable.adb_300)
                        }
                        in 400 until 500 -> {
                            binding.RGBLight.setImageResource(R.drawable.light_400)
                            binding.rgbLedColorStatusImageView.setImageResource(R.drawable.adb_400)
                        }
                        in 500 until 600 -> {
                            binding.RGBLight.setImageResource(R.drawable.light_500)
                            binding.rgbLedColorStatusImageView.setImageResource(R.drawable.adb_500)
                        }
                        in 600 until 700 -> {
                            binding.RGBLight.setImageResource(R.drawable.light_600)
                            binding.rgbLedColorStatusImageView.setImageResource(R.drawable.adb_600)
                        }
                        in 700 until 800 -> {
                            binding.RGBLight.setImageResource(R.drawable.light_700)
                            binding.rgbLedColorStatusImageView.setImageResource(R.drawable.adb_700)
                        }
                        in 800 until 900 -> {
                            binding.RGBLight.setImageResource(R.drawable.light_800)
                            binding.rgbLedColorStatusImageView.setImageResource(R.drawable.adb_800)
                        }
                        in 900 until 1000 -> {
                            binding.RGBLight.setImageResource(R.drawable.light_900)
                            binding.rgbLedColorStatusImageView.setImageResource(R.drawable.adb_900)
                        }
                    }
                }
            }
            "iot/returnItem" -> {
                speakOut()
                Toast.makeText(this, "도서 반납을 시작해주세요", Toast.LENGTH_SHORT).show()
                val returnVal = msg.split("|")
                val KDC = returnVal[0].split(".")[0].toInt()
                val title = returnVal[1]
                val imgUrl = returnVal[2]
                kdcNum = KDC
                bookTitle = title
                bookThumbnail = imgUrl
                val returnBook = ReturnBook(
                    title = bookTitle,
                    imgUrl = bookThumbnail,
                    barcodeNum = barcodeNum,
                    kdc = kdcNum
                )
                returnBookList.add(returnBook)
                adapter.notifyDataSetChanged()
                when (KDC) {
                    in 0..299 -> {
                        sortBox1Counting += 1
                        binding.sortBox1CountTextView.text = sortBox1Counting.toString()
                    }
                    in 300..599 -> {
                        sortBox2Counting += 1
                        binding.sortBox2CountTextView.text = sortBox2Counting.toString()
                    }
                    in 600..999 -> {
                        sortBox3Counting += 1
                        binding.sortBox3CountTextView.text = sortBox3Counting.toString()
                    }
                }
            }
            "iot/laser" -> {
                when (msg){
                   "1" -> {
                           binding.line1Object.visibility = View.VISIBLE
                   }
                    "2" -> {
                            binding.line2Object.visibility = View.VISIBLE
                    }
                    "finish/1" -> {
                        binding.line1Object.visibility = View.INVISIBLE
                    }
                    "finish/2" -> {
                        binding.line2Object.visibility = View.INVISIBLE
                    }
                }
            }
            "iot/led" -> {
                when (msg){
                    "blue" -> {
                        binding.beltStatusCorrect.setImageResource(R.drawable.circle_blue)
                        binding.beltStatusStop.setImageResource(R.drawable.ic_baseline_circle_24)
                        binding.beltStatusError.setImageResource(R.drawable.ic_baseline_circle_24)
                    }
                    "yellow" -> {
                        binding.beltStatusCorrect.setImageResource(R.drawable.ic_baseline_circle_24)
                        binding.beltStatusStop.setImageResource(R.drawable.circle_yellow)
                        binding.beltStatusError.setImageResource(R.drawable.ic_baseline_circle_24)
                    }
                    "red" -> {
                        binding.beltStatusCorrect.setImageResource(R.drawable.ic_baseline_circle_24)
                        binding.beltStatusStop.setImageResource(R.drawable.ic_baseline_circle_24)
                        binding.beltStatusError.setImageResource(R.drawable.circle_red)
                    }
                }
            }
            "iot/breaker" -> {
                when (msg) {
                    "1/start" -> {
                        binding.breaker1StatusSwitch.isChecked = true
                        binding.breaker1StatusTextView.text = "작동 중"
                    }
                    "1/stop" -> {
                        binding.breaker1StatusSwitch.isChecked = false
                        binding.breaker1StatusTextView.text = "멈춤"
                    }
                    "2/start" -> {
                        binding.breaker2StatusSwitch.isChecked = true
                        binding.breaker2StatusTextView.text = "작동 중"
                    }
                    "2/stop" -> {
                        binding.breaker2StatusSwitch.isChecked = false
                        binding.breaker2StatusTextView.text = "멈춤"
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }
    }
}