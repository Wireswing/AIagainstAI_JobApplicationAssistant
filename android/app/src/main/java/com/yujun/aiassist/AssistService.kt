package com.yujun.aiassist

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView

class AssistService : AccessibilityService() {

    private var lastQuestion = ""
    private var generating = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceInfo = serviceInfo.apply {
            notificationTimeout = 800
            flags = flags or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val pkg = event.packageName?.toString() ?: return
        if (!pkg.contains("job51")) return
        val prefs = AppPrefs(this)
        if (!prefs.overlayEnabled) return

        val text = collectText(rootInActiveWindow)
        if (text.isBlank() || text == lastQuestion) return
        if (!looksLikeQuestion(text)) return

        lastQuestion = text
        prefs.lastQuestion = text
        showOverlay("识别到问题：\n$text\n\n正在生成回答…")
        if (!generating) {
            generating = true
            val handler = Handler(Looper.getMainLooper())
            Thread {
                val ans = LlmClient.generate(text, prefs)
                prefs.lastAnswer = ans
                handler.post { showOverlay("问题：$text\n\n---\n$ans") }
                generating = false
            }.start()
        }
    }

    private fun looksLikeQuestion(t: String): Boolean {
        return t.contains("?") || t.contains("？") ||
               t.startsWith("请") || t.contains("介绍") || t.contains("为什么") ||
               t.contains("什么") || t.contains("如何")
    }

    private fun collectText(node: AccessibilityNodeInfo?): String {
        if (node == null) return ""
        val sb = StringBuilder()
        val q = ArrayDeque<AccessibilityNodeInfo>()
        q.add(node)
        while (q.isNotEmpty()) {
            val n = q.removeFirst()
            val txt = n.text?.toString()
            if (!txt.isNullOrBlank()) sb.append(txt).append(" ")
            for (i in 0 until n.childCount) n.getChild(i)?.let { q.add(it) }
        }
        return sb.toString().trim()
    }

    private fun showOverlay(text: String) {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val tv = TextView(this).apply {
            this.text = text
            textSize = 13f
            setBackgroundColor(0xCCFFFFFF.toInt())
            setTextColor(0xFF222222.toInt())
            setPadding(28, 24, 28, 24)
        }
        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply { x = 40; y = 160 }
        try { wm.addView(tv, lp) } catch (e: Exception) { }
    }

    override fun onInterrupt() {}
}
