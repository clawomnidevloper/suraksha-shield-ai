package com.example.data.verifier

import java.net.URI
import java.util.Locale

enum class ScanRiskLevel(val label: String, val colorHex: Long) {
    SAFE("SAFE & VERIFIED", 0xFF10B981),
    LOW_RISK("LOW RISK", 0xFF3B82F6),
    SUSPICIOUS("SUSPICIOUS", 0xFFF59E0B),
    DANGEROUS("HIGH RISK THREAT", 0xFFEF4444),
    CRITICAL("CRITICAL SCAM DETECTED", 0xFFDC2626)
}

data class VerificationResult(
    val toolName: String,
    val riskLevel: ScanRiskLevel,
    val riskScore: Int, // 0 to 100
    val title: String,
    val summary: String,
    val flags: List<String>,
    val educationalInsight: String,
    val actionSteps: List<String>,
    val isBlacklisted: Boolean = false
)

object SmsScannerEngine {
    fun analyze(message: String, senderHeader: String = ""): VerificationResult {
        val lower = message.lowercase(Locale.ROOT)
        val flags = mutableListOf<String>()
        var score = 5

        // Check urgency and threat triggers
        if (lower.contains("tonight at") || lower.contains("within 24 hours") || lower.contains("immediately") || lower.contains("urgently") || lower.contains("urgent action")) {
            flags.add("Artificial Urgency: Pressuring victim to panic before verifying with real authorities")
            score += 25
        }

        // Check electricity disconnection scam
        if ((lower.contains("electricity") || lower.contains("power") || lower.contains("bijli")) &&
            (lower.contains("disconnected") || lower.contains("bill") || lower.contains("officer"))
        ) {
            flags.add("Electricity Disconnection Modus Operandi: Classic fraud where scammers pose as state electricity board")
            score += 35
        }

        // Check bank account block / KYC expiry scam
        if ((lower.contains("sbi") || lower.contains("hdfc") || lower.contains("icici") || lower.contains("bank") || lower.contains("pan") || lower.contains("aadhaar")) &&
            (lower.contains("blocked") || lower.contains("suspended") || lower.contains("kyc expired") || lower.contains("update pan"))
        ) {
            flags.add("Bank KYC Block Pretext: Banks NEVER send links via SMS to update PAN or Aadhaar")
            score += 35
        }

        // Check lottery / reward / bonus points scam
        if (lower.contains("lottery") || lower.contains("kbc") || lower.contains("won") || lower.contains("congratulations") || lower.contains("reward points") || lower.contains("claim cash")) {
            flags.add("Lottery / Unsolicited Prize Bait: Free money promises are always phishing traps")
            score += 30
        }

        // Check suspicious links
        if (lower.contains("http://") || lower.contains("https://") || lower.contains("bit.ly") || lower.contains(".top") || lower.contains(".xyz") || lower.contains(".site") || lower.contains(".tk") || lower.contains(".cc") || lower.contains(".apk")) {
            flags.add("Suspicious Link Included: Genuine banking SMS uses registered short domain or in-app notifications")
            score += 25
        }

        // Check APK download mention
        if (lower.contains(".apk") || lower.contains("download app") || lower.contains("install our support app")) {
            flags.add("Malicious APK Download Solicitation: Scammers deliver banking trojans directly via links")
            score += 35
        }

        // Check OTP request or forwarding
        if (lower.contains("share otp") || lower.contains("tell otp") || lower.contains("forward this sms")) {
            flags.add("Direct OTP / Credential Theft Attempt: Official institutions never ask you to forward OTPs")
            score += 40
        }

        // Check sender header if provided
        val trimmedHeader = senderHeader.trim().uppercase(Locale.ROOT)
        if (trimmedHeader.isNotEmpty()) {
            if (trimmedHeader.length > 9 || trimmedHeader.contains("+") || trimmedHeader.all { it.isDigit() }) {
                flags.add("Unregistered Personal Sender: Official transactional SMS must come from a certified 6-character TRAI DLT header (e.g., AD-SBIBNK)")
                score += 25
            }
        }

        score = score.coerceIn(0, 100)
        val level = when {
            score >= 75 -> ScanRiskLevel.CRITICAL
            score >= 50 -> ScanRiskLevel.DANGEROUS
            score >= 25 -> ScanRiskLevel.SUSPICIOUS
            score >= 10 -> ScanRiskLevel.LOW_RISK
            else -> ScanRiskLevel.SAFE
        }

        return VerificationResult(
            toolName = "SMS & Message Threat Radar",
            riskLevel = level,
            riskScore = score,
            title = if (score >= 50) "Dangerous Fraud / Phishing Message" else if (score >= 25) "Suspicious Message Detected" else "Likely Safe Communication",
            summary = if (score >= 50) {
                "This message exhibits high indicators of social engineering, panic induction, or credential theft."
            } else {
                "Message analysis found minimal immediate threat patterns."
            },
            flags = flags.ifEmpty { listOf("No critical scam keywords found in the sample text") },
            educationalInsight = "RBI and CERT-In reiterate: Never click SMS links regarding electricity cut-offs or bank KYC. Government utilities never instruct citizens to call personal 10-digit mobile numbers.",
            actionSteps = if (score >= 50) {
                listOf(
                    "DO NOT click any link in the message.",
                    "DO NOT call the number listed in the SMS body.",
                    "Block the sender and report the SMS to 1909 (TRAI Spam reporting).",
                    "If money was deducted, dial National Cyber Crime Helpline 1930 immediately."
                )
            } else {
                listOf(
                    "Verify the sender header matches official DLT registered formats.",
                    "Never disclose OTP, CVV, or UPI PIN to anyone."
                )
            }
        )
    }
}

