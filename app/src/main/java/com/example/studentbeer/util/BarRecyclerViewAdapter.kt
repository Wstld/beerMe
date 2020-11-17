package com.example.studentbeer.util.RecyclerViewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.example.studentbeer.R
import com.example.studentbeer.data.models.BarModel
import com.example.studentbeer.viewmodel.MainActivityViewModel

class BarRecyclerViewAdapter(
    private val bars: MutableList<BarModel>,
    private val viewModel: MainActivityViewModel
) : RecyclerView.Adapter<BarViewHolder>() {


    override fun getItemCount(): Int {
        return bars.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val barCard = layoutInflater.inflate(R.layout.bar_item, parent, false)
        return BarViewHolder(barCard)
    }

    override fun onBindViewHolder(holder: BarViewHolder, position: Int) {
        holder.bindItem(bars[position])
        // holder.walkingTime.text =
    }

}

class BarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    //val tvDistance: Button = view.findViewById(R.id.tvDistance)
    private val tvPrice: Button = view.findViewById(R.id.tvPrice)
    private val tvBarName: Button = view.findViewById(R.id.tvBarName)
    private val tvAddress: Button = view.findViewById(R.id.tvAddress)
    private val tvOpenHours: Button = view.findViewById(R.id.tvOpenHours)
    private val rbBar: RatingBar = view.findViewById(R.id.rbBar)
    //val bDirection: RatingBar = view.findViewById(R.id.bDirection)

    fun bindItem(bar: BarModel) {
        tvPrice.text = bar.beerPrice.toString()
        tvBarName.text = bar.barName
        tvAddress.text = bar.streetName
        tvOpenHours.text = bar.openHours
        rbBar.rating = bar.rating.toFloat()
    }

}