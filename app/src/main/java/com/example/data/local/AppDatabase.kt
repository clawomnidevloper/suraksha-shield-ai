package com.example.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppDatabase private constructor(context: Context) {
    private val helper = DatabaseHelper(context.applicationContext)
    private val dao = SqliteScamReportDao(helper)

    fun scamReportDao(): ScamReportDao = dao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppDatabase(context).also { INSTANCE = it }
            }
        }
    }

    private class DatabaseHelper(context: Context) : SQLiteOpenHelper(
        context,
        "suraksha_scam_guard.db",
        null,
        1
    ) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS scam_reports (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type TEXT NOT NULL,
                    target TEXT NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    threatLevel TEXT NOT NULL,
                    reportCount INTEGER NOT NULL,
                    reportedAt INTEGER NOT NULL,
                    tags TEXT NOT NULL,
                    isUserReported INTEGER NOT NULL
                )
                """.trimIndent()
            )
            // Seed initial reports
            val seedReports = getInitialSeedReports()
            db.beginTransaction()
            try {
                for (report in seedReports) {
                    val values = ContentValues().apply {
                        put("type", report.type)
                        put("target", report.target)
                        put("title", report.title)
                        put("description", report.description)
                        put("threatLevel", report.threatLevel)
                        put("reportCount", report.reportCount)
                        put("reportedAt", report.reportedAt)
                        put("tags", report.tags)
                        put("isUserReported", if (report.isUserReported) 1 else 0)
                    }
                    db.insert("scam_reports", null, values)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS scam_reports")
            onCreate(db)
        }
    }

    private class SqliteScamReportDao(private val helper: DatabaseHelper) : ScamReportDao {
        private val _reportsFlow = MutableStateFlow<List<ScamReportEntity>>(emptyList())

        init {
            CoroutineScope(Dispatchers.IO).launch {
                refresh()
            }
        }

        private fun refresh() {
            val list = queryAll()
            _reportsFlow.value = list
        }

        private fun queryAll(): List<ScamReportEntity> {
            val db = helper.readableDatabase
            val cursor = db.query(
                "scam_reports",
                null,
                null,
                null,
                null,
                null,
                "reportedAt DESC"
            )
            val list = mutableListOf<ScamReportEntity>()
            cursor.use { c ->
                while (c.moveToNext()) {
                    list.add(cursorToEntity(c))
                }
            }
            return list
        }

        private fun cursorToEntity(c: Cursor): ScamReportEntity {
            return ScamReportEntity(
                id = c.getLong(c.getColumnIndexOrThrow("id")),
                type = c.getString(c.getColumnIndexOrThrow("type")),
                target = c.getString(c.getColumnIndexOrThrow("target")),
                title = c.getString(c.getColumnIndexOrThrow("title")),
                description = c.getString(c.getColumnIndexOrThrow("description")),
                threatLevel = c.getString(c.getColumnIndexOrThrow("threatLevel")),
                reportCount = c.getInt(c.getColumnIndexOrThrow("reportCount")),
                reportedAt = c.getLong(c.getColumnIndexOrThrow("reportedAt")),
                tags = c.getString(c.getColumnIndexOrThrow("tags")),
                isUserReported = c.getInt(c.getColumnIndexOrThrow("isUserReported")) == 1
            )
        }

        override fun getAllReports(): Flow<List<ScamReportEntity>> = _reportsFlow.asStateFlow()

        override fun searchReports(query: String): Flow<List<ScamReportEntity>> {
            return _reportsFlow.map { list ->
                if (query.isBlank()) list
                else list.filter {
                    it.target.contains(query, ignoreCase = true) ||
                    it.title.contains(query, ignoreCase = true) ||
                    it.tags.contains(query, ignoreCase = true)
                }
            }
        }

        override fun getReportsByType(type: String): Flow<List<ScamReportEntity>> {
            return _reportsFlow.map { list ->
                if (type.equals("ALL", ignoreCase = true)) list
                else list.filter { it.type.equals(type, ignoreCase = true) }
            }
        }

        override suspend fun findExactMatch(target: String): ScamReportEntity? = withContext(Dispatchers.IO) {
            val db = helper.readableDatabase
            val cursor = db.query(
                "scam_reports",
                null,
                "LOWER(target) = LOWER(?)",
                arrayOf(target),
                null,
                null,
                null,
                "1"
            )
            cursor.use {
                if (it.moveToFirst()) cursorToEntity(it) else null
            }
        }

        override suspend fun findPartialMatch(target: String): ScamReportEntity? = withContext(Dispatchers.IO) {
            val db = helper.readableDatabase
            val cursor = db.query(
                "scam_reports",
                null,
                "LOWER(?) LIKE '%' || LOWER(target) || '%' OR LOWER(target) LIKE '%' || LOWER(?) || '%'",
                arrayOf(target, target),
                null,
                null,
                null,
                "1"
            )
            cursor.use {
                if (it.moveToFirst()) cursorToEntity(it) else null
            }
        }

        override suspend fun insertReport(report: ScamReportEntity): Long = withContext(Dispatchers.IO) {
            val db = helper.writableDatabase
            val values = ContentValues().apply {
                put("type", report.type)
                put("target", report.target)
                put("title", report.title)
                put("description", report.description)
                put("threatLevel", report.threatLevel)
                put("reportCount", report.reportCount)
                put("reportedAt", report.reportedAt)
                put("tags", report.tags)
                put("isUserReported", if (report.isUserReported) 1 else 0)
            }
            val id = db.insert("scam_reports", null, values)
            refresh()
            id
        }

        override suspend fun insertReports(reports: List<ScamReportEntity>) = withContext(Dispatchers.IO) {
            val db = helper.writableDatabase
            db.beginTransaction()
            try {
                for (report in reports) {
                    val values = ContentValues().apply {
                        put("type", report.type)
                        put("target", report.target)
                        put("title", report.title)
                        put("description", report.description)
                        put("threatLevel", report.threatLevel)
                        put("reportCount", report.reportCount)
                        put("reportedAt", report.reportedAt)
                        put("tags", report.tags)
                        put("isUserReported", if (report.isUserReported) 1 else 0)
                    }
                    db.insert("scam_reports", null, values)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
            refresh()
        }

        override suspend fun upvoteReport(id: Long) = withContext(Dispatchers.IO) {
            val db = helper.writableDatabase
            db.execSQL("UPDATE scam_reports SET reportCount = reportCount + 1 WHERE id = ?", arrayOf(id))
            refresh()
        }

        override suspend fun getCount(): Int = withContext(Dispatchers.IO) {
            val db = helper.readableDatabase
            val cursor = db.rawQuery("SELECT COUNT(*) FROM scam_reports", null)
            cursor.use {
                if (it.moveToFirst()) it.getInt(0) else 0
            }
        }
    }
}

fun getInitialSeedReports(): List<ScamReportEntity> = listOf(
    ScamReportEntity(
        type = "PHONE",
        target = "+923019842104",
        title = "Fake KBC / WhatsApp Lottery Call",
        description = "Scammer calls claiming recipient won ₹25 Lakhs in Kaun Banega Crorepati lucky draw, demands ₹25,000 processing fee via UPI.",
        threatLevel = "CRITICAL",
        reportCount = 842,
        tags = "Lottery, KBC, Advance Fee, Pakistan Prefix",
        isUserReported = false
    ),
    ScamReportEntity(
        type = "PHONE",
        target = "+919876543210",
        title = "Fake Mumbai Cyber Crime / Digital Arrest",
        description = "Caller claims to be DCP Crime Branch Mumbai, stating an illegal narcotics parcel registered in recipient's Aadhaar was seized at Delhi Airport.",
        threatLevel = "CRITICAL",
        reportCount = 1256,
        tags = "Digital Arrest, Narcotics, Aadhaar Spoof, Extortion",
        isUserReported = false
    ),
    ScamReportEntity(
        type = "PHONE",
        target = "18602334455",
        title = "Spoofed Airline Customer Care Google Maps",
        description = "Fraudulent number planted on Google Maps posing as Indigo / Air India flight cancellation refund desk, tricks caller into downloading AnyDesk.",
        threatLevel = "HIGH",
        reportCount = 312,
        tags = "Customer Care, Refund, AnyDesk, Google Maps Spoof",
        isUserReported = false
    ),
    ScamReportEntity(
        type = "UPI",
        target = "electricity.billdesk.urgent@okhdfcbank",
        title = "Fake Electricity Bill Disconnection VPA",
        description = "Sent in SMS warning power will be disconnected at 9:30 PM due to unpaid bill of ₹12. Scammer initiates a debit collect request of ₹50,000.",
        threatLevel = "CRITICAL",
        reportCount = 1489,
        tags = "Electricity, Power Cut, Collect Fraud, UPI",
        isUserReported = false
    ),
    ScamReportEntity(
        type = "UPI",
        target = "refund.support.desk@paytm",
        title = "Fake Zomato / Swiggy Food Refund VPA",
        description = "Impersonates food delivery support asking user to approve a reverse UPI request to receive their order refund.",
        threatLevel = "HIGH",
        reportCount = 590,
        tags = "Refund, Reverse UPI, Food Delivery, Collect Request",
        isUserReported = false
    ),
    ScamReportEntity(
        type = "UPI",
        target = "pmkisan.bonus.claim@upi",
        title = "Fake PM Kisan Beneficiary Claim",
        description = "Phishing UPI address asking farmers to verify bank account with ₹1 deposit which empties bank balance.",
        threatLevel = "CRITICAL",
        reportCount = 741,
        tags = "PM Kisan, Agriculture Subsidy, Village Target",
        isUserReported = false
    ),
    ScamReportEntity(
        type = "URL",
        target = "sbi-online-kyc-panupdate.top",
        title = "Fake SBI NetBanking Phishing Portal",
        description = "Exact clone of State Bank of India login page designed to capture Username, Password, and SMS OTP to siphon funds.",
        threatLevel = "CRITICAL",
        reportCount = 2104,
        tags = "SBI, Phishing, NetBanking, Credential Stealer, .top TLD",
        isUserReported = false
    ),
    ScamReportEntity(
        type = "URL",
        target = "incometax-refund-gov-in.cc",
        title = "Fake Income Tax Department Refund Portal",
        description = "Sends SMS claiming income tax refund of ₹15,490 approved. Asks for debit card number, expiry, and CVV to credit money.",
        threatLevel = "CRITICAL",
        reportCount = 1380,
        tags = "Income Tax, Card Fraud, CVV Harvest, Fake Gov",
        isUserReported = false
    ),
    ScamReportEntity(
        type = "URL",
        target = "indiapost-speedpost-customs.site",
        title = "Fake India Post Delivery Failure Phishing",
        description = "SMS claiming parcel address is incomplete. Asks for ₹5 redelivery fee via credit card, signing user up for recurring international subscription.",
        threatLevel = "HIGH",
        reportCount = 920,
        tags = "India Post, Redelivery Scam, Credit Card Trap",
        isUserReported = false
    ),
    ScamReportEntity(
        type = "APK",
        target = "WhatsApp_Gold_2025.apk",
        title = "WhatsApp Gold Spyware & OTP Interceptor",
        description = "Malicious APK distributed via forward messages promising secret features. In reality, it requests Accessibility Service and steals 2FA SMS OTPs.",
        threatLevel = "CRITICAL",
        reportCount = 3450,
        tags = "Trojan, Spyware, Accessibility Abuse, OTP Sniffer",
        isUserReported = false
    ),
    ScamReportEntity(
        type = "APK",
        target = "PM_Kisan_16th_Kist_Verify.apk",
        title = "Fake PM-Kisan Yojana Malware",
        description = "Trojan targeting rural users that hides icon after install and silently sends all incoming bank notifications to remote command server.",
        threatLevel = "CRITICAL",
        reportCount = 1120,
        tags = "Rural Fraud, Silent SMS, Banking Malware",
        isUserReported = false
    ),
    ScamReportEntity(
        type = "TASK_SCAM",
        target = "@VIP_Crypto_Task_Earning_Official",
        title = "Telegram Like & Earn Part-Time Fraud",
        description = "Recruiter on WhatsApp offers ₹150 for liking 3 YouTube videos. Once trust is earned, user is lured to deposit ₹50,000 for VIP tasks and funds are locked.",
        threatLevel = "CRITICAL",
        reportCount = 4120,
        tags = "Telegram Task, YouTube Like, Advance Fee, Investment Fraud",
        isUserReported = false
    ),
    ScamReportEntity(
        type = "DIGITAL_ARREST",
        target = "CBI_Narcotics_Special_Unit_Skype",
        title = "Fake Police / CBI Digital Arrest Video Ring",
        description = "Scammers set up a fake police station studio with Indian flag and police badges on Skype. Threatens victim with immediate jail unless entire savings are moved to 'RBI Escrow'.",
        threatLevel = "CRITICAL",
        reportCount = 2890,
        tags = "Digital Arrest, Video Extortion, Fake Police, High Value Fraud",
        isUserReported = false
    )
)
