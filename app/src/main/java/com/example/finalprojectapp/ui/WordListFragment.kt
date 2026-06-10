package com.example.finalprojectapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectapp.R
import com.example.finalprojectapp.databinding.FragmentWordListBinding
import com.example.finalprojectapp.ui.adapter.WordListAdapter
import com.example.finalprojectapp.ui.viewmodel.MainViewModel

class WordListFragment : Fragment() {
    private var _binding: FragmentWordListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: MainViewModel
    private val adapter by lazy { 
        WordListAdapter { word ->
            viewModel.toggleMemorized(word)
        } 
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWordListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        val day = arguments?.getInt("DAY_NUM") ?: 1
        binding.toolbar.title = getString(R.string.day_words_format, day)
        binding.toolbar.setNavigationOnClickListener { 
            parentFragmentManager.popBackStack()
        }

        setupRecyclerView()
        observeViewModel()
        
        viewModel.loadWordsByDay(day)
    }

    private fun setupRecyclerView() {
        binding.rvWords.layoutManager = LinearLayoutManager(context)
        binding.rvWords.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.currentWords.observe(viewLifecycleOwner) { wordList ->
            adapter.submitList(wordList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}