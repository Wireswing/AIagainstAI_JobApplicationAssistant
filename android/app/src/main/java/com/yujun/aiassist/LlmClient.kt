package com.yujun.aiassist

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object LlmClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM = """你是「余君」的面试应答助手。根据面试官提出的问题，结合候选人的真实背景，生成自然、简洁、自信的中文口语化回答。要求：1) 控制在80-150字 2) 用第一人称 3) 不吹牛、不堆术语 4) 突出最相关的1-2个亮点 5) 若问题是敏感项（如薪资），按预设口径回答。"""

    fun generate(question: String, cfg: AppPrefs): String {
        if (cfg.apiKey.isBlank()) return "请先在设置中填入 API Key"
        val sys = SYSTEM + "\n\n候选人背景：\n" + CvData.profile
        val root = JSONObject()
        root.put("model", cfg.model)
        val msgs = JSONArray()
        msgs.put(JSONObject().put("role", "system").put("content", sys))
        msgs.put(JSONObject().put("role", "user").put("content", "面试官问题：$question\n请给出回答。"))
        root.put("messages", msgs)
        root.put("temperature", 0.4)
        root.put("max_tokens", 300)

        val url = cfg.baseUrl.trimEnd('/') + "/chat/completions"
        val body = root.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val req = Request.Builder().url(url)
            .header("Authorization", "Bearer " + cfg.apiKey)
            .header("Content-Type", "application/json")
            .post(body).build()
        return try {
            client.newCall(req).execute().use { r ->
                val text = r.body?.string() ?: return "网络返回为空"
                if (!r.isSuccessful) return "请求失败 HTTP " + r.code + "：" + text.take(200)
                val jo = JSONObject(text)
                jo.getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getString("content").trim()
            }
        } catch (e: Exception) {
            "调用出错：" + e.message
        }
    }
}
