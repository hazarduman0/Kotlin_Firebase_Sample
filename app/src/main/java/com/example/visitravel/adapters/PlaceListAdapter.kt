package com.example.visitravel.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.visitravel.R
import com.example.visitravel.models.Location
import com.example.visitravel.utils.Helper

class PlaceListAdapter(private val places: MutableList<Location>) : RecyclerView.Adapter<PlaceListAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener

    val helper = Helper()

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_card, parent, false)
        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: PlaceListAdapter.ViewHolder, position: Int) {
        val place = places[position]
        holder.cardName.text = place.locationName
        holder.cardDescription.text = place.locationDescription
        holder.cardDate.text = helper.formatDate(place.date)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    inner class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val cardName : TextView = itemView.findViewById(R.id.cardLocationName)
        val cardDescription : TextView = itemView.findViewById(R.id.cardDescription)
        val cardDate : TextView = itemView.findViewById(R.id.cardDate)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }

            itemView.setOnLongClickListener {
                val dialogBuilder = AlertDialog.Builder(itemView.context)
                dialogBuilder.setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Delete") { dialog, which ->
                        val position = absoluteAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            places.removeAt(position)
                            notifyItemRemoved(position)
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show()

                true
            }
        }

    }
}