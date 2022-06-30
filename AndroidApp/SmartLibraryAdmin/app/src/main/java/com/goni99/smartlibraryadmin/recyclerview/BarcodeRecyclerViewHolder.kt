package com.goni99.smartlibraryadmin.recyclerview

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.goni99.smartlibraryadmin.App
import com.goni99.smartlibraryadmin.R
import com.goni99.smartlibraryadmin.model.ReturnBook

class BarcodeRecyclerViewHolder(
    itemView:View
):RecyclerView.ViewHolder(itemView) {
    val title = itemView.findViewById<TextView>(R.id.book_return_page_title)
    val imgUrl = itemView.findViewById<ImageView>(R.id.book_return_image)
    val barcodeNum = itemView.findViewById<TextView>(R.id.barcode_number_text_view)
    val kdc = itemView.findViewById<TextView>(R.id.kdc_number_text_view)
    fun bind(returnBook: ReturnBook){
        title.text = returnBook.title
        barcodeNum.text = returnBook.barcodeNum.toString()
        kdc.text = returnBook.kdc.toString()
        Glide
            .with(App.instance)
            .load(returnBook.imgUrl)
            .placeholder(R.drawable.ic_baseline_book_24)
            .into(imgUrl)
    }
}