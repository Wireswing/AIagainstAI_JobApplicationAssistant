package com.yujun.aiassist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = AppPrefs(this)

        findViewById<Button>(R.id.btnAccessibility).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        findViewById<Button>(R.id.btnOverlay).setOnClickListener {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")))
        }
        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        findViewById<Button>(R.id.btnRefresh).setOnClickListener { refresh(prefs) }
        refresh(prefs)
    }

    private fun refresh(prefs: AppPrefs) {
        val msg = StringBuilder()
        msg.append("悬浮窗：").append(if (prefs.overlayEnabled) "开" else "关").append("\n")
        msg.append("模型：").append(prefs.model).append("\n")
        msg.append("Base URL：").append(prefs.baseUrl).append("\n")
        msg.append("Key 已配置：").append(if (prefs.apiKey.isNotEmpty()) "是" else "否").append("\n\n")
        msg.append("最近问题：").append(prefs.lastQuestion.ifBlank { "无" }).append("\n\n")
        msg.append("最近回答：").append(prefs.lastAnswer.ifBlank { "等待面试问题…" })
        findViewById<TextView>(R.id.tvStatus).text = msg.toString()
    }
}