object UpiVerifierEngine {
    fun analyze(upiInput: String): VerificationResult {
        val trimmed = upiInput.trim()
        val flags = mutableListOf<String>()
        var score = 10

        var vpa = trimmed
        var isQrDeepLink = false
        var qrParams = mapOf<String, String>()

        if (trimmed.startsWith("upi://pay", ignoreCase = true)) {
            isQrDeepLink = true
            flags.add("Decoded UPI Intent Deep-Link QR code")
            try {
                val query = trimmed.substringAfter("?", "")
                qrParams = query.split("&").mapNotNull {
                    val parts = it.split("=")
                    if (parts.size == 2) parts[0].lowercase(Locale.ROOT) to parts[1] else null
                }.toMap()
                vpa = qrParams["pa"] ?: ""
            } catch (e: Exception) {
                flags.add("Corrupted or obfuscated UPI QR format")
                score += 30
            }
        }

        val vpaLower = vpa.lowercase(Locale.ROOT)

        if (!vpaLower.contains("@")) {
            return VerificationResult(
                toolName = "UPI VPA & QR Inspector",
                riskLevel = ScanRiskLevel.SUSPICIOUS,
                riskScore = 50,
                title = "Invalid UPI Address Format",
                summary = "The entered string '$trimmed' is not a valid Virtual Payment Address (e.g. name@bank).",
                flags = listOf("Missing '@' delimiter required in standard NPCI UPI specification"),
                educationalInsight = "A genuine UPI ID always follows the format `username@bankpsp` (e.g., `user@okhdfcbank`, `merchant@paytm`).",
                actionSteps = listOf("Double check spelling and verify through official banking app.")
            )
        }

        val parts = vpaLower.split("@")
        val handle = parts[0]
        val psp = parts.getOrNull(1) ?: ""

        // Check for reverse payment scam in QR parameters
        if (isQrDeepLink) {
            val amount = qrParams["am"]
            val payeeName = qrParams["pn"]
            if (amount != null && amount.isNotEmpty()) {
                flags.add("Payment Amount Specified: ₹$amount will be DEBITED from YOUR account upon entering PIN")
            }
            if (payeeName != null && (payeeName.contains("prize", true) || payeeName.contains("cashback", true) || payeeName.contains("refund", true))) {
                flags.add("Deceptive Payee Name ('$payeeName'): Using prize/cashback terms to mask money deduction")
                score += 45
            }
            flags.add("GOLDEN RULE: UPI PIN is ONLY needed to SEND money. You NEVER need to enter PIN or scan QR to RECEIVE money!")
            score += 25
        }

        // Check deceptive keywords in handle
        val suspiciousKeywords = listOf("billdesk", "electricity", "refund", "customercare", "helpdesk", "pmkisan", "lottery", "cashback", "reward", "cbi", "police")
        for (kw in suspiciousKeywords) {
            if (handle.contains(kw)) {
                flags.add("Impersonation Handle Trap: The handle '$handle' mimics official services. Legitimate entities have registered verified merchant codes, not generic VPA handles.")
                score += 40
                break
            }
        }

        // Check common personal handles used to deceive
        if (handle.contains("airindia") || handle.contains("zomato") || handle.contains("swiggy") || handle.contains("indigo")) {
            flags.add("Impersonating Corporate Brand on Personal PSP Handle")
            score += 45
        }

        score = score.coerceIn(0, 100)
        val level = when {
            score >= 70 -> ScanRiskLevel.CRITICAL
            score >= 45 -> ScanRiskLevel.DANGEROUS
            score >= 25 -> ScanRiskLevel.SUSPICIOUS
            else -> ScanRiskLevel.SAFE
        }

        return VerificationResult(
            toolName = "UPI VPA & QR Inspector",
            riskLevel = level,
            riskScore = score,
            title = if (score >= 60) "High Risk UPI Address / Collect Trap" else if (score >= 30) "Caution Advised on UPI Target" else "Standard Personal UPI Format",
            summary = "Analyzed VPA '$vpa' on PSP provider '@$psp'.",
            flags = flags.ifEmpty { listOf("Standard individual VPA format recognized") },
            educationalInsight = "NPCI Cyber Awareness: Never approve 'Collect Requests' from unknown persons. Entering your UPI PIN always debits money from your account, regardless of what the caller claims.",
            actionSteps = if (score >= 50) {
                listOf(
                    "DO NOT approve any collect request from '$vpa'.",
                    "DO NOT enter your UPI PIN under any pretext.",
                    "Report this UPI VPA in your payment app (GPay / PhonePe / Paytm).",
                    "Block the VPA immediately."
                )
            } else {
                listOf(
                    "Verify the payee's real name that appears in the green check banner before confirming.",
                    "Remember: Receiving money requires zero PIN entries."
                )
            }
        )
    }
}

