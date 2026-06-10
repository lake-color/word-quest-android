package com.example.finalprojectapp.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.SettingsManager
import com.example.finalprojectapp.data.SoundManager
import com.example.finalprojectapp.data.WordDatabase
import com.example.finalprojectapp.databinding.SettingsDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast

interface BgmChangeListener {
    fun onBgmChanged(index: Int)
}

class SettingsDialog : DialogFragment() {
    private var _binding: SettingsDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsManager: SettingsManager
    private lateinit var soundManager: SoundManager
    private var bgmChangeListener: BgmChangeListener? = null

    fun setBgmChangeListener(listener: BgmChangeListener) {
        bgmChangeListener = listener
    }

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
                bgmChangeListener?.onBgmChanged(index)
            }
        }

        binding.toggleGroupGameBgm.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                soundManager.playSfx("click")
                val index = if (checkedId == R.id.btnGameBgm1) 1 else 2
                settingsManager.gameBgmIndex = index
            }
        }

        binding.btnResetWrongCount.setOnClickListener {
            soundManager.playSfx("click")
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    WordDatabase.getDatabase(requireContext()).wordDao().resetAllWrongCounts()
                }
                Toast.makeText(requireContext(), "오답 기록이 모두 초기화되었습니다.", Toast.LENGTH_SHORT).show()
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
