package com.example.finalprojectapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.databinding.ItemStageBinding
import com.example.finalprojectapp.ui.StudyActivity

class StageAdapter(private val stageCount: Int) : RecyclerView.Adapter<StageAdapter.StageViewHolder>() {

    class StageViewHolder(val binding: ItemStageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StageViewHolder {
        val binding = ItemStageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StageViewHolder, position: Int) {
        val stageNum = position + 1
        holder.binding.txtStageNum.text = stageNum.toString()

        // 지그재그 배치 (Bias만 적용)
        val params = holder.binding.cardStage.layoutParams as ConstraintLayout.LayoutParams
        params.horizontalBias = getBiasForPosition(position)
        holder.binding.cardStage.layoutParams = params

        holder.binding.cardStage.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, StudyActivity::class.java).apply {
                putExtra("STAGE_NUM", stageNum)
            }
            context.startActivity(intent)
        }
    }

    private fun getBiasForPosition(position: Int): Float {
        return when (position % 4) {
            0 -> 0.2f // 좌
            1 -> 0.5f // 중
            2 -> 0.8f // 우
            3 -> 0.5f // 중
            else -> 0.5f
        }
    }

    override fun getItemCount(): Int = stageCount
}
