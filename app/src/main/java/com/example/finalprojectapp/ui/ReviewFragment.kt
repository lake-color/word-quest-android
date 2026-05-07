package com.example.finalprojectapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectapp.databinding.FragmentReviewBinding
import com.example.finalprojectapp.ui.adapter.ReviewAdapter
import com.example.finalprojectapp.ui.viewmodel.ReviewViewModel

class ReviewFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: ReviewViewModel
    private val reviewAdapter by lazy { ReviewAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화 (fragment-ktx 미설치 대응)
        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvReview.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.reviewWords.observe(viewLifecycleOwner) { words ->
            // 어댑터에 데이터 전달
            reviewAdapter.submitList(words)
            
            // 데이터 유무에 따른 빈 화면 처리
            binding.tvEmpty.isVisible = words.isEmpty()
            binding.rvReview.isVisible = words.isNotEmpty()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}