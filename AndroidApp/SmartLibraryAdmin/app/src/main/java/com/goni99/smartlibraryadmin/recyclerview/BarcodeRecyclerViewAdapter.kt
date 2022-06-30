package com.goni99.smartlibraryadmin.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goni99.smartlibraryadmin.R
import com.goni99.smartlibraryadmin.model.ReturnBook

class BarcodeRecyclerViewAdapter: RecyclerView.Adapter<BarcodeRecyclerViewHolder>() {

    private var barcodeList = ArrayList<ReturnBook>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_barcode_data, parent, false)
        return BarcodeRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BarcodeRecyclerViewHolder, position: Int) {
        holder.bind(barcodeList[position])
    }

    override fun getItemCount(): Int = barcodeList.size


    fun setBarcodeList(barcodeList: ArrayList<ReturnBook>){
        this.barcodeList = barcodeList
    }
}