package com.example.finalprojectapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.Word
import com.example.finalprojectapp.databinding.ItemWordListBinding

class WordListAdapter(private val onStarClick: (Word) -> Unit) : ListAdapter<Word, WordListAdapter.WordViewHolder>(DiffCallback) {

    inner class WordViewHolder(private val binding: ItemWordListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.txtEnglish.text = word.english
            binding.txtKorean.text = word.korean
            
            // 오답 횟수 표시
            if (word.wrongCount > 0) {
                binding.txtWrongCount.visibility = android.view.View.VISIBLE
                binding.txtWrongCount.text = binding.root.context.getString(R.string.wrong_count_x_format, word.wrongCount)
            } else {
                binding.txtWrongCount.visibility = android.view.View.GONE
            }

            val starIcon = if (word.isMemorized) {
                R.drawable.ic_star_filled
            } else {
                R.drawable.ic_star_hollow
            }
            binding.btnStar.setImageResource(starIcon)
            
            binding.btnStar.setOnClickListener {
                onStarClick(word)
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
