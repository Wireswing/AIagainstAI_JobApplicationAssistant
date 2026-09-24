package com.yujun.aiassist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val sp = AppPrefs(this)
        val etKey = findViewById<EditText>(R.id.etKey)
        val etBase = findViewById<EditText>(R.id.etBase)
        val etModel = findViewById<EditText>(R.id.etModel)
        val swOverlay = findViewById<Switch>(R.id.swOverlay)

        etKey.setText(sp.apiKey)
        etBase.setText(sp.baseUrl)
        etModel.setText(sp.model)
        swOverlay.isChecked = sp.overlayEnabled

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            sp.apiKey = etKey.text.toString().trim()
            sp.baseUrl = etBase.text.toString().trim()
            sp.model = etModel.text.toString().trim()
            sp.overlayEnabled = swOverlay.isChecked
            Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show()
            finish()
        }
        findViewById<Button>(R.id.btnTest).setOnClickListener {
            sp.apiKey = etKey.text.toString().trim()
            sp.baseUrl = etBase.text.toString().trim()
            sp.model = etModel.text.toString().trim()
            Thread {
                val r = LlmClient.generate("请简单介绍一下你自己", sp)
                runOnUiThread { findViewById<android.widget.TextView>(R.id.tvTrResult).text = r }
            }.start()
        }
    }
}
