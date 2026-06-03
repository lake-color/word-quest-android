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

        val checkedMainBgmId = when (settingsManager.mainBgmIndex) {
            1 -> R.id.btnMainBgm1
            2 -> R.id.btnMainBgm2
            else -> R.id.btnMainBgm3
        }
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
            soundManager.playSfx("click")
            dismiss()
        }

        binding.seekSound.addOnChangeListener { _, value, fromUser ->
            val vol = value.toInt()
            binding.txtSoundValue.text = vol.toString()
            settingsManager.masterVolume = vol
            soundManager.updateVolumes()
            if (fromUser) soundManager.playSfx("click")
        }

        binding.seekBgm.addOnChangeListener { _, value, fromUser ->
            val vol = value.toInt()
            binding.txtBgmValue.text = vol.toString()
            settingsManager.bgmVolume = vol
            soundManager.updateVolumes()
            if (fromUser) soundManager.playSfx("click")
        }

        binding.seekSfx.addOnChangeListener { _, value, fromUser ->
            val vol = value.toInt()
            binding.txtSfxValue.text = vol.toString()
            settingsManager.sfxVolume = vol
            soundManager.updateVolumes()
            if (fromUser) soundManager.playSfx("click")
        }
        
        binding.toggleGroupFps.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                soundManager.playSfx("click")
                val fps = when (checkedId) {
                    R.id.radioFps60 -> 60
                    R.id.radioFps180 -> 180
                    else -> 120
                }
                settingsManager.fps = fps
                settingsManager.applySettings(requireActivity())
            }
        }

        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            soundManager.playSfx("click")
            settingsManager.isVibrationEnabled = isChecked
        }

        binding.toggleGroupMainBgm.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                soundManager.playSfx("click")
                val index = when (checkedId) {
                    R.id.btnMainBgm1 -> 1
                    R.id.btnMainBgm2 -> 2
                    else -> 3
                }
                settingsManager.mainBgmIndex = index
                if (activity is MainActivity || activity is HomeActivity || activity is StudyActivity) {
                    val bgmName = when (index) {
                        1 -> "bgm_main"
                        2 -> "bgm_main2"
                        else -> "bgm_main3"
                    }
                    soundManager.playBgm(bgmName)
                }
            }
        }

        binding.toggleGroupGameBgm.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                soundManager.playSfx("click")
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
