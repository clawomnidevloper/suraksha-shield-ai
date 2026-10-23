package com.example.data.local

data class ScamReportEntity(
    val id: Long = 0,
    val type: String, // "PHONE", "UPI", "URL", "APK", "TASK_SCAM", "DIGITAL_ARREST"
    val target: String, // e.g. "+923019842104", "electricity.desk@okhdfc", "sbi-kyc-verify.top"
    val title: String,
    val description: String,
    val threatLevel: String, // "CRITICAL", "HIGH", "MODERATE"
    val reportCount: Int = 1,
    val reportedAt: Long = System.currentTimeMillis(),
    val tags: String = "",
    val isUserReported: Boolean = false
)
