package com.goni99.smartlibrarysystem.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.goni99.smartlibrarysystem.model.Book
import com.goni99.smartlibrarysystem.model.User

class LibraryViewModel:ViewModel() {
    private var _userList = MutableLiveData<ArrayList<User>>()
    val userList: LiveData<ArrayList<User>>
        get() = _userList

    private var _isRentBookList = MutableLiveData<Boolean>()
    val isRentBookList: LiveData<Boolean>
        get() = _isRentBookList

    private var _rentBookList = MutableLiveData<ArrayList<ArrayList<Book>>>()
    val rentBookList: LiveData<ArrayList<ArrayList<Book>>>
        get() = _rentBookList

    private var _recommendBook = MutableLiveData<ArrayList<Book>>()
    val recommendBook: LiveData<ArrayList<Book>>
        get() = _recommendBook


    fun setUserList(userList: ArrayList<User>){
        this._userList.value = userList
    }

    fun setRentBookList(rentBookList: ArrayList<ArrayList<Book>>){
        this._rentBookList.value = rentBookList
    }

    fun setIsRentBookList(status: Boolean){
        this._isRentBookList.value = status
    }

    fun setRecommendBook(recommendBook: ArrayList<Book>){
        this._recommendBook.value = recommendBook
    }
}