package com.mathbank.ui.activities

import android.os.Bundle
import android.text.InputType
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
            val apiKey = settingsManager.getApiKey()
            if (apiKey.isNotEmpty()) {
                binding.etApiKey.setText("sk-or-..." + apiKey.takeLast(6))
            }
        }
    }

    private fun setupUI() {
        var isApiKeyVisible = false
        binding.btnToggleApiKey.setOnClickListener {
            isApiKeyVisible = !isApiKeyVisible
            lifecycleScope.launch {
                val key = settingsManager.getApiKey()
                binding.etApiKey.setText(
                    if (isApiKeyVisible) key else "sk-or-..." + key.takeLast(6)
                )
                binding.etApiKey.inputType = if (isApiKeyVisible)
                    InputType.TYPE_CLASS_TEXT
                else
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        binding.btnSaveApiKey.setOnClickListener {
            val key = binding.etApiKey.text.toString().trim()
            if (key.length > 10) {
                lifecycleScope.launch {
                    settingsManager.saveApiKey(key)
                    Toast.makeText(
                        this@SettingsActivity,
                        "API anahtari kaydedildi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "Gecersiz API anahtari", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener { finish() }
    }
}
