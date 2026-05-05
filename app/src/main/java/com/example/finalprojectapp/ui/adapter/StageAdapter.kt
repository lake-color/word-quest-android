package com.example.finalprojectapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
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
        holder.binding.btnStage.text = stageNum.toString()

        // 지그재그 로직: ConstraintLayout의 horizontalBias 조정
        val params = holder.binding.btnStage.layoutParams as ConstraintLayout.LayoutParams
        params.horizontalBias = when (position % 4) {
            0 -> 0.3f // 왼쪽
            1 -> 0.5f // 중앙
            2 -> 0.7f // 오른쪽
            3 -> 0.5f // 중앙
            else -> 0.5f
        }
        holder.binding.btnStage.layoutParams = params

        // 첫 번째 아이템은 위쪽 선 숨기기
        holder.binding.linePath.visibility = if (position == 0) View.GONE else View.VISIBLE

        holder.binding.btnStage.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, StudyActivity::class.java).apply {
                putExtra("STAGE_NUM", stageNum)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = stageCount
}