package com.example.finalprojectapp.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.SettingsManager
import com.example.finalprojectapp.data.SoundManager
import com.example.finalprojectapp.databinding.SettingsDialogBinding

class SettingsDialog : DialogFragment() {
    private var _binding: SettingsDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsManager: SettingsManager
    private lateinit var soundManager: SoundManager

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
        settingsManager = SettingsManager(requireContext())
        soundManager = SoundManager.getInstance(requireContext())
        
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        binding.seekSound.value = settingsManager.masterVolume.toFloat()
        binding.txtSoundValue.text = settingsManager.masterVolume.toString()

        binding.seekBgm.value = settingsManager.bgmVolume.toFloat()
        binding.txtBgmValue.text = settingsManager.bgmVolume.toString()

        binding.seekSfx.value = settingsManager.sfxVolume.toFloat()
        binding.txtSfxValue.text = settingsManager.sfxVolume.toString()

        binding.switchVibration.isChecked = settingsManager.isVibrationEnabled

        val checkedMainBgmId = if (settingsManager.mainBgmIndex == 1) R.id.btnMainBgm1 else R.id.btnMainBgm2
        binding.toggleGroupMainBgm.check(checkedMainBgmId)

        val checkedGameBgmId = if (settingsManager.gameBgmIndex == 1) R.id.btnGameBgm1 else R.id.btnGameBgm2
        binding.toggleGroupGameBgm.check(checkedGameBgmId)

        val checkedFpsId = when (settingsManager.fps) {
            60 -> R.id.radioFps60
            180 -> R.id.radioFps180
            else -> R.id.radioFps120
        }
        binding.toggleGroupFps.check(checkedFpsId)
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.seekSound.addOnChangeListener { _, value, fromUser ->
            val vol = value.toInt()
            binding.txtSoundValue.text = vol.toString()
            settingsManager.masterVolume = vol
            soundManager.updateVolumes()
        }

        binding.seekBgm.addOnChangeListener { _, value, fromUser ->
            val vol = value.toInt()
            binding.txtBgmValue.text = vol.toString()
            settingsManager.bgmVolume = vol
            soundManager.updateVolumes()
        }

        binding.seekSfx.addOnChangeListener { _, value, fromUser ->
            val vol = value.toInt()
            binding.txtSfxValue.text = vol.toString()
            settingsManager.sfxVolume = vol
            soundManager.updateVolumes()
        }
        
        binding.toggleGroupFps.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val fps = when (checkedId) {
                    R.id.radioFps60 -> 60
                    R.id.radioFps180 -> 180
                    else -> 120
                }
                settingsManager.fps = fps
            }
        }

        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.isVibrationEnabled = isChecked
        }

        binding.toggleGroupMainBgm.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val index = if (checkedId == R.id.btnMainBgm1) 1 else 2
                settingsManager.mainBgmIndex = index
                // 현재 메인 화면이나 학습 화면일 경우 BGM 즉시 변경
                if (activity is MainActivity || activity is HomeActivity || activity is StudyActivity) {
                    val bgmName = if (index == 1) "bgm_main" else "bgm_main2"
                    soundManager.playBgm(bgmName)
                }
            }
        }

        binding.toggleGroupGameBgm.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val index = if (checkedId == R.id.btnGameBgm1) 1 else 2
                settingsManager.gameBgmIndex = index
            }
        }
    }

    override fun onStart() {
        super.onStart()
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
