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

        // --- 연결선 로직 전면 재작성 (Midpoint Translation 방식) ---
        if (position < stageCount - 1) {
            holder.binding.lineNext.visibility = View.VISIBLE
            holder.itemView.post {
                val line = holder.binding.lineNext
                val card = holder.binding.cardStage
                
                val parentWidth = holder.itemView.width
                val itemHeight = holder.itemView.height
                val cardWidth = card.width
                
                if (parentWidth == 0 || itemHeight == 0) return@post

                val currentBias = getBiasForPosition(position)
                val nextBias = getBiasForPosition(position + 1)
                
                // 1. 각 노드의 중심점 좌표 (startX, startY는 현재 카드의 중심 = 0,0 기준)
                // 현재 카드의 중심 (절대 좌표계 아님, itemView 기준)
                val startX = currentBias * (parentWidth - cardWidth) + (cardWidth / 2f)
                val startY = itemHeight / 2f
                
                // 다음 카드의 중심점 (itemView 하나 위(-Y)에 있다고 가정)
                val endX = nextBias * (parentWidth - cardWidth) + (cardWidth / 2f)
                val endY = -itemHeight / 2f
                
                // 2. 벡터 및 길이 계산
                val dx = endX - startX
                val dy = endY - startY
                // 길이를 1.1배로 늘려 다음 노드 안쪽으로 깊숙이 들어가게 함
                val length = sqrt(dx * dx + dy * dy) * 1.1f
                val angle = Math.toDegrees(atan2(dx.toDouble(), -dy.toDouble())).toFloat()

                // 3. 라인 속성 설정
                val lp = line.layoutParams
                lp.height = length.toInt()
                line.layoutParams = lp
                
                // 중앙 기준으로 회전 (기본 Pivot은 Center)
                line.pivotX = lp.width / 2f
                line.pivotY = length / 2f
                
                // 현재 카드 중심(0,0)에서 두 카드 사이의 중점으로 이동
                line.translationX = dx / 2f
                line.translationY = dy / 2f
                
                line.rotation = angle
            }
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
