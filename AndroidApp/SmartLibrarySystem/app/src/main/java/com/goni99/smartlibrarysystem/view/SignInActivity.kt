package com.goni99.smartlibrarysystem.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.goni99.smartlibrarysystem.databinding.ActivitySignInBinding
import com.goni99.smartlibrarysystem.model.User
import com.goni99.smartlibrarysystem.utils.API
import com.goni99.smartlibrarysystem.utils.Constants.TAG
import com.goni99.smartlibrarysystem.utils.SharedPreferenceManager
import com.goni99.smartlibrarysystem.utils.onMyTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import kotlin.concurrent.thread

class SignInActivity : AppCompatActivity() {
    val binding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }
    private lateinit var userList: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d(TAG, "SignInActivity - onCreate() called")


        binding.signInIdEditText.onMyTextChanged {
            setEditText(it.toString().count(), binding.signInIdTextLayout, "아이디를 입력해주세요")
        }

        binding.signInPwdEditText.onMyTextChanged {
            setEditText(it.toString().count(), binding.signInPwdTextLayout, "비밀번호를 입력해주세요")
            if (it.toString().count() == 12){
                Toast.makeText(this, "검색어를 12자까지만 입력할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signInButton.setOnClickListener {
            if (binding.signInIdEditText.text.toString().count() == 0){
                Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (binding.signInPwdEditText.text.toString().count() == 0){
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                thread {
                    val jsonObj =JSONObject()
                    jsonObj.put("id", binding.signInIdEditText.text)
                    Log.d(TAG, "jsonObj : ${jsonObj}")

                    val client = OkHttpClient()

                    val jsonData = jsonObj.toString()
                    Log.d(TAG, "jsonData : ${jsonData}")

                    val body = jsonData.toRequestBody("application/json".toMediaTypeOrNull())
                    val builder = Request.Builder()
                        .url(API.BASE_URL + "api/user/")
                        .post(body)
                    val request = builder.build()
                    val response: Response = client.newCall(request).execute()
                    Log.d(TAG, "response code : ${response.code}")
                    if (response.code == 200){
                        val result: String? = response.body?.string()

                        val returnJsonObj = JSONObject(result)

                        Log.d(TAG, "returnJsonObj : ${returnJsonObj}")
                        val id = returnJsonObj.getString("id")
                        val pwd = returnJsonObj.getString("password")

                        Log.d(TAG, "id : ${id}, pwd : $pwd")
                        if (binding.signInPwdEditText.text.toString() == pwd){
                            runOnUiThread{
                                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                            }
                            val user = User(
                                id = returnJsonObj.getString("id"),
                                pwd = returnJsonObj.getString("password"),
                                name = returnJsonObj.getString("name"),
                                gender = returnJsonObj.getBoolean("gender"),
                                age = returnJsonObj.getInt("age"),
                                phone = returnJsonObj.getString("phone")
                            )
                            val userList = ArrayList<User>()
                            userList.add(user)
                            SharedPreferenceManager.setUserList(userList)
                            SharedPreferenceManager.setUserAuth()
                            startLibraryActivity()
                        } else {
                            runOnUiThread {
                                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "로그인 인증 호출 실패", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        val isUserHistory = SharedPreferenceManager.getUserAuth()
        if (isUserHistory){
            Log.d(TAG, "onStart - isUserHistory : $isUserHistory")
            startLibraryActivity()
        } else {
            Log.d(TAG, "onStart - isUserHistory : $isUserHistory")
        }
//        val userList = SharedPreferenceManager.getUserList()
//        if (userList.isEmpty()){
//            Log.d(TAG, "onStart - userList Empty : $userList")
//        } else {
//            Log.d(TAG, "onStart - userList is Not Empty : $userList")
//            startLibraryActivity()
//        }
    }

    fun startLibraryActivity(){
        val intent = Intent(this, LibraryActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun setEditText(cnt: Int, textLayout: TextInputLayout, inputText:String){
        if (cnt > 0){
            textLayout.helperText = ""
            binding.signInScrollView.scrollTo(0, 500)
        } else {
            textLayout.helperText = inputText
        }
    }
}