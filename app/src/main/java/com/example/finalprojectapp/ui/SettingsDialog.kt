package com.example.finalprojectapp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.SettingsManager
import com.example.finalprojectapp.data.SoundManager
import com.example.finalprojectapp.databinding.SettingsDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        binding.seekSound.value = settingsManager.masterVolume.toFloat()
        binding.txtSoundValue.text = getString(R.string.number_format, settingsManager.masterVolume)

        binding.seekBgm.value = settingsManager.bgmVolume.toFloat()
        binding.txtBgmValue.text = getString(R.string.number_format, settingsManager.bgmVolume)

        binding.seekSfx.value = settingsManager.sfxVolume.toFloat()
        binding.txtSfxValue.text = getString(R.string.number_format, settingsManager.sfxVolume)

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
            binding.txtSoundValue.text = getString(R.string.number_format, vol)
            settingsManager.masterVolume = vol
            soundManager.updateVolumes()
            if (fromUser) soundManager.playSfx("click")
        }

        binding.seekBgm.addOnChangeListener { _, value, fromUser ->
            val vol = value.toInt()
            binding.txtBgmValue.text = getString(R.string.number_format, vol)
            settingsManager.bgmVolume = vol
            soundManager.updateVolumes()
            if (fromUser) soundManager.playSfx("click")
        }

        binding.seekSfx.addOnChangeListener { _, value, fromUser ->
            val vol = value.toInt()
            binding.txtSfxValue.text = getString(R.string.number_format, vol)
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
                    com.example.finalprojectapp.data.WordDatabase.getDatabase(requireContext()).wordDao().resetAllWrongCounts()
                }
                android.widget.Toast.makeText(requireContext(), getString(R.string.reset_complete), android.widget.Toast.LENGTH_SHORT).show()
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
