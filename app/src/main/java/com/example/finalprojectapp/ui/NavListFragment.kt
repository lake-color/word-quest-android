package com.example.finalprojectapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectapp.R
import com.example.finalprojectapp.databinding.FragmentNavListBinding
import com.example.finalprojectapp.ui.adapter.StageDayAdapter

class NavListFragment : Fragment() {
    private var _binding: FragmentNavListBinding? = null
    private val binding get() = _binding!!

    // 모드 구분 (true: 학습/스테이지, false: 단어장/Day)
    private var isStageMode: Boolean = true

    companion object {
        fun newInstance(isStageMode: Boolean): NavListFragment {
            return NavListFragment().apply {
                arguments = Bundle().apply { putBoolean("IS_STAGE_MODE", isStageMode) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isStageMode = arguments?.getBoolean("IS_STAGE_MODE") ?: true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNavListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 모드에 따른 배경화면 노출 여부
        binding.imgNavBackground.isVisible = isStageMode

        binding.rvNavList.apply {
            layoutManager = LinearLayoutManager(context).apply {
                if (isStageMode) {
                    reverseLayout = true
                    stackFromEnd = true
                }
            }
            adapter = StageDayAdapter(20, isStageMode) { num ->
                val fragment = if (isStageMode) {
                    StudyFragment().apply { arguments = Bundle().apply { putInt("STAGE_NUM", num) } }
                } else {
                    WordListFragment().apply { arguments = Bundle().apply { putInt("DAY_NUM", num) } }
                }
                
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.home_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}