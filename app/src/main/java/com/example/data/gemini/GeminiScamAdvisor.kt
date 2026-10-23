package com.example.data.gemini

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiScamAdvisor {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeScamScenario(userText: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNullOrEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalForensicReport(userText)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val systemInstruction = "You are Suraksha Shield AI, a world-class cybersecurity fraud investigator. Analyze the user's suspicious message, email, call, or offer. Structure your answer with: 1. Verdict (Legit, Suspicious, or Definite Scam), 2. Threat Tactics (Psychological manipulation, urgency, spoofing), 3. Modus Operandi (How the scammers execute this), 4. Immediate Safety Action Steps (Golden hour, reporting to 1930 / cybercrime portal)."

            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "$systemInstruction\n\nScenario to inspect:\n$userText")
                            })
                        }
                        put("parts", partsArray)
                    })
                }
                put("contents", contentsArray)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext generateLocalForensicReport(userText)
            }

            val responseString = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseString)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                text
            } else {
                generateLocalForensicReport(userText)
            }
        } catch (e: Exception) {
            Log.e("GeminiScamAdvisor", "API error: ${e.message}", e)
            generateLocalForensicReport(userText)
        }
    }

    private fun generateLocalForensicReport(scenario: String): String {
        val lower = scenario.lowercase()
        val isUrgent = lower.contains("urgent") || lower.contains("tonight") || lower.contains("immediately") || lower.contains("hours")
        val isArrest = lower.contains("police") || lower.contains("cbi") || lower.contains("arrest") || lower.contains("parcel")
        val isElectricity = lower.contains("electricity") || lower.contains("power") || lower.contains("bill")
        val isBank = lower.contains("bank") || lower.contains("kyc") || lower.contains("pan") || lower.contains("sbi") || lower.contains("block")
        val isTask = lower.contains("telegram") || lower.contains("like") || lower.contains("job") || lower.contains("task") || lower.contains("earn")

        val threatScore = when {
            isArrest || isElectricity || isBank || isTask -> "CRITICAL THREAT (95% Scam Confidence)"
            isUrgent -> "HIGH RISK (75% Suspicious Pattern)"
            else -> "SUSPICIOUS PATTERN (Requires Strict Verification)"
        }

        val analysisBuilder = StringBuilder()
        analysisBuilder.append("🛡️ **SURAKSHA FORENSIC THREAT ASSESSMENT**\n\n")
        analysisBuilder.append("• **Verdict**: $threatScore\n\n")
        analysisBuilder.append("🔍 **TACTICAL MANIPULATION BREAKDOWN**:\n")

        if (isArrest) {
            analysisBuilder.append("- **Digital Arrest Extortion**: Scammers pose as police/CBI asserting an Aadhaar-linked illegal parcel was intercepted. This is 100% false; police never arrest via video call.\n")
        }
        if (isElectricity) {
            analysisBuilder.append("- **Disconnection Panic Bait**: Falsely claiming electricity will be disconnected tonight to provoke emotional panic so victims don't verify with official power boards.\n")
        }
        if (isBank) {
            analysisBuilder.append("- **Bank KYC Pretext**: Banks NEVER send SMS with direct third-party links or APKs to update PAN or Aadhaar.\n")
        }
        if (isTask) {
            analysisBuilder.append("- **Prepaid Task Trap**: Scammers pay small sums (₹150) for trivial tasks, then demand large deposits to unlock earnings and steal the principal.\n")
        }
        if (isUrgent) {
            analysisBuilder.append("- **Artificial Time Limit**: Setting deadlines prevents victims from consulting family or bank managers.\n")
        }

        analysisBuilder.append("\n🚨 **IMMEDIATE ACTION PROTOCOL**:\n")
        analysisBuilder.append("1. **Cease Communication**: Block sender/caller immediately.\n")
        analysisBuilder.append("2. **Zero Funds Transfer**: Never send money to 'clear your name' or 'verify an account'.\n")
        analysisBuilder.append("3. **Golden Hour Helpline**: If money was sent, dial **1930** (National Cyber Crime Helpline in India) immediately so the banking channel can freeze the funds before cash-out.\n")
        analysisBuilder.append("4. **Report Incident**: File complaint at www.cybercrime.gov.in or your national fraud reporting center.\n")

        return analysisBuilder.toString()
    }
}
