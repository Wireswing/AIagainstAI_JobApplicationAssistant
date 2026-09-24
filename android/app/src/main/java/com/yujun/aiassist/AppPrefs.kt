package com.yujun.aiassist

import android.content.Context
import android.content.SharedPreferences

class AppPrefs(context: Context) {
    private val sp: SharedPreferences = context.getSharedPreferences("aiassist", Context.MODE_PRIVATE)
    var apiKey: String
        get() = sp.getString("api_key", "") ?: ""
        set(v) = sp.edit().putString("api_key", v).apply()
    var baseUrl: String
        get() = sp.getString("base_url", "https://api.deepseek.com/v1") ?: "https://api.deepseek.com/v1"
        set(v) = sp.edit().putString("base_url", v).apply()
    var model: String
        get() = sp.getString("model", "deepseek-chat") ?: "deepseek-chat"
        set(v) = sp.edit().putString("model", v).apply()
    var overlayEnabled: Boolean
        get() = sp.getBoolean("overlay_enabled", true)
        set(v) = sp.edit().putBoolean("overlay_enabled", v).apply()
    var lastAnswer: String
        get() = sp.getString("last_answer", "") ?: ""
        set(v) = sp.edit().putString("last_answer", v).apply()
    var lastQuestion: String
        get() = sp.getString("last_question", "") ?: ""
        set(v) = sp.edit().putString("last_question", v).apply()
}
