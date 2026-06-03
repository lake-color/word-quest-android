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

        // 연결선 가시성 및 각도 처리
        if (position < stageCount - 1) {
            holder.binding.lineNext.visibility = View.VISIBLE
            val nextBias = getBiasForPosition(position + 1)
            
            // 단순 선 연결이 아닌 지그재그 방향을 나타내기 위해 회전 및 길이 조정
            // 화면 너비를 대략 1000px로 가정했을 때의 계산 (정밀도는 떨어질 수 있음)
            val dx = (nextBias - currentBias) * 300f 
            val dy = 120f // item height 대략값
            val angle = Math.toDegrees(atan2(dx.toDouble(), dy.toDouble())).toFloat()
            val length = sqrt(dx * dx + dy * dy)
            
            holder.binding.lineNext.rotation = angle
            holder.binding.lineNext.layoutParams.height = length.toInt()
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
            0 -> 0.25f // 좌
            1 -> 0.5f  // 중
            2 -> 0.75f // 우
            3 -> 0.5f  // 중
            else -> 0.5f
        }
    }

    override fun getItemCount(): Int = stageCount
}
