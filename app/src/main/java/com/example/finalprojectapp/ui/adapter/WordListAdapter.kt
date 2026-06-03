package com.example.finalprojectapp.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.Word
import com.example.finalprojectapp.databinding.ItemWordListBinding

class WordListAdapter : ListAdapter<Word, WordListAdapter.WordViewHolder>(DiffCallback) {

    class WordViewHolder(private val binding: ItemWordListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.txtEnglish.text = word.english
            binding.txtKorean.text = word.korean
            
            if (word.isMemorized) {
                binding.cardWord.strokeColor = Color.parseColor("#FFD700") // Gold/Yellow
                binding.ivStar.visibility = View.VISIBLE
            } else {
                binding.cardWord.strokeColor = Color.TRANSPARENT
                binding.ivStar.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object DiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean = oldItem == newItem
    }
}
