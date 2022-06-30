package com.goni99.smartlibrarysystem.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.goni99.smartlibrarysystem.R
import com.goni99.smartlibrarysystem.adapter.LibraryPageAdapter
import com.goni99.smartlibrarysystem.databinding.ActivityLibraryBinding
import com.goni99.smartlibrarysystem.fragment.UserFragment
import com.goni99.smartlibrarysystem.model.Book
import com.goni99.smartlibrarysystem.model.User
import com.goni99.smartlibrarysystem.utils.API
import com.goni99.smartlibrarysystem.utils.Constants.TAG
import com.goni99.smartlibrarysystem.utils.SharedPreferenceManager
import com.goni99.smartlibrarysystem.viewmodel.LibraryViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class LibraryActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityLibraryBinding.inflate(layoutInflater)
    }
    private val fragmentManager = supportFragmentManager
    private lateinit var userFragment: UserFragment

    private lateinit var libraryViewModel: LibraryViewModel

    private var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        userFragment = UserFragment()
        libraryViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(LibraryViewModel::class.java)

        Log.d(TAG, "LibraryActivity - onCreate() called")


        val sharedUserList = SharedPreferenceManager.getUserList()
        userList = sharedUserList as ArrayList<User>
        libraryViewModel.setUserList(userList)

        Log.d(TAG, "userList : $userList")
        callRentBookAPI(userList[0].id)

        binding.bookReturnTopAppBar.setNavigationOnClickListener {
            binding.libraryFrame.visibility = View.VISIBLE
            binding.bottomNavigationBar.visibility = View.VISIBLE
            fragmentManager
                .beginTransaction()
                .remove(userFragment)
                .commit()
        }

        binding.bookReturnTopAppBar.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.user_menu_item -> {
                    binding.libraryFrame.visibility = View.INVISIBLE
                    binding.bottomNavigationBar.visibility = View.INVISIBLE
                    fragmentManager
                        .beginTransaction()
                        .replace(R.id.user_frame_layout, userFragment)
                        .commit()
                    true
                }
                else -> false
            }
        }

        setViewPager()
    }

    private fun callRentBookAPI(userId: String) {
        Log.d(TAG, "LibraryActivity - callRentBookAPI called()")
        thread {
            runOnUiThread {
                libraryViewModel.setIsRentBookList(false)
            }
            val jsonObj = JSONObject()
            jsonObj.put("user_id", userId)

            val client = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.HOURS)
                .readTimeout(1, TimeUnit.HOURS)
                .writeTimeout(1, TimeUnit.HOURS)
                .build()
            val jsonData = jsonObj.toString()
            Log.d(TAG, "jsonData : $jsonData")

            val body = jsonData.toRequestBody("application/json".toMediaTypeOrNull())
            val builder = Request.Builder()
                .url(API.BASE_URL + "api/rentList/")
                .post(body)
            val request = builder.build()
            val response: Response = client.newCall(request).execute()
            Log.d(TAG, "response code : ${response.code}")
            if (response.code == 200){
                runOnUiThread {
                    libraryViewModel.setIsRentBookList(true)
                    Toast.makeText(this, "대여 책 리스트 요청 성공", Toast.LENGTH_SHORT).show()
                }
                val result: String? = response.body?.string()
                val returnJsonArray = JSONArray(result)
                var returnAllBookArrayList = ArrayList<ArrayList<Book>>()
                for (i in 0 until returnJsonArray.length()){
                    var returnJsonData = returnJsonArray.getJSONObject(i)
                    var rentBookData = Book(
                        id = returnJsonData.getInt("id"),
                        isbn13 = returnJsonData.getString("isbn13"),
                        title = returnJsonData.getString("title"),
                        author = returnJsonData.getString("author"),
                        publisher = returnJsonData.getString("publisher"),
                        imgUrl = returnJsonData.getString("img_url"),
                        kdc = returnJsonData.getString("kdc_class_no")
                    )
                    var returnArrayList = ArrayList<Book>()
                    returnArrayList.add(rentBookData)
                    Log.d(TAG, "어레이 리스트 : $returnArrayList")
                    returnAllBookArrayList.add(returnArrayList)
                }
                Log.d(TAG, "대여 책 리스트 All Array List : $returnAllBookArrayList")
                runOnUiThread{
                    libraryViewModel.setRentBookList(returnAllBookArrayList)
                }
                Log.d(TAG, "대여 책 리스트 JSON ARRAY : $returnJsonArray")

            } else {
                runOnUiThread {
                    Toast.makeText(this, "대여 책 리스트 요청 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setViewPager() {
        with(binding) {
            libraryFrame.adapter = LibraryPageAdapter(this@LibraryActivity)

            libraryFrame.registerOnPageChangeCallback(
                object: ViewPager2.OnPageChangeCallback(){
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        bottomNavigationBar.menu.getItem(position).isChecked = true
                    }
                }
            )
            setBottomNavigation()
        }
    }

    private fun setBottomNavigation() {
        val navigation = binding.bottomNavigationBar
        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_first -> {
                    binding.libraryFrame.currentItem = 0
                }
                R.id.menu_second -> {
                    binding.libraryFrame.currentItem = 1
                }
            }
            true
        }
    }
}