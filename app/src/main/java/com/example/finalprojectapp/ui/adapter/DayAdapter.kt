package com.example.finalprojectapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.databinding.ItemDayBinding
import com.example.finalprojectapp.ui.WordListActivity

class DayAdapter(private val dayCount: Int) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    class DayViewHolder(val binding: ItemDayBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val dayNum = position + 1
        holder.binding.txtDayNum.text = "DAY $dayNum"

        holder.binding.cardDay.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, WordListActivity::class.java).apply {
                putExtra("DAY_NUM", dayNum)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dayCount
}
