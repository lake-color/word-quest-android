package com.example.finalprojectapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectapp.ui.adapter.StageAdapter
import com.example.finalprojectapp.databinding.FragmentLearnBinding

class LearnFragment : Fragment() {
    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)

        initStageMap()

        return binding.root
    }

    private fun initStageMap() {
        // 일단 20개의 스테이지를 생성
        val adapter = StageAdapter(20)
        binding.rvStageMap.apply {
            layoutManager = LinearLayoutManager(context).apply {
                reverseLayout = true // 아래에서 위로 올라가는 느낌 (선택 사항)
                stackFromEnd = true
            }
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}