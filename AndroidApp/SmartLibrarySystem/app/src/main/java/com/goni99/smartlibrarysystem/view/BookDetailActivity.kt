package com.goni99.smartlibrarysystem.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.goni99.smartlibrarysystem.R
import com.goni99.smartlibrarysystem.databinding.ActivityBookDetailBinding

class BookDetailActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityBookDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val id = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title")
        val author = intent.getStringExtra("author")
        val publisher = intent.getStringExtra("publisher")
        val isbn13 = intent.getStringExtra("isbn13")
        val kdc = intent.getStringExtra("kdc")
        val imageUrl = intent.getStringExtra("image")

        binding.bookDetailTopAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.apply {
            bookDetailId.text = id.toString()
            bookDetailTitle.text = title
            bookDetailAuthor.text = author
            bookDetailPublisher.text = publisher
            bookDetailIsbn13.text = isbn13
            bookDetailKdc.text = kdc
            Glide
                .with(this@BookDetailActivity)
                .load(imageUrl)
                .placeholder(R.drawable.ic_baseline_book_24)
                .into(bookDetailImage)
        }
    }
}