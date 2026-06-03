package com.example.finalprojectapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.databinding.ItemStageBinding
import com.example.finalprojectapp.ui.StudyActivity
import kotlin.math.atan2
import kotlin.math.sqrt

class StageAdapter(private val stageCount: Int) : RecyclerView.Adapter<StageAdapter.StageViewHolder>() {

    class StageViewHolder(val binding: ItemStageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StageViewHolder {
        val binding = ItemStageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StageViewHolder, position: Int) {
        val stageNum = position + 1
        holder.binding.txtStageNum.text = stageNum.toString()

        val params = holder.binding.cardStage.layoutParams as ConstraintLayout.LayoutParams
        val currentBias = getBiasForPosition(position)
        params.horizontalBias = currentBias
        holder.binding.cardStage.layoutParams = params

        // 연결선 로직 (다음 스테이지 방향으로 연결)
        if (position < stageCount - 1) {
            holder.binding.lineNext.visibility = View.VISIBLE
            val nextBias = getBiasForPosition(position + 1)
            
            // 화면 너비를 대략 1080px로 가정했을 때의 Bias 차이 계산
            val dx = (nextBias - currentBias) * 800f 
            val dy = 160f // item 높이 + padding 대략값
            
            val angle = Math.toDegrees(atan2(dx.toDouble(), dy.toDouble())).toFloat()
            val length = sqrt(dx * dx + dy * dy)
            
            holder.binding.lineNext.rotation = angle
            holder.binding.lineNext.layoutParams.height = length.toInt()
            
            // Pivot 설정 (상단 중앙 기준으로 회전)
            holder.binding.lineNext.pivotX = 2f // line width가 4dp이므로 절반
            holder.binding.lineNext.pivotY = 0f
        } else {
            holder.binding.lineNext.visibility = View.GONE
        }

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
