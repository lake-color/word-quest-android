package com.example.finalprojectapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectapp.databinding.ActivityWordListBinding
import com.example.finalprojectapp.ui.adapter.WordListAdapter
import com.example.finalprojectapp.ui.viewmodel.WordListViewModel

class WordListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWordListBinding
    private lateinit var viewModel: WordListViewModel
    private val adapter by lazy { 
        WordListAdapter { word ->
            viewModel.toggleMemorized(word)
        } 
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        viewModel = ViewModelProvider(this)[WordListViewModel::class.java]

        val day = intent.getIntExtra("DAY_NUM", 1)
        binding.toolbar.title = "Day $day Words"
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        observeViewModel()
        
        viewModel.loadWords(day)
    }

    private fun setupRecyclerView() {
        binding.rvWords.layoutManager = LinearLayoutManager(this)
        binding.rvWords.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.words.observe(this) { wordList ->
            adapter.submitList(wordList)
        }
    }
}
