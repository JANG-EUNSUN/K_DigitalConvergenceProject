package com.goni99.smartlibrarysystem.utils

import android.content.Context
import android.util.Log
import com.goni99.smartlibrarysystem.App
import com.goni99.smartlibrarysystem.model.User
import com.goni99.smartlibrarysystem.utils.Constants.TAG
import com.google.gson.Gson

object SharedPreferenceManager {
    private const val SHARED_USER = "shared_user"
    private const val KEY_SHARED_USER = "key_shared_user"

    private const val SHARED_USER_MODE = "shared_user_mode"
    private const val KEY_SHARED_USER_MODE = "key_shared_user_mode"

    // 유저 저장
    fun setUserList(userList: MutableList<User>) {
        Log.d(TAG, "SharedPreferenceManager - setUserList() called / userList $userList")

        // 쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_USER, Context.MODE_PRIVATE)
        val userListString = Gson().toJson(userList)

        // 쉐어드 에디터 가져오기
        val editor = shared.edit()
        editor.putString(KEY_SHARED_USER, userListString)
        editor.apply()
    }

    // 유저 로그인 유지 설정하기
    fun setUserAuth(){
        Log.d(TAG, "SharedPreferenceManager - setUserAuth() called")

        val shared = App.instance.getSharedPreferences(SHARED_USER_MODE, Context.MODE_PRIVATE)

        val editor = shared.edit()
        editor.putBoolean(KEY_SHARED_USER_MODE, true)
        editor.apply()
    }

    // 유저 로그인 유지 확인하기
    fun getUserAuth(): Boolean{
        val shared = App.instance.getSharedPreferences(SHARED_USER_MODE, Context.MODE_PRIVATE)
        val isUserHistory = shared.getBoolean(KEY_SHARED_USER_MODE, false)
        Log.d(TAG, "SharedPreferenceManager - setUserAuth() called $isUserHistory")

        return isUserHistory
    }

    // 유저 정보 가져오기
    fun getUserList(): MutableList<User>{
        val shared = App.instance.getSharedPreferences(SHARED_USER, Context.MODE_PRIVATE)
        val userListString = shared.getString(KEY_SHARED_USER, "")!!

        Log.d(TAG, "SharedPreferenceManager - getUserList() called / userListString $userListString")
        var userList = ArrayList<User>()
        userList = Gson().fromJson(userListString, Array<User>::class.java).toMutableList() as ArrayList<User>
        Log.d(TAG, "SharedPreferenceManager - getUserList() called / userList $userList")
        return userList
    }

    // 유저 정보 삭제
    fun clearUserList(){
        Log.d(TAG, "SharedPreferenceManager - clearUserList() called")
        val shared = App.instance.getSharedPreferences(SHARED_USER, Context.MODE_PRIVATE)
        val sharedMode = App.instance.getSharedPreferences(SHARED_USER_MODE, Context.MODE_PRIVATE)

        val editor = shared.edit()
        editor.clear()
        editor.apply()
        val editorMode = sharedMode.edit()
        editorMode.clear()
        editorMode.apply()
    }
}