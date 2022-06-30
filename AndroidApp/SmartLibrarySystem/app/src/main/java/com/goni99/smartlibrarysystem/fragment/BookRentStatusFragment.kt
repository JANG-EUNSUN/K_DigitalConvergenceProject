package com.goni99.smartlibrarysystem.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goni99.smartlibrarysystem.databinding.FragmentBookRentStatusBinding
import com.goni99.smartlibrarysystem.model.Book
import com.goni99.smartlibrarysystem.recyclerview.BookRentRecyclerViewAdapter
import com.goni99.smartlibrarysystem.recyclerview.IBookRentRecyclerView
import com.goni99.smartlibrarysystem.utils.Constants
import com.goni99.smartlibrarysystem.utils.Constants.TAG
import com.goni99.smartlibrarysystem.view.BookDetailActivity
import com.goni99.smartlibrarysystem.viewmodel.LibraryViewModel

class BookRentStatusFragment :
    Fragment(),
    IBookRentRecyclerView{
    private var mBinding: FragmentBookRentStatusBinding? = null
    private val binding get() = mBinding!!

    private lateinit var libraryViewModel: LibraryViewModel
    private lateinit var bookRentRecyclerViewAdapter: BookRentRecyclerViewAdapter

    private lateinit var rentBook: Book

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        libraryViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())
            .get(LibraryViewModel::class.java)
        Log.d("fragment_lifecycle","BookRentStatusFragment - onViewCreated() called")

        mBinding = FragmentBookRentStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("fragment_lifecycle","BookRentStatusFragment - onViewCreated() called")
        bookRentRecyclerViewAdapter = BookRentRecyclerViewAdapter(this)
        binding.bookReturnRecyclerView.adapter = bookRentRecyclerViewAdapter
        binding.bookReturnRecyclerView.layoutManager = LinearLayoutManager(context)

        observeData()
    }

    override fun onStart() {
        super.onStart()
        Log.d("fragment_lifecycle","BookRentStatusFragment - onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("fragment_lifecycle","BookRentStatusFragment - onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("fragment_lifecycle","BookRentStatusFragment - onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("fragment_lifecycle","BookRentStatusFragment - onStop() called")
    }

    fun observeData(){
        libraryViewModel.rentBookList.observe(viewLifecycleOwner, Observer {
            Log.d(Constants.TAG,"rent book list : ${it}")
            it.forEach {
                it.forEach {
                    bookRentRecyclerViewAdapter.setBookList(it)
                    bookRentRecyclerViewAdapter.notifyDataSetChanged()
                }
            }
        })
        libraryViewModel.isRentBookList.observe(viewLifecycleOwner, Observer {
            if (it == false){
                binding.rentBookLoadingLayout.visibility = View.VISIBLE
            } else {
                binding.rentBookLoadingLayout.visibility = View.INVISIBLE
            }
        })
    }

    override fun onBookItemDetailClicked(position: Int) {
        Log.d(TAG, "BookRentStatusFragment - onBookItemDetailClicked() called / position : $position")
        libraryViewModel.rentBookList.observe(viewLifecycleOwner, Observer {
            Log.d(Constants.TAG,"rent book list : ${it[position]}")
            rentBook = it[position][0]
        })
        val intent = Intent(activity, BookDetailActivity::class.java)
        intent.putExtra("id", rentBook.id)
        intent.putExtra("author", rentBook.author)
        intent.putExtra("image", rentBook.imgUrl)
        intent.putExtra("title", rentBook.title)
        intent.putExtra("isbn13", rentBook.isbn13)
        intent.putExtra("kdc", rentBook.kdc)
        intent.putExtra("publisher", rentBook.publisher)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}