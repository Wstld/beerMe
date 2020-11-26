package com.example.studentbeer.other.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentbeer.R
import com.example.studentbeer.data.models.BarModel
import com.example.studentbeer.data.models.apiResponse.DirectionsModel
import com.example.studentbeer.databinding.BarItemBinding
import kotlinx.coroutines.runBlocking

class BarRecyclerViewAdapter(
    private val bars: MutableList<BarModel>,
    private val context: Context,
    val onClickedNavBtn: (BarModel) -> Unit,
    val getDirections: (Double,Double) -> DirectionsModel?
) : RecyclerView.Adapter<BarRecyclerViewAdapter.ViewHolder>() {
    private val binding = BarItemBinding.inflate(LayoutInflater.from(context))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = BarItemBinding.bind(view)
        fun bindItems(bar: BarModel) {
            val direction = getDirections(bar.latitude,bar.longitude)
            if (direction != null) {
                binding.tvDistance.text = direction.routes[0].legs[0].duration.text
            }
           // binding.llContainer.setBackgroundResource(R.drawable.stroke_custom_bg)
            binding.tvBarName.text = bar.barName
            binding.tvAddress.text = bar.streetName
            binding.tvOpenHours.text = bar.openHours
            binding.tvPrice.text = bar.beerPrice.toString()
            binding.rbBar.rating = bar.rating.toFloat()
            binding.bDirection.setOnClickListener {
                onClickedNavBtn(bar)
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