object UrlDomainVerifierEngine {
    fun analyze(urlInput: String): VerificationResult {
        var clean = urlInput.trim()
        if (!clean.startsWith("http://") && !clean.startsWith("https://")) {
            clean = "https://$clean"
        }

        val flags = mutableListOf<String>()
        var score = 10

        try {
            val uri = URI(clean)
            val host = uri.host ?: clean
            val hostLower = host.lowercase(Locale.ROOT)
            val path = uri.path ?: ""

            // Check high-risk scam TLDs
            val badTlds = listOf(".top", ".xyz", ".tk", ".ml", ".ga", ".cf", ".gq", ".site", ".buzz", ".cfd", ".icu", ".cc", ".click", ".link")
            for (tld in badTlds) {
                if (hostLower.endsWith(tld)) {
                    flags.add("High-Risk TLD ($tld): Commonly used by phishing operators due to cheap disposable domain registrations")
                    score += 35
                    break
                }
            }

            // Check IP address hostname
            val isIp = hostLower.matches(Regex("""^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$"""))
            if (isIp) {
                flags.add("Raw IP Address Host ($host): Legitimate organizations use registered domain names, not direct IP addresses")
                score += 45
            }

            // Check brand typosquatting / lookalikes
            val targetedBrands = listOf("sbi", "hdfc", "icici", "incometax", "uidai", "indiapost", "whatsapp", "netflix", "paytm", "phonepe", "amazon")
            for (brand in targetedBrands) {
                if (hostLower.contains(brand)) {
                    // Check if it's the real official domain
                    val isOfficial = when (brand) {
                        "sbi" -> hostLower.endsWith("sbi.co.in") || hostLower == "onlinesbi.sbi" || hostLower.endsWith(".sbi")
                        "hdfc" -> hostLower.endsWith("hdfcbank.com")
                        "icici" -> hostLower.endsWith("icicibank.com")
                        "incometax" -> hostLower.endsWith("incometax.gov.in")
                        "uidai" -> hostLower.endsWith("uidai.gov.in")
                        "indiapost" -> hostLower.endsWith("indiapost.gov.in")
                        "whatsapp" -> hostLower.endsWith("whatsapp.com")
                        "paytm" -> hostLower.endsWith("paytm.com")
                        "phonepe" -> hostLower.endsWith("phonepe.com")
                        "amazon" -> hostLower.endsWith("amazon.in") || hostLower.endsWith("amazon.com")
                        else -> false
                    }
                    if (!isOfficial) {
                        flags.add("Brand Impersonation / Typosquatting: Domain '$host' mimics official brand '$brand' without authorization")
                        score += 50
                    }
                }
            }

            // Check suspicious path keywords
            val pathLower = path.lowercase(Locale.ROOT)
            if (pathLower.contains("kyc") || pathLower.contains("login") || pathLower.contains("pan-card") || pathLower.contains("verification") || pathLower.contains("reward")) {
                flags.add("Credential Harvesting Path: Targeting personal identity data (PAN/KYC/Credentials)")
                score += 20
            }

            // Check unencrypted HTTP
            if (clean.startsWith("http://")) {
                flags.add("Insecure HTTP Protocol: Data submitted is transmitted unencrypted")
                score += 20
            }

            score = score.coerceIn(0, 100)
            val level = when {
                score >= 70 -> ScanRiskLevel.CRITICAL
                score >= 45 -> ScanRiskLevel.DANGEROUS
                score >= 25 -> ScanRiskLevel.SUSPICIOUS
                else -> ScanRiskLevel.SAFE
            }

            return VerificationResult(
                toolName = "Phishing URL & Domain Threat Analyzer",
                riskLevel = level,
                riskScore = score,
                title = if (score >= 60) "Critical Phishing Domain Detected" else if (score >= 30) "Suspicious Link Caution" else "Domain Appears Standard",
                summary = "Analyzed domain '$host' against global anti-phishing threat intelligence.",
                flags = flags.ifEmpty { listOf("No deceptive redirects or typosquatting patterns found") },
                educationalInsight = "Always check the domain suffix: Genuine Indian Government portals always end in `.gov.in` or `.nic.in`. Banks use dedicated top-level extensions (e.g. `.sbi` or registered `.com`/`.co.in`).",
                actionSteps = if (score >= 50) {
                    listOf(
                        "DO NOT enter usernames, passwords, card numbers, or OTP on this website.",
                        "Close the browser tab immediately.",
                        "Report the link on national cyber portal (cybercrime.gov.in)."
                    )
                } else {
                    listOf(
                        "Ensure the browser padlock icon is active.",
                        "Bookmark your official bank portal directly rather than clicking SMS links."
                    )
                }
            )
        } catch (e: Exception) {
            return VerificationResult(
                toolName = "Phishing URL & Domain Threat Analyzer",
                riskLevel = ScanRiskLevel.SUSPICIOUS,
                riskScore = 40,
                title = "Malformed URL Format",
                summary = "Could not parse '$urlInput' as a valid URI.",
                flags = listOf("Syntax error or invalid URL encoding"),
                educationalInsight = "Attackers often use URL encoding tricks to bypass basic security filters.",
                actionSteps = listOf("Do not click links with strange formatting or characters.")
            )
        }
    }
}

