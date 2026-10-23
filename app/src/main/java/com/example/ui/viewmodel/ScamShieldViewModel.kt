package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.cloud.CloudSyncStatus
import com.example.data.cloud.SupabaseThreatService
import com.example.data.gemini.GeminiScamAdvisor
import com.example.data.global.CountryJurisdiction
import com.example.data.global.GlobalJurisdictionRepository
import com.example.data.local.AppDatabase
import com.example.data.local.ScamReportEntity
import com.example.data.simulator.ScamDrillRepository
import com.example.data.verifier.ApkInspectorEngine
import com.example.data.verifier.DigitalArrestDefenderEngine
import com.example.data.verifier.EmailForensicsEngine
import com.example.data.verifier.JobScamEngine
import com.example.data.verifier.PhoneCallerVerifierEngine
import com.example.data.verifier.PonziEngine
import com.example.data.verifier.SmsScannerEngine
import com.example.data.verifier.UpiVerifierEngine
import com.example.data.verifier.UrlDomainVerifierEngine
import com.example.data.verifier.VerificationResult
import com.example.data.verifier.VoiceCloneDefenderEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScamShieldViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.scamReportDao()

    // Navigation state: 0=Radar, 1=12 Tools Lab, 2=Blacklist, 3=SOS & Jurisdictions, 4=Drills, 5=AI Advisor
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedToolIndex = MutableStateFlow(0)
    val selectedToolIndex: StateFlow<Int> = _selectedToolIndex.asStateFlow()

    // Global Country Jurisdiction
    private val _selectedCountryCode = MutableStateFlow("IN")
    val selectedCountryCode: StateFlow<String> = _selectedCountryCode.asStateFlow()

    val currentJurisdiction: StateFlow<CountryJurisdiction> = MutableStateFlow(
        GlobalJurisdictionRepository.getJurisdiction("IN")
    ).apply {
        viewModelScope.launch {
            _selectedCountryCode.collect { code ->
                value = GlobalJurisdictionRepository.getJurisdiction(code)
            }
        }
    }

    // Cloud Backend Sync State
    private val _cloudSyncStatus = MutableStateFlow(
        CloudSyncStatus(
            isOnline = true,
            isSyncing = false,
            lastSyncTimestamp = System.currentTimeMillis(),
            latencyMs = 38,
            totalCloudRecords = 34254,
            syncMessage = "Connected to Global Threat Intelligence Feed"
        )
    )
    val cloudSyncStatus: StateFlow<CloudSyncStatus> = _cloudSyncStatus.asStateFlow()

    // Community Reports & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterCategory = MutableStateFlow("ALL")
    val filterCategory: StateFlow<String> = _filterCategory.asStateFlow()

    val communityReports: StateFlow<List<ScamReportEntity>> = combine(
        dao.getAllReports(),
        _searchQuery,
        _filterCategory
    ) { reports, query, filter ->
        reports.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.target.contains(query, ignoreCase = true) ||
                item.title.contains(query, ignoreCase = true) ||
                item.tags.contains(query, ignoreCase = true)

            val matchesCategory = filter == "ALL" || item.type.equals(filter, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 1. SMS Scanner State
    var smsInputText = MutableStateFlow("Dear Customer, your electricity power will be disconnected tonight at 9:30 PM due to unpaid previous month bill. Call immediate electricity officer at 9876543210 or update KYC at http://bijli-pay.top")
    var smsSenderHeader = MutableStateFlow("BZ-ELECT")
    var smsScanResult = MutableStateFlow<VerificationResult?>(null)

    // 2. UPI Verifier State
    var upiInputText = MutableStateFlow("electricity.billdesk.urgent@okhdfcbank")
    var upiScanResult = MutableStateFlow<VerificationResult?>(null)

    // 3. URL Analyzer State
    var urlInputText = MutableStateFlow("https://sbi-online-kyc-panupdate.top/login")
    var urlScanResult = MutableStateFlow<VerificationResult?>(null)

    // 4. Phone Inspector State
    var phoneInputText = MutableStateFlow("+923019842104")
    var phoneScanResult = MutableStateFlow<VerificationResult?>(null)

    // 5. APK Inspector State
    var apkInputText = MutableStateFlow("WhatsApp_Gold_2025.apk")
    var apkScanResult = MutableStateFlow<VerificationResult?>(null)

    // 6. Digital Arrest Defender State
    var daVideoCall = MutableStateFlow(true)
    var daAadhaarParcel = MutableStateFlow(true)
    var daFundsTransfer = MutableStateFlow(true)
    var daStayOnline = MutableStateFlow(true)
    var daResult = MutableStateFlow<VerificationResult?>(null)

    // 7. Job & Task Scam State
    var jobDailyPromise = MutableStateFlow("3500")
    var jobDepositRequired = MutableStateFlow("2000")
    var jobIsTelegram = MutableStateFlow(true)
    var jobTaskType = MutableStateFlow("YouTube Video Liking & Google Map Rating")
    var jobResult = MutableStateFlow<VerificationResult?>(null)

    // 8. Ponzi Verifier State
    var ponziMonthlyReturn = MutableStateFlow("25")
    var ponziHasReferral = MutableStateFlow(true)
    var ponziIsRegistered = MutableStateFlow(false)
    var ponziResult = MutableStateFlow<VerificationResult?>(null)

    // 11. Email Forensics State
    var emailInputText = MutableStateFlow("From: Chase Security Alert <security@chase-portal.top>\nAuthentication-Results: spf=fail; dkim=fail; dmarc=fail\nSubject: Urgent: Unauthorized wire transfer release required within 2 hours or funds will be frozen.")
    var emailScanResult = MutableStateFlow<VerificationResult?>(null)

    // 12. Voice Clone Defender State
    var vcImmediateBail = MutableStateFlow(true)
    var vcForbidCallingOthers = MutableStateFlow(true)
    var vcDemandsCrypto = MutableStateFlow(true)
    var vcVoiceSlightlyOff = MutableStateFlow(true)
    var vcResult = MutableStateFlow<VerificationResult?>(null)

    // AI Advisor State
    var aiQueryText = MutableStateFlow("A caller claiming to be from Mumbai Crime Branch showed a police badge on Skype and says a parcel with 5 passports and MDMA drugs in my name was stopped by customs. They asked me to transfer ₹1,50,000 to an RBI secret verification account to avoid immediate arrest. Is this real?")
    var aiAdvisorResponse = MutableStateFlow("")
    var aiAdvisorLoading = MutableStateFlow(false)

    // Scam Drills State
    var currentDrillIndex = MutableStateFlow(0)
    var drillSelectedOption = MutableStateFlow<Int?>(null)
    var drillScore = MutableStateFlow(0)
    var drillAnswersCount = MutableStateFlow(0)

    init {
        // Run initial verification on default inputs so users immediately see live results
        runSmsScan()
        runUpiScan()
        runUrlScan()
        runPhoneScan()
        runApkScan()
        runDigitalArrestEvaluation()
        runJobScan()
        runPonziScan()
        runEmailScan()
        runVoiceCloneScan()

        // Background sync with Global Threat Backend
        syncWithCloudBackend()
    }

    fun setCountry(code: String) {
        _selectedCountryCode.value = code
    }

    fun setTab(index: Int) {
        _selectedTab.value = index
    }

    fun setToolIndex(index: Int) {
        _selectedToolIndex.value = index
    }

    fun navigateToTool(index: Int) {
        _selectedToolIndex.value = index
        _selectedTab.value = 1 // Switch to 12 Tools Lab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterCategory(category: String) {
        _filterCategory.value = category
    }

    fun syncWithCloudBackend() {
        viewModelScope.launch {
            _cloudSyncStatus.value = _cloudSyncStatus.value.copy(isSyncing = true)
            val (newThreats, status) = SupabaseThreatService.syncWithGlobalBackend(getApplication())
            if (newThreats.isNotEmpty()) {
                dao.insertReports(newThreats)
            }
            _cloudSyncStatus.value = status
        }
    }

    // 1. SMS Scan
    fun runSmsScan() {
        val result = SmsScannerEngine.analyze(smsInputText.value, smsSenderHeader.value)
        smsScanResult.value = result
    }

    // 2. UPI Scan
    fun runUpiScan() {
        val result = UpiVerifierEngine.analyze(upiInputText.value)
        upiScanResult.value = result
    }

    // 3. URL Scan
    fun runUrlScan() {
        val result = UrlDomainVerifierEngine.analyze(urlInputText.value)
        urlScanResult.value = result
    }

    // 4. Phone Scan
    fun runPhoneScan() {
        val result = PhoneCallerVerifierEngine.analyze(phoneInputText.value)
        phoneScanResult.value = result
    }

    // 5. APK Scan
    fun runApkScan() {
        val result = ApkInspectorEngine.analyze(apkInputText.value)
        apkScanResult.value = result
    }

    // 6. Digital Arrest Scan
    fun runDigitalArrestEvaluation() {
        val result = DigitalArrestDefenderEngine.evaluate(
            claimsArrestOnVideo = daVideoCall.value,
            claimsAadhaarParcel = daAadhaarParcel.value,
            asksForFundsTransfer = daFundsTransfer.value,
            demandsVictimStayOnline = daStayOnline.value
        )
        daResult.value = result
    }

    // 7. Job Task Scan
    fun runJobScan() {
        val daily = jobDailyPromise.value.toDoubleOrNull() ?: 0.0
        val deposit = jobDepositRequired.value.toDoubleOrNull() ?: 0.0
        val result = JobScamEngine.calculate(
            dailyPromise = daily,
            depositRequired = deposit,
            isTelegramGroup = jobIsTelegram.value,
            taskType = jobTaskType.value
        )
        jobResult.value = result
    }

    // 8. Ponzi Scan
    fun runPonziScan() {
        val returnPct = ponziMonthlyReturn.value.toDoubleOrNull() ?: 0.0
        val result = PonziEngine.evaluate(
            guaranteedMonthlyReturnPercent = returnPct,
            hasReferralCommission = ponziHasReferral.value,
            isRegisteredRegulatory = ponziIsRegistered.value
        )
        ponziResult.value = result
    }

    // 11. Email Forensics Scan
    fun runEmailScan() {
        val result = EmailForensicsEngine.analyze(emailInputText.value)
        emailScanResult.value = result
    }

    // 12. Voice Clone Scan
    fun runVoiceCloneScan() {
        val result = VoiceCloneDefenderEngine.evaluate(
            demandsImmediateBail = vcImmediateBail.value,
            forbidsCallingOthers = vcForbidCallingOthers.value,
            demandsCryptoOrGiftCards = vcDemandsCrypto.value,
            voiceSoundsSlightlyOff = vcVoiceSlightlyOff.value
        )
        vcResult.value = result
    }

    // AI Advisor Query
    fun runAiAdvisorQuery() {
        if (aiQueryText.value.isBlank()) return
        viewModelScope.launch {
            aiAdvisorLoading.value = true
            aiAdvisorResponse.value = ""
            val output = GeminiScamAdvisor.analyzeScamScenario(aiQueryText.value)
            aiAdvisorResponse.value = output
            aiAdvisorLoading.value = false
        }
    }

    // Scam Drill Actions
    fun answerDrill(optionIndex: Int) {
        if (drillSelectedOption.value != null) return
        drillSelectedOption.value = optionIndex
        val drill = ScamDrillRepository.drills[currentDrillIndex.value]
        if (drill.options[optionIndex].isCorrect) {
            drillScore.value += 25
        }
        drillAnswersCount.value += 1
    }

    fun nextDrill() {
        if (currentDrillIndex.value < ScamDrillRepository.drills.size - 1) {
            currentDrillIndex.value += 1
            drillSelectedOption.value = null
        } else {
            // Reset to first drill
            currentDrillIndex.value = 0
            drillSelectedOption.value = null
            drillScore.value = 0
            drillAnswersCount.value = 0
        }
    }

    // Community Reporting with Cloud Sync
    fun addCommunityReport(
        type: String,
        target: String,
        title: String,
        description: String,
        threatLevel: String,
        tags: String
    ) {
        viewModelScope.launch {
            val report = ScamReportEntity(
                type = type,
                target = target,
                title = title,
                description = description,
                threatLevel = threatLevel,
                reportCount = 1,
                tags = tags,
                isUserReported = true
            )
            dao.insertReport(report)
            SupabaseThreatService.submitReportToCloud(report)
        }
    }

    fun upvoteReport(id: Long) {
        viewModelScope.launch {
            dao.upvoteReport(id)
        }
    }
}
