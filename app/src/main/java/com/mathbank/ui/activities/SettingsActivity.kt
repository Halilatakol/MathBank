package com.mathbank.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mathbank.databinding.ActivitySettingsBinding
import com.mathbank.data.repository.SettingsManager
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settingsManager = SettingsManager(this)
        loadSettings()
        setupUI()
    }

    private fun loadSettings() {
        lifecycleScope.launch {
            val orKey = settingsManager.getOpenRouterKey()
            val gemKey = settingsManager.getGeminiKey()
            val selectedModel = settingsManager.getSelectedModel()

            if (orKey.isNotEmpty()) {
                binding.etOpenRouterKey.setText("sk-or-..." + orKey.takeLast(6))
            }
            if (gemKey.isNotEmpty()) {
                binding.etGeminiKey.setText("AIza..." + gemKey.takeLast(6))
            }
            if (selectedModel == SettingsManager.MODEL_GEMINI) {
                binding.radioGemini.isChecked = true
            } else {
                binding.radioOpenRouter.isChecked = true
            }
        }
    }

    private fun setupUI() {
        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                val orKey = binding.etOpenRouterKey.text.toString().trim()
                val gemKey = binding.etGeminiKey.text.toString().trim()

                if (orKey.isNotEmpty() && !orKey.startsWith("sk-or-...")) {
                    settingsManager.saveOpenRouterKey(orKey)
                }
                if (gemKey.isNotEmpty() && !gemKey.startsWith("AIza...")) {
                    settingsManager.saveGeminiKey(gemKey)
                }

                val selectedModel = if (binding.radioGemini.isChecked) {
                    SettingsManager.MODEL_GEMINI
                } else {
                    SettingsManager.MODEL_OPENROUTER
                }
                settingsManager.saveSelectedModel(selectedModel)

                Toast.makeText(
                    this@SettingsActivity,
                    "Ayarlar kaydedildi",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
        binding.btnBack.setOnClickListener { finish() }
    }
}