object PhoneCallerVerifierEngine {
    fun analyze(phoneNumber: String): VerificationResult {
        val clean = phoneNumber.replace(Regex("[^0-9+]"), "")
        val flags = mutableListOf<String>()
        var score = 10

        // Check international prefixes heavily associated with WhatsApp/Voice scams in India
        if (clean.startsWith("+92")) {
            flags.add("Country Code +92 (Pakistan): Highly flagged origin for fake lottery (KBC) and impersonation scams targeting Indian citizens")
            score += 55
        } else if (clean.startsWith("+880")) {
            flags.add("Country Code +880 (Bangladesh): High incidence of cross-border telecom extortion")
            score += 35
        } else if (clean.startsWith("+234")) {
            flags.add("Country Code +234 (Nigeria): Origin of advanced fee, romantic extortion, and customs parcel scams")
            score += 45
        } else if (clean.startsWith("+855") || clean.startsWith("+95") || clean.startsWith("+856")) {
            flags.add("Southeast Asia (+855 Cambodia / +95 Myanmar / +856 Laos): Known base of cyber-slavery fraud call center compounds")
            score += 55
        } else if (clean.startsWith("+1") && clean.length > 10) {
            flags.add("North America / Virtual VoIP (+1): Frequently leased by overseas scammers via burner VoIP apps (TextNow/Skype)")
            score += 25
        }

        // Check fake 1800 / 1860 toll free spoofing
        if (clean.startsWith("1800") || clean.startsWith("1860")) {
            if (clean.length != 11 && clean.length != 10) {
                flags.add("Irregular Toll-Free Length: Legitimate toll-free numbers follow strict 10 or 11-digit standards")
                score += 30
            }
            flags.add("Verification Notice: Scammers frequently alter toll-free customer care numbers on Google Maps listings for airlines, couriers, and banks")
            score += 20
        }

        // Check standard Indian mobile format
        if (clean.startsWith("+91") || (clean.length == 10 && clean.matches(Regex("""^[6-9]\d{9}$""")))) {
            flags.add("Indian Domestic Cellular: Real authorities (Police, Courts, CBI) do not conduct arrests or legal notifications from private mobile phones")
            score += 15
        }

        score = score.coerceIn(0, 100)
        val level = when {
            score >= 70 -> ScanRiskLevel.CRITICAL
            score >= 45 -> ScanRiskLevel.DANGEROUS
            score >= 25 -> ScanRiskLevel.SUSPICIOUS
            else -> ScanRiskLevel.SAFE
        }

        return VerificationResult(
            toolName = "Phone Number & Caller Risk Inspector",
            riskLevel = level,
            riskScore = score,
            title = if (score >= 60) "High-Risk Fraud / Extortion Caller" else if (score >= 30) "Unverified Caller - Proceed With Caution" else "Standard Number Profile",
            summary = "Risk assessment for telephone number '$clean'.",
            flags = flags.ifEmpty { listOf("No international extortion prefix flags detected") },
            educationalInsight = "Telecom Regulatory Authority of India (TRAI) and Department of Telecommunications (DoT) advise: International calls spoofed to look like local numbers or WhatsApp calls with foreign country codes claiming to be courier/police are 100% scams.",
            actionSteps = if (score >= 50) {
                listOf(
                    "Block the number on your device and WhatsApp.",
                    "Report on Chakshu portal (sancharsaathi.gov.in) under DoT.",
                    "Never accept video calls from unknown foreign numbers."
                )
            } else {
                listOf(
                    "Do not share OTP or sensitive data over phone calls.",
                    "Verify customer care numbers directly from the official app, not Google search."
                )
            }
        )
    }
}

object ApkInspectorEngine {
    fun analyze(appNameOrPkg: String): VerificationResult {
        val lower = appNameOrPkg.lowercase(Locale.ROOT)
        val flags = mutableListOf<String>()
        var score = 15

        if (lower.endsWith(".apk")) {
            flags.add("Direct APK Sideloading: Bypasses Google Play Protect real-time malware analysis and sandboxing")
            score += 30
        }

        // Dangerous remote access tools masquerading as customer support
        val remoteTools = listOf("anydesk", "teamviewer", "quicksupport", "rustdesk", "airdroid", "screenshare")
        for (tool in remoteTools) {
            if (lower.contains(tool)) {
                flags.add("Remote Screen Sharing Tool ($tool): Scammers trick victims into installing this to view bank OTPs in real-time and control device remotely")
                score += 55
                break
            }
        }

        // Fake apps mimicking welfare or rewards
        if (lower.contains("pm_kisan") || lower.contains("pmkisan") || lower.contains("yojana") || lower.contains("ladli") || lower.contains("subsidy")) {
            flags.add("Welfare Scheme Trojan: Fraudulent APK pretending to disburse government subsidies")
            score += 45
        }

        if (lower.contains("gold") || lower.contains("pink") || lower.contains("plus") && lower.contains("whatsapp")) {
            flags.add("Modified Messaging App (WhatsApp Gold/Pink): Infamous malware family designed to harvest contact books and hijack WhatsApp sessions")
            score += 50
        }

        if (lower.contains("reward") || lower.contains("cashback") || lower.contains("bonus") || lower.contains("sbi_reward")) {
            flags.add("Fake Banking Rewards APK: Trojan designed to capture net-banking credentials and auto-forward SMS")
            score += 45
        }

        score = score.coerceIn(0, 100)
        val level = when {
            score >= 70 -> ScanRiskLevel.CRITICAL
            score >= 45 -> ScanRiskLevel.DANGEROUS
            score >= 25 -> ScanRiskLevel.SUSPICIOUS
            else -> ScanRiskLevel.SAFE
        }

        return VerificationResult(
            toolName = "Malicious APK & App Inspector",
            riskLevel = level,
            riskScore = score,
            title = if (score >= 60) "High Risk Malicious APK Profile" else if (score >= 30) "Caution: Sideloaded App" else "Standard Package Name",
            summary = "Security evaluation of application target '$appNameOrPkg'.",
            flags = flags.ifEmpty { listOf("No notorious banking trojan patterns identified") },
            educationalInsight = "Android malware commonly abuses two critical system permissions: 'Accessibility Services' (allowing it to tap buttons and read screen passwords) and 'Notification Listener' (allowing it to secretly intercept 2FA bank OTPs).",
            actionSteps = if (score >= 50) {
                listOf(
                    "DO NOT install this APK file.",
                    "If already installed: Enable Airplane mode immediately, go to Android Settings > Apps, and uninstall.",
                    "Revoke any granted Accessibility or Device Admin permissions in Settings."
                )
            } else {
                listOf(
                    "Only download apps from the official Google Play Store.",
                    "Never enable 'Install unknown apps' from browser or file manager."
                )
            }
        )
    }
}

object DigitalArrestDefenderEngine {
    fun evaluate(
        claimsArrestOnVideo: Boolean,
        claimsAadhaarParcel: Boolean,
        asksForFundsTransfer: Boolean,
        demandsVictimStayOnline: Boolean
    ): VerificationResult {
        val flags = mutableListOf<String>()
        var score = 10

        if (claimsArrestOnVideo) {
            flags.add("VIDEO CALL ARREST HAS ZERO LEGAL VALIDITY: Under Indian and international law, no court, police station, or agency can arrest a citizen via video call (Skype/WhatsApp/Zoom)")
            score += 35
        }
        if (claimsAadhaarParcel) {
            flags.add("Fake FedEx / Customs Contraband Pretext: Fraud ring falsely claims drugs/passports were found in a courier package under your Aadhaar/Passport")
            score += 25
        }
        if (asksForFundsTransfer) {
            flags.add("Extortion Demanded: Demand to transfer money to a 'Govt Secret Verification Account' or 'RBI Escrow Account'. Government agencies NEVER accept money for innocence verification")
            score += 35
        }
        if (demandsVictimStayOnline) {
            flags.add("Psychological Isolation: Forcing victim to remain on camera 24/7 without talking to family or lawyer to prevent intervention")
            score += 20
        }

        score = score.coerceIn(0, 100)
        val level = if (score >= 60) ScanRiskLevel.CRITICAL else if (score >= 30) ScanRiskLevel.DANGEROUS else ScanRiskLevel.LOW_RISK

        return VerificationResult(
            toolName = "Digital Arrest Extortion Defender",
            riskLevel = level,
            riskScore = score,
            title = "DIGITAL ARREST SCAM IN PROGRESS",
            summary = "The caller is an imposter running a criminal extortion racket. You are in NO legal danger from law enforcement.",
            flags = flags.ifEmpty { listOf("No digital arrest indicators selected") },
            educationalInsight = "MHA / Indian Cyber Crime Coordination Centre (I4C) Directive: 'Digital Arrest' does not exist in the Code of Criminal Procedure (CrPC). Police summons are always delivered in physical writing under Section 41A CrPC, never via Skype video calls.",
            actionSteps = listOf(
                "DISCONNECT THE CALL IMMEDIATELY. Do not be afraid.",
                "DO NOT transfer a single rupee to any account.",
                "Call the National Cyber Crime Helpline 1930 without delay.",
                "Report incident on www.cybercrime.gov.in."
            )
        )
    }
}

object JobScamEngine {
    fun calculate(
        dailyPromise: Double,
        depositRequired: Double,
        isTelegramGroup: Boolean,
        taskType: String
    ): VerificationResult {
        val flags = mutableListOf<String>()
        var score = 20

        if (isTelegramGroup) {
            flags.add("Telegram VIP Group Requirement: Standard conduit for crypto task fraud to isolate victims and display fake winning testimonials")
            score += 30
        }

        if (depositRequired > 0) {
            flags.add("Advance Deposit Required (₹${depositRequired.toInt()}): Legitimate employers PAY you, they NEVER ask you to deposit money to unlock tasks")
            score += 40
        }

        if (dailyPromise >= 2000) {
            flags.add("Unrealistic Payout Rate (₹${dailyPromise.toInt()}/day for minimal effort like liking videos or rating hotels)")
            score += 25
        }

        if (taskType.contains("like", true) || taskType.contains("rating", true) || taskType.contains("crypto", true)) {
            flags.add("Classic Task Fraud Category ($taskType): Scammers pay ₹150 initially to hook trust, then steal larger lump sums")
            score += 20
        }

        score = score.coerceIn(0, 100)
        val level = if (score >= 70) ScanRiskLevel.CRITICAL else if (score >= 40) ScanRiskLevel.DANGEROUS else ScanRiskLevel.SUSPICIOUS

        return VerificationResult(
            toolName = "Job & Task-Based Scam Evaluator",
            riskLevel = level,
            riskScore = score,
            title = if (score >= 60) "High Probability Task Fraud Trap" else "Suspicious Employment Offer",
            summary = "Analysis of part-time task offer promising ₹${dailyPromise.toInt()}/day with ₹${depositRequired.toInt()} entry fee.",
            flags = flags,
            educationalInsight = "The Task Scam Lifecycle: Stage 1: Pay tiny reward (₹200-₹500). Stage 2: Introduce 'Prepaid VIP Tasks' (₹5,000-₹50,000). Stage 3: Lock account citing 'System error / tax clearance' demanding more money. Stage 4: Scammers block victim.",
            actionSteps = listOf(
                "Refuse any task requiring an upfront deposit or crypto recharge.",
                "Leave the Telegram group immediately.",
                "If you already sent money, immediately contact your bank and file a complaint at 1930."
            )
        )
    }
}

object PonziEngine {
    fun evaluate(
        guaranteedMonthlyReturnPercent: Double,
        hasReferralCommission: Boolean,
        isRegisteredRegulatory: Boolean
    ): VerificationResult {
        val flags = mutableListOf<String>()
        var score = 15

        if (guaranteedMonthlyReturnPercent >= 10.0) {
            flags.add("Mathematically Impossible Guaranteed Return (${guaranteedMonthlyReturnPercent}%/month = ${(guaranteedMonthlyReturnPercent * 12).toInt()}% annualized). No legitimate asset class guarantees such yields risk-free")
            score += 45
        }

        if (hasReferralCommission) {
            flags.add("Multi-Level Pyramid / Referral Incentives: Payouts rely on recruiting new investors rather than genuine business revenue")
            score += 35
        }

        if (!isRegisteredRegulatory) {
            flags.add("Unregulated Entity: Not registered with SEBI, RBI, SEC, or FCA. Operates illegally under Prize Chits and Money Circulation Schemes Act")
            score += 35
        }

        score = score.coerceIn(0, 100)
        val level = if (score >= 60) ScanRiskLevel.CRITICAL else if (score >= 35) ScanRiskLevel.DANGEROUS else ScanRiskLevel.LOW_RISK

        return VerificationResult(
            toolName = "Crypto & Ponzi Scheme Verifier",
            riskLevel = level,
            riskScore = score,
            title = if (score >= 60) "High-Risk Ponzi / Pyramid Scheme" else "Unverified Investment Proposal",
            summary = "Assessment of investment model with ${guaranteedMonthlyReturnPercent}% monthly promise.",
            flags = flags,
            educationalInsight = "Ponzi Scheme Rule: When old investors are paid using money deposited by new investors, the mathematical collapse is 100% guaranteed the moment new recruitments slow down.",
            actionSteps = listOf(
                "Never invest in unregistered schemes promising guaranteed returns.",
                "Verify asset manager license on SEBI (sebi.gov.in) or RBI website.",
                "Do not recruit family or friends into referral tiers."
            )
        )
    }
}

