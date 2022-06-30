package com.goni99.smartlibrarysystem.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goni99.smartlibrarysystem.R
import com.goni99.smartlibrarysystem.model.Book
import com.goni99.smartlibrarysystem.utils.Constants.TAG

class BookRentRecyclerViewAdapter(
    bookRentRecyclerViewInterface: IBookRentRecyclerView
): RecyclerView.Adapter<BookRentRecyclerViewHolder>() {
    private var bookList = ArrayList<Book>()
    private var iBookRentRecyclerView: IBookRentRecyclerView? = null

    init {
        Log.d(TAG, "BookRentRecyclerViewAdapter - init() called")
        this.iBookRentRecyclerView = bookRentRecyclerViewInterface
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookRentRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_book_return_item, parent, false)
        return BookRentRecyclerViewHolder(view, this.iBookRentRecyclerView!!)
    }

    override fun onBindViewHolder(holder: BookRentRecyclerViewHolder, position: Int) {
        holder.bind(bookList[position])
    }

    override fun getItemCount(): Int = bookList.size

    fun setBookList(bookList: Book){
        this.bookList.add(bookList)
    }
}