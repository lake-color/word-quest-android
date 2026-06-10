package com.example.finalprojectapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectapp.databinding.FragmentWordbookBinding
import com.example.finalprojectapp.ui.adapter.StageDayAdapter

class WordbookFragment : Fragment() {
    private var _binding: FragmentWordbookBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordbookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvWordbook.layoutManager = LinearLayoutManager(context)
        // 통합 어댑터 사용 (모드: false = Day)
        binding.rvWordbook.adapter = StageDayAdapter(20, false) { num ->
            // WordListFragment로 이동
            val fragment = WordListFragment().apply {
                arguments = Bundle().apply { putInt("DAY_NUM", num) }
            }
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(com.example.finalprojectapp.R.id.home_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