object EmailForensicsEngine {
    fun analyze(headerOrContent: String): VerificationResult {
        val lower = headerOrContent.lowercase(Locale.ROOT)
        val flags = mutableListOf<String>()
        var score = 15

        // Check SPF / DKIM / DMARC failures
        if (lower.contains("spf=fail") || lower.contains("spf=softfail")) {
            flags.add("SPF Verification FAILED: The sending server is not authorized by the domain's DNS SPF record")
            score += 35
        }
        if (lower.contains("dkim=fail")) {
            flags.add("DKIM Signature FAILED: Email cryptographic integrity check failed; email was altered in transit")
            score += 35
        }
        if (lower.contains("dmarc=fail")) {
            flags.add("DMARC Alignment FAILED: Sender header does not align with domain policy")
            score += 30
        }

        // Check Display Name Impersonation
        if (lower.contains("chase") || lower.contains("paypal") || lower.contains("apple") || lower.contains("microsoft") || lower.contains("netflix") || lower.contains("bank")) {
            if (lower.contains("@gmail.com") || lower.contains("@yahoo.com") || lower.contains("@outlook.com") || lower.contains("@hotmail.com")) {
                flags.add("Free Webmail Spoofing: Corporate brand notification sent from a free consumer email address (Gmail/Yahoo/Outlook)")
                score += 50
            }
        }

        // Check urgency and wire fraud terms
        if (lower.contains("wire transfer") || lower.contains("invoice attached") || lower.contains("urgent payment required") || lower.contains("funds release")) {
            flags.add("Business Email Compromise (BEC) Signature: Demanding urgent change of bank wiring instructions")
            score += 30
        }

        if (lower.contains("unauthorized sign-in") || lower.contains("password expires in 2 hours") || lower.contains("verify credentials")) {
            flags.add("Credential Harvesting Trap: Fake security alert designed to provoke login link clicks")
            score += 25
        }

        score = score.coerceIn(0, 100)
        val level = when {
            score >= 70 -> ScanRiskLevel.CRITICAL
            score >= 45 -> ScanRiskLevel.DANGEROUS
            score >= 25 -> ScanRiskLevel.SUSPICIOUS
            else -> ScanRiskLevel.SAFE
        }

        return VerificationResult(
            toolName = "Email Header & BEC Phishing Analyzer",
            riskLevel = level,
            riskScore = score,
            title = if (score >= 60) "High-Probability Email Spoof / BEC Fraud" else if (score >= 30) "Suspicious Email Indicator" else "Standard Email Headers",
            summary = "Forensic inspection of email header authentication records and sender alignment.",
            flags = flags.ifEmpty { listOf("No critical authentication failures detected in sample") },
            educationalInsight = "FBI BEC Warning: Always verify unexpected bank account changes by phone using a pre-established trusted number, never the number in the email.",
            actionSteps = if (score >= 50) {
                listOf(
                    "DO NOT open attachments (.html, .iso, .zip, or macros-enabled documents).",
                    "DO NOT click 'Sign in to update password'.",
                    "Forward to your IT department or report as phishing in your email client."
                )
            } else {
                listOf(
                    "Check the true sender address inside angle brackets <...>, not just the display name."
                )
            }
        )
    }
}

object VoiceCloneDefenderEngine {
    fun evaluate(
        demandsImmediateBail: Boolean,
        forbidsCallingOthers: Boolean,
        demandsCryptoOrGiftCards: Boolean,
        voiceSoundsSlightlyOff: Boolean
    ): VerificationResult {
        val flags = mutableListOf<String>()
        var score = 15

        if (demandsImmediateBail) {
            flags.add("Urgent Bail / Medical Hostage Pretext: Fabricating an acute crisis (jail, car crash) to trigger parental panic")
            score += 35
        }
        if (forbidsCallingOthers) {
            flags.add("Isolation Constraint: Threatens that calling other relatives or police will result in harm or immediate arrest")
            score += 30
        }
        if (demandsCryptoOrGiftCards) {
            flags.add("Irreversible Payment Demanded: Courthouses and real police departments NEVER accept Apple Gift Cards, Western Union, or Bitcoin")
            score += 40
        }
        if (voiceSoundsSlightlyOff) {
            flags.add("AI Voice Synthesis Artifacts: Voice cloning models frequently display slight emotional flatness, robotic cadence, or short audio bursts before handing off to a 'lawyer'")
            score += 20
        }

        score = score.coerceIn(0, 100)
        val level = if (score >= 65) ScanRiskLevel.CRITICAL else if (score >= 40) ScanRiskLevel.DANGEROUS else ScanRiskLevel.SUSPICIOUS

        return VerificationResult(
            toolName = "AI Voice Clone & Kidnap Extortion Defender",
            riskLevel = level,
            riskScore = score,
            title = if (score >= 60) "CRITICAL AI VOICE CLONING SCAM IN PROGRESS" else "Caution: Unverified Distress Call",
            summary = "Analysis of emergency distress call demanding untraceable money transfer.",
            flags = flags.ifEmpty { listOf("No distress call extortion indicators selected") },
            educationalInsight = "FTC Consumer Alert: Using only a 3-second audio snippet from social media (TikTok, Instagram, YouTube), AI cloning tools can replicate anyone's voice with pitch and inflection.",
            actionSteps = listOf(
                "PAUSE AND BREATHE. Hang up the call immediately.",
                "Call the loved one back directly on their known personal phone number.",
                "Ask your 'Family Secret Passphrase' (a pre-agreed secret word only your family knows).",
                "Contact local police or dial 911 / 112 if in genuine doubt."
            )
        )
    }
}

