package com.example.finalprojectapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.databinding.ItemDayBinding
import com.example.finalprojectapp.databinding.ItemStageBinding

/**
 * 학습(Stage)과 단어장(Day)에서 공통으로 사용하는 통합 어댑터
 * @param count 아이템 개수 (기본 20개)
 * @param isStageMode true면 학습 맵(지그재그), false면 단어장(그리드) 모드
 */
class StageDayAdapter(
    private val count: Int, 
    private val isStageMode: Boolean,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 학습 맵용 홀더
    class StageViewHolder(val binding: ItemStageBinding) : RecyclerView.ViewHolder(binding.root)
    // 단어장용 홀더
    class DayViewHolder(val binding: ItemDayBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int = if (isStageMode) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            StageViewHolder(ItemStageBinding.inflate(inflater, parent, false))
        } else {
            DayViewHolder(ItemDayBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val num = position + 1
        val context = holder.itemView.context

        if (holder is StageViewHolder) {
            holder.binding.txtStageNum.text = num.toString()
            
            // 지그재그 배치 로직 (Stage 모드에서만 적용)
            val params = holder.binding.cardStage.layoutParams as ConstraintLayout.LayoutParams
            params.horizontalBias = when (position % 4) {
                0 -> 0.2f // 좌
                1 -> 0.5f // 중
                2 -> 0.8f // 우
                else -> 0.5f // 중
            }
            holder.binding.cardStage.layoutParams = params

            holder.binding.cardStage.setOnClickListener {
                onItemClick(num)
            }
        } else if (holder is DayViewHolder) {
            holder.binding.txtDayNum.text = context.getString(com.example.finalprojectapp.R.string.day_format, num)
            
            holder.binding.cardDay.setOnClickListener {
                onItemClick(num)
            }
        }
    }

    override fun getItemCount(): Int = count
}