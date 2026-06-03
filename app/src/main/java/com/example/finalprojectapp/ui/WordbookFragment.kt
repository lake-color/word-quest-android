package com.example.finalprojectapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectapp.databinding.FragmentWordbookBinding
import com.example.finalprojectapp.ui.adapter.DayAdapter

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
        binding.rvWordbook.adapter = DayAdapter(20)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
