package com.example.studentbeer.other.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentbeer.R
import com.example.studentbeer.data.models.BarModel
import com.example.studentbeer.databinding.BarItemBinding

class BarRecyclerViewAdapter(
    private val bars: MutableList<BarModel>,
    private val context: Context
) : RecyclerView.Adapter<BarRecyclerViewAdapter.ViewHolder>() {
    private val binding = BarItemBinding.inflate(LayoutInflater.from(context))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = BarItemBinding.bind(view)
        fun bindItems(bar: BarModel) {
            binding.llContainer.setBackgroundResource(R.drawable.stroke_custom_bg)
            binding.tvBarName.text = bar.barName
            binding.tvAddress.text = bar.streetName
            binding.tvOpenHours.text = bar.openHours
            binding.tvPrice.text = bar.beerPrice.toString()
            binding.rbBar.rating = bar.rating.toFloat()
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
