package com.example.finalprojectapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.SoundManager
import com.example.finalprojectapp.databinding.FragmentStartBinding

class StartFragment : Fragment() {
    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var soundManager: SoundManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        soundManager = SoundManager.getInstance(requireContext())
        
        binding.btnStart.setOnClickListener {
            soundManager.playSfx("click")
            // HomeActivity의 네비게이션을 호출하거나 직접 프래그먼트 전환
            (activity as? MainActivity)?.navigateToHome()
        }

        binding.btnSet.setOnClickListener {
            soundManager.playSfx("click")
            val dialog = SettingsDialog()
            dialog.setBgmChangeListener(activity as BgmChangeListener)
            dialog.show(parentFragmentManager, "SettingsDialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}