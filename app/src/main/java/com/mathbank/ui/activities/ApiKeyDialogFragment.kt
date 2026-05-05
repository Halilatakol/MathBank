package com.mathbank.ui.activities

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.mathbank.R
import com.mathbank.data.repository.SettingsManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ApiKeyDialogFragment : DialogFragment() {

    var onKeySaved: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_api_key, null)
        val etApiKey = view.findViewById<TextInputEditText>(R.id.etApiKey)
        val settingsManager = SettingsManager(requireContext())

        return AlertDialog.Builder(requireContext())
            .setTitle("OpenRouter API Anahtari")
            .setMessage("openrouter.ai adresinden ucretsiz alabilirsiniz.")
            .setView(view)
            .setPositiveButton("Kaydet") { _, _ ->
                val key = etApiKey.text.toString().trim()
                if (key.length > 10) {
                    lifecycleScope.launch {
                        settingsManager.saveApiKey(key)
                        Toast.makeText(
                            requireContext(),
                            "Kaydedildi",
                            Toast.LENGTH_SHORT
                        ).show()
                        onKeySaved?.invoke()
                    }
                } else {
                    Toast.makeText(requireContext(), "Gecersiz anahtar", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Iptal", null)
            .create()
    }
}
