package com.goni99.smartlibrarysystem.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.goni99.smartlibrarysystem.App
import com.goni99.smartlibrarysystem.R
import com.goni99.smartlibrarysystem.databinding.FragmentBookRecommendBinding
import com.goni99.smartlibrarysystem.utils.API
import com.goni99.smartlibrarysystem.utils.Constants.TAG
import com.goni99.smartlibrarysystem.utils.SharedPreferenceManager
import com.goni99.smartlibrarysystem.viewmodel.LibraryViewModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class BookRecommendFragment:Fragment() {
    private var mBinding: FragmentBookRecommendBinding? = null
    private val binding get() = mBinding!!

    private lateinit var libraryViewModel: LibraryViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentBookRecommendBinding.inflate(inflater, container, false)
        libraryViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())
            .get(LibraryViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("fragment_lifecycle","BookRecommendFragment - onViewCreated() called")

        val isUserList = SharedPreferenceManager.getUserList()

        binding.userNameText.text = isUserList[0].name
        callRecommendAPI(isUserList[0].id)
    }


    private fun callRecommendAPI(userId:String){
        thread{
            activity?.runOnUiThread {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            val jsonObj = JSONObject()
            jsonObj.put("user_id", userId)

            val client = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.HOURS)
                .readTimeout(1, TimeUnit.HOURS)
                .connectTimeout(1, TimeUnit.HOURS)
                .build()

            val jsonData = jsonObj.toString()
            Log.d(TAG, "jsonData : $jsonData")

            val body = jsonData.toRequestBody("application/json".toMediaTypeOrNull())
            val builder = Request.Builder()
                .url(API.BASE_URL + "api/getBookRcmd/") // 추천 도서 불러오기
                .post(body)
            val request = builder.build()
            val response: Response = client.newCall(request).execute()
            Log.d(TAG, "response code : ${response.code}")
            if (response.code == 200){
                activity?.runOnUiThread {
                    binding.loadingLayout.visibility = View.INVISIBLE
                    Toast.makeText(App.instance, "추천 책 리스트 요청 성공", Toast.LENGTH_SHORT).show()
                }
                val result: String? = response.body?.string()
                val returnJsonObject = JSONObject(result)

                activity?.runOnUiThread{
                    binding.recommendBookTitleText.text = returnJsonObject.getString("title")
                    binding.recommendBookAuthorText.text = returnJsonObject.getString("author")

                    val KDC = returnJsonObject.getString("kdc_class_no").split(".")[0].toInt()
                    when (KDC){
                        in 0 until 100 -> {
                            binding.recommendBookTypeText.text = "총류"
                        }
                        in 100 until 200 -> {
                            binding.recommendBookTypeText.text = "철학"
                        }
                        in 200 until 300 -> {
                            binding.recommendBookTypeText.text = "종교"
                        }
                        in 300 until 400 -> {
                            binding.recommendBookTypeText.text = "사회과학"
                        }
                        in 400 until 500 -> {
                            binding.recommendBookTypeText.text = "자연과학"
                        }
                        in 500 until 600 -> {
                            binding.recommendBookTypeText.text = "기술과학"
                        }
                        in 600 until 700 -> {
                            binding.recommendBookTypeText.text = "예술"
                        }
                        in 700 until 800 -> {
                            binding.recommendBookTypeText.text = "언어"
                        }
                        in 800 until 900 -> {
                            binding.recommendBookTypeText.text = "문학"
                        }
                        in 900 until 1000 -> {
                            binding.recommendBookTypeText.text = "역사"
                        }

                    }
                    Glide
                        .with(App.instance)
                        .load(returnJsonObject.getString("img_url"))
                        .placeholder(R.drawable.ic_baseline_book_24)
                        .into(binding.recommendBookImage)
                }

                Log.d(TAG, "추천 책 리스트 JSON ARRAY : $returnJsonObject")

            } else {
                activity?.runOnUiThread {
                    Toast.makeText(App.instance, "추천 책 리스트 요청 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("fragment_lifecycle","BookRecommendFragment - onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("fragment_lifecycle","BookRecommendFragment - onResume() called")
    }
    
    override fun onPause() {
        super.onPause()
        Log.d("fragment_lifecycle","BookRecommendFragment - onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("fragment_lifecycle","BookRecommendFragment - onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}