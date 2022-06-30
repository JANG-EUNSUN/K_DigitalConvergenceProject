package com.goni99.smartlibrarysystem.recyclerview

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.goni99.smartlibrarysystem.App
import com.goni99.smartlibrarysystem.R
import com.goni99.smartlibrarysystem.model.Book
import com.goni99.smartlibrarysystem.utils.Constants

class BookRentRecyclerViewHolder(
    itemView: View,
    bookRentRecyclerViewInterface: IBookRentRecyclerView
): RecyclerView.ViewHolder(itemView),
    View.OnClickListener
{
    private var iBookRentRecyclerView: IBookRentRecyclerView

    // 뷰 가져오기
    private val bookImage = itemView.findViewById<ImageView>(R.id.book_return_image)
    private val bookTitle = itemView.findViewById<TextView>(R.id.book_return_page_title)
    private val bookAuthor = itemView.findViewById<TextView>(R.id.book_return_page_author)
    private val bookPublisher = itemView.findViewById<TextView>(R.id.book_return_page_publisher)
    private val constraintBookItem = itemView.findViewById<ConstraintLayout>(R.id.constraint_book_item)

    init {
        Log.d(Constants.TAG, "BookRentRecyclerViewHolder - init() called")
        this.iBookRentRecyclerView = bookRentRecyclerViewInterface
        // 리스너 연결
        constraintBookItem.setOnClickListener(this)
    }


    fun bind(bookList: Book){
        bookTitle.text = bookList.title
        bookAuthor.text = bookList.author
        bookPublisher.text = bookList.publisher

        Glide
            .with(App.instance)
            .load(bookList.imgUrl)
            .placeholder(R.drawable.ic_baseline_book_24)
            .into(bookImage)
    }

    override fun onClick(v: View?) {
        when (v){
            constraintBookItem -> {
                Log.d(Constants.TAG, "책 상세 정보 아이템 클릭")
                this.iBookRentRecyclerView.onBookItemDetailClicked(adapterPosition)
            }
        }
    }
}