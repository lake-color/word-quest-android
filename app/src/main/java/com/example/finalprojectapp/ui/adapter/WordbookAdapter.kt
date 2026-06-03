package com.example.finalprojectapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.data.Word
import com.example.finalprojectapp.databinding.ItemWordbookBinding

class WordbookAdapter(private val onStarClick: (Word) -> Unit) : ListAdapter<Word, WordbookAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemWordbookBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.tvEnglish.text = word.english
            binding.tvKorean.text = word.korean
            
            val starIcon = if (word.isMemorized) {
                com.example.finalprojectapp.R.drawable.ic_star_filled
            } else {
                com.example.finalprojectapp.R.drawable.ic_star_hollow
            }
            binding.btnStar.setImageResource(starIcon)
            
            binding.btnStar.setOnClickListener {
                onStarClick(word)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWordbookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean = oldItem == newItem
    }
}