package com.example.finalprojectapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.finalprojectapp.databinding.FragmentSimpleBinding

class EquipFragment : Fragment() {
    private var _binding: FragmentSimpleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSimpleBinding.inflate(inflater, container, false)
        binding.txtTitle.text = getString(R.string.nav_equip)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}