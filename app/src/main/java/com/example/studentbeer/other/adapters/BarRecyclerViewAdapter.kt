package com.example.studentbeer.other.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentbeer.R
import com.example.studentbeer.data.models.BarFinishedModel
import com.example.studentbeer.data.models.BarModel
import com.example.studentbeer.databinding.BarItemBinding

class BarRecyclerViewAdapter(
    private var bars: MutableList<BarFinishedModel>,
    private val context: Context,
    val onClickedNavBtn: (BarModel) -> Unit,
) : RecyclerView.Adapter<BarRecyclerViewAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = BarItemBinding.bind(view)
        fun bindItems(bar: BarFinishedModel) {
            binding.tvBarName.text = bar.bar.barName
            binding.tvAddress.text = bar.bar.streetName
            binding.tvDistance.text = bar.distanceInTime
            binding.tvPrice.text = bar.bar.beerPrice.toString()
            binding.rbBar.rating = bar.bar.rating.toFloat()
            binding.bDirection.setOnClickListener {
                onClickedNavBtn(bar.bar)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.bar_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(bars[position])
    }

    override fun getItemCount(): Int {
        return bars.size
    }
}