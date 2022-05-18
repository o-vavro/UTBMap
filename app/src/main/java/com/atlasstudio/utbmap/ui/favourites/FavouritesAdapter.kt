package com.atlasstudio.utbmap.ui.favourites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atlasstudio.utbmap.data.Office
import com.atlasstudio.utbmap.databinding.ItemFavouritesBinding

class FavouritesAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Office, FavouritesAdapter.FavouritesViewHolder>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
            val binding = ItemFavouritesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return FavouritesViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
            val currentItem = getItem(position)
            holder.bind(currentItem)
        }

        inner class FavouritesViewHolder(private val binding: ItemFavouritesBinding) :
            RecyclerView.ViewHolder(binding.root) {

            init {
                binding.apply {
                    labelLocation.setOnClickListener {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val favourite = getItem(position)
                            listener.onItemClick(favourite)
                        }
                    }

                    textViewName.setOnClickListener {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val favourite = getItem(position)
                            listener.onItemClick(favourite)
                        }
                    }

                    buttonDelete.setOnClickListener {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val favourite = getItem(position)
                            listener.onButtonDeleteClick(favourite)
                        }
                    }
                }
            }

            fun bind(location: Office) {
                binding.apply {
                    textViewName.text = location.id + " - " + location.name
                }
            }
        }

        interface OnItemClickListener {
            fun onItemClick(favourite: Office)
            fun onButtonDeleteClick(favourite: Office)
        }

        class DiffCallback : DiffUtil.ItemCallback<Office>() {
            override fun areItemsTheSame(oldItem: Office, newItem: Office) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Office, newItem: Office) =
                oldItem == newItem
        }
}