package com.example.finalprojectapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectapp.databinding.FragmentWordbookBinding
import com.example.finalprojectapp.ui.adapter.WordbookAdapter
import com.example.finalprojectapp.ui.viewmodel.WordbookViewModel
import com.google.android.material.tabs.TabLayout

class WordbookFragment : Fragment() {
    private var _binding: FragmentWordbookBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WordbookViewModel
    private val adapter by lazy { WordbookAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordbookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[WordbookViewModel::class.java]

        setupRecyclerView()
        setupTabLayout()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvWordbook.layoutManager = LinearLayoutManager(context)
        binding.rvWordbook.adapter = adapter
    }

    private fun setupTabLayout() {
        binding.tabLayoutGroups.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.setStageRange(1, 5)
                    1 -> viewModel.setStageRange(6, 10)
                    2 -> viewModel.setStageRange(11, 15)
                    3 -> viewModel.setStageRange(16, 20)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.words.observe(viewLifecycleOwner) { wordList ->
            adapter.submitList(wordList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}