package com.example.finalprojectapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectapp.R
import com.example.finalprojectapp.databinding.FragmentLearnBinding
import com.example.finalprojectapp.ui.adapter.StageDayAdapter

class LearnFragment : Fragment() {
    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)

        initStageMap()

        return binding.root
    }

    private fun initStageMap() {
        // 통합 어댑터 사용 (모드: true = Stage)
        val adapter = StageDayAdapter(20, true) { num ->
            // StudyFragment로 이동
            val fragment = StudyFragment().apply {
                arguments = Bundle().apply { putInt("STAGE_NUM", num) }
            }
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.home_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.rvStageMap.apply {
            layoutManager = LinearLayoutManager(context).apply {
                reverseLayout = true
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