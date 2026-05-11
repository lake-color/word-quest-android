package com.example.finalprojectapp.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.finalprojectapp.databinding.SettingsDialogBinding

class SettingsDialog : DialogFragment() {
    private var _binding: SettingsDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 다이얼로그 배경 투명 처리 (MaterialCardView의 둥근 모서리를 살리기 위함)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setupListeners()
    }

    private fun setupListeners() {
        // 닫기 버튼
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        // Material 3 Slider 리스너 설정
        binding.seekSound.addOnChangeListener { _, value, _ ->
            binding.txtSoundValue.text = value.toInt().toString()
        }

        binding.seekBgm.addOnChangeListener { _, value, _ ->
            binding.txtBgmValue.text = value.toInt().toString()
        }

        binding.seekSfx.addOnChangeListener { _, value, _ ->
            binding.txtSfxValue.text = value.toInt().toString()
        }
        
        // FPS 토글 버튼 그룹 리스너 (필요 시 구현)
        binding.toggleGroupFps.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                // 선택된 FPS에 따른 로직 처리
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그 너비를 화면의 90%로 설정
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}