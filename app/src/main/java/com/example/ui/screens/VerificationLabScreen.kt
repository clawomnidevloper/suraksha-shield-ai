package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QuickSampleChips
import com.example.ui.components.ResultCard
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBluePrimary
import com.example.ui.theme.SafeGreen
import com.example.ui.viewmodel.ScamShieldViewModel

@Composable
fun VerificationLabScreen(
    viewModel: ScamShieldViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTool by viewModel.selectedToolIndex.collectAsState()
    val toolTitles = listOf(
        "1. SMS Radar",
        "2. UPI / QR",
        "3. URL Analyzer",
        "4. Phone Check",
        "5. APK Shield",
        "6. Digital Arrest",
        "7. Job Fraud",
        "8. Crypto Ponzi",
        "9. USSD Forward",
        "10. Golden Hour",
        "11. Email Phish",
        "12. Voice Clone"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("verification_lab_screen")
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTool,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            toolTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTool == index,
                    onClick = { viewModel.setToolIndex(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTool == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.testTag("lab_tab_$index")
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTool) {
                0 -> item { Tool1SmsScanner(viewModel) }
                1 -> item { Tool2UpiVerifier(viewModel) }
                2 -> item { Tool3UrlAnalyzer(viewModel) }
                3 -> item { Tool4PhoneInspector(viewModel) }
                4 -> item { Tool5ApkInspector(viewModel) }
                5 -> item { Tool6DigitalArrest(viewModel) }
                6 -> item { Tool7JobScam(viewModel) }
                7 -> item { Tool8PonziVerifier(viewModel) }
                8 -> item { Tool9UssdForwarding() }
                9 -> item { Tool10GoldenHourProtocol() }
                10 -> item { Tool11EmailForensics(viewModel) }
                11 -> item { Tool12VoiceClone(viewModel) }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Tool 1: SMS & Message Phishing Radar
// ------------------------------------------------------------------------------------------------
@Composable
fun Tool1SmsScanner(viewModel: ScamShieldViewModel) {
    val smsText by viewModel.smsInputText.collectAsState()
    val senderHeader by viewModel.smsSenderHeader.collectAsState()
    val result by viewModel.smsScanResult.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    val samples = listOf(
        "Electricity Cut" to "Dear Customer, your electricity power will be disconnected tonight at 9:30 PM due to unpaid previous month bill. Call immediate electricity officer at 9876543210 or update KYC at http://bijli-pay.top",
        "SBI Blocked" to "Dear SBI User, your NetBanking account has been suspended today due to expired PAN card. Immediately update your PAN at https://sbi-online-kyc-panupdate.top/login or your account will be permanently closed.",
        "KBC Lottery" to "Congratulations! Your mobile number won ₹25,00,000 in KBC Jio Lucky Draw 2025. Call our WhatsApp manager at +923019842104 to claim prize money.",
        "FedEx Drugs" to "FedEx Alert: Your shipment #FX-8890 containing illegal narcotics and fake passports was seized by Mumbai Police at Delhi airport. Contact DCP immediately to prevent arrest.",
        "Legit OTP" to "148920 is your secret OTP for transaction at Flipkart of ₹1,499. Valid for 10 mins. Do not share OTP with anyone including bank staff. State Bank of India."
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Tool 1: SMS & Message Phishing Threat Radar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Scans incoming SMS, WhatsApp forwards, and emails for urgency manipulation, fake bank alerts, electricity disconnections, and credential phishing links.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        QuickSampleChips(
            samples = samples,
            onSelect = { sampleText ->
                viewModel.smsInputText.value = sampleText
                viewModel.runSmsScan()
            }
        )

        OutlinedTextField(
            value = senderHeader,
            onValueChange = { viewModel.smsSenderHeader.value = it },
            label = { Text("Sender Header / Number (Optional)") },
            placeholder = { Text("e.g. BZ-ELECT, VK-SBIBNK, +919876543210") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sms_sender_input"),
            singleLine = true
        )

        OutlinedTextField(
            value = smsText,
            onValueChange = { viewModel.smsInputText.value = it },
            label = { Text("Message Body Text") },
            placeholder = { Text("Paste suspicious message here...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .testTag("sms_text_input"),
            trailingIcon = {
                IconButton(onClick = {
                    val clip = clipboardManager.getText()?.text
                    if (!clip.isNullOrBlank()) {
                        viewModel.smsInputText.value = clip
                    }
                }) {
                    Icon(imageVector = Icons.Default.ContentPaste, contentDescription = "Paste Clipboard")
                }
            }
        )

        Button(
            onClick = { viewModel.runSmsScan() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("run_sms_scan_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary)
        ) {
            Icon(imageVector = Icons.Default.Security, contentDescription = "Scan")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Analyze Message Threat Level", fontWeight = FontWeight.Bold)
        }

        result?.let {
            ResultCard(result = it)
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Tool 2: UPI VPA & QR Debit Trap Inspector
// ------------------------------------------------------------------------------------------------
@Composable
fun Tool2UpiVerifier(viewModel: ScamShieldViewModel) {
    val upiInput by viewModel.upiInputText.collectAsState()
    val result by viewModel.upiScanResult.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    val samples = listOf(
        "Fake Electricity VPA" to "electricity.billdesk.urgent@okhdfcbank",
        "Fake Swiggy Refund" to "refund.support.desk@paytm",
        "Fake Reverse QR Code" to "upi://pay?pa=scam.win@icici&pn=Lottery%20Prize%20Cashback&am=25000&cu=INR",
        "Fake PM Kisan" to "pmkisan.bonus.claim@upi",
        "Legit Merchant" to "merchant123@icici"
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Tool 2: UPI VPA & Payment QR Code Inspector",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Prevents the infamous 'UPI Reverse Collect Scam': Scammers send collect requests or deceptive QR codes claiming you will receive money, but entering your UPI PIN actually DEBITS your bank balance!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        QuickSampleChips(
            samples = samples,
            onSelect = { sample ->
                viewModel.upiInputText.value = sample
                viewModel.runUpiScan()
            }
        )

        OutlinedTextField(
            value = upiInput,
            onValueChange = { viewModel.upiInputText.value = it },
            label = { Text("UPI ID or QR Intent String") },
            placeholder = { Text("e.g. user@okhdfcbank or upi://pay?pa=...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("upi_input_field"),
            trailingIcon = {
                IconButton(onClick = {
                    val clip = clipboardManager.getText()?.text
                    if (!clip.isNullOrBlank()) {
                        viewModel.upiInputText.value = clip
                    }
                }) {
                    Icon(imageVector = Icons.Default.ContentPaste, contentDescription = "Paste Clipboard")
                }
            }
        )

        Button(
            onClick = { viewModel.runUpiScan() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("run_upi_scan_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary)
        ) {
            Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Inspect")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Verify UPI Safety & Intent", fontWeight = FontWeight.Bold)
        }

        result?.let {
            ResultCard(result = it)
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Tool 3: Phishing URL & Domain Threat Analyzer
// ------------------------------------------------------------------------------------------------
@Composable
fun Tool3UrlAnalyzer(viewModel: ScamShieldViewModel) {
    val urlInput by viewModel.urlInputText.collectAsState()
    val result by viewModel.urlScanResult.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    val samples = listOf(
        "Fake SBI NetBanking" to "https://sbi-online-kyc-panupdate.top/login",
        "Fake Income Tax" to "https://incometax-refund-gov-in.cc",
        "Fake India Post" to "http://indiapost-speedpost-customs.site",
        "Raw IP Phishing" to "http://192.168.1.105/aadhaar-kyc",
        "Official Bank Site" to "https://www.onlinesbi.sbi"
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Tool 3: Phishing URL & Domain Threat Analyzer",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Detects brand typosquatting, high-risk disposable TLDs (.top, .xyz, .site), raw IP hostnames, unencrypted connections, and credential harvesting paths.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        QuickSampleChips(
            samples = samples,
            onSelect = { sample ->
                viewModel.urlInputText.value = sample
                viewModel.runUrlScan()
            }
        )

        OutlinedTextField(
            value = urlInput,
            onValueChange = { viewModel.urlInputText.value = it },
            label = { Text("URL / Website Link") },
            placeholder = { Text("https://example-scam-domain.xyz") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("url_input_field"),
            trailingIcon = {
                IconButton(onClick = {
                    val clip = clipboardManager.getText()?.text
                    if (!clip.isNullOrBlank()) {
                        viewModel.urlInputText.value = clip
                    }
                }) {
                    Icon(imageVector = Icons.Default.ContentPaste, contentDescription = "Paste Clipboard")
                }
            }
        )

        Button(
            onClick = { viewModel.runUrlScan() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("run_url_scan_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary)
        ) {
            Icon(imageVector = Icons.Default.Language, contentDescription = "Analyze")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Analyze Domain Safety", fontWeight = FontWeight.Bold)
        }

        result?.let {
            ResultCard(result = it)
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Tool 4: Phone Number & Caller Risk Inspector
// ------------------------------------------------------------------------------------------------
@Composable
fun Tool4PhoneInspector(viewModel: ScamShieldViewModel) {
    val phoneInput by viewModel.phoneInputText.collectAsState()
    val result by viewModel.phoneScanResult.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    val samples = listOf(
        "Pakistan (+92) KBC" to "+923019842104",
        "Fake Police Extortion" to "+919876543210",
        "Southeast Asia (+855) Compound" to "+85523888999",
        "Nigeria (+234) Customs" to "+2348031234567",
        "Fake 1860 Airline Maps" to "18602334455"
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Tool 4: Phone Number & Caller Risk Inspector",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Evaluates international caller prefixes, VoIP burner prefixes, and fake toll-free 1800/1860 numbers planted by scammers on Google Maps.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        QuickSampleChips(
            samples = samples,
            onSelect = { sample ->
                viewModel.phoneInputText.value = sample
                viewModel.runPhoneScan()
            }
        )

        OutlinedTextField(
            value = phoneInput,
            onValueChange = { viewModel.phoneInputText.value = it },
            label = { Text("Phone Number with Country Code") },
            placeholder = { Text("+92... or +91...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("phone_input_field"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            trailingIcon = {
                IconButton(onClick = {
                    val clip = clipboardManager.getText()?.text
                    if (!clip.isNullOrBlank()) {
                        viewModel.phoneInputText.value = clip
                    }
                }) {
                    Icon(imageVector = Icons.Default.ContentPaste, contentDescription = "Paste Clipboard")
                }
            }
        )

        Button(
            onClick = { viewModel.runPhoneScan() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("run_phone_scan_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary)
        ) {
            Icon(imageVector = Icons.Default.Call, contentDescription = "Inspect")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Inspect Caller Profile", fontWeight = FontWeight.Bold)
        }

        result?.let {
            ResultCard(result = it)
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Tool 5: Malicious APK & Sideload Trojan Inspector
// ------------------------------------------------------------------------------------------------
@Composable
fun Tool5ApkInspector(viewModel: ScamShieldViewModel) {
    val apkInput by viewModel.apkInputText.collectAsState()
    val result by viewModel.apkScanResult.collectAsState()

    val samples = listOf(
        "WhatsApp Gold Spyware" to "WhatsApp_Gold_2025.apk",
        "Fake PM Kisan Yojana" to "PM_Kisan_16th_Kist_Verify.apk",
        "Screen Sharing AnyDesk" to "AnyDesk_Remote_Bank_Support.apk",
        "Fake SBI Rewards" to "SBI_YONO_Reward_Bonus.apk"
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Tool 5: Malicious APK & Sideload Trojan Inspector",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Identifies malicious Android APKs distributed through WhatsApp and SMS links. Exposes Accessibility Service hijacking, Notification Listener abuse, and remote screen takeovers.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        QuickSampleChips(
            samples = samples,
            onSelect = { sample ->
                viewModel.apkInputText.value = sample
                viewModel.runApkScan()
            }
        )

        OutlinedTextField(
            value = apkInput,
            onValueChange = { viewModel.apkInputText.value = it },
            label = { Text("APK Name or Package String") },
            placeholder = { Text("e.g. WhatsApp_Gold.apk") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("apk_input_field")
        )

        Button(
            onClick = { viewModel.runApkScan() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("run_apk_scan_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary)
        ) {
            Icon(imageVector = Icons.Default.Android, contentDescription = "Inspect APK")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Analyze APK Threat Signature", fontWeight = FontWeight.Bold)
        }

        result?.let {
            ResultCard(result = it)
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Tool 6: Digital Arrest Extortion Defender
// ------------------------------------------------------------------------------------------------
@Composable
fun Tool6DigitalArrest(viewModel: ScamShieldViewModel) {
    val videoCall by viewModel.daVideoCall.collectAsState()
    val aadhaarParcel by viewModel.daAadhaarParcel.collectAsState()
    val fundsTransfer by viewModel.daFundsTransfer.collectAsState()
    val stayOnline by viewModel.daStayOnline.collectAsState()
    val result by viewModel.daResult.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Tool 6: 'Digital Arrest' Extortion Defender",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AlertRed
        )
        Text(
            text = "Tackles the rampant extortion racket where fraudsters pose in fake police uniforms on Skype/WhatsApp video calls claiming your Aadhaar was used in illegal drug/money laundering parcels.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Current Scenario Checklist:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Caller poses on Skype/WhatsApp video call", modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Switch(checked = videoCall, onCheckedChange = { viewModel.daVideoCall.value = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Claims illegal drug/customs parcel with your Aadhaar", modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Switch(checked = aadhaarParcel, onCheckedChange = { viewModel.daAadhaarParcel.value = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Demands funds transfer to 'Govt/RBI Escrow' to clear name", modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Switch(checked = fundsTransfer, onCheckedChange = { viewModel.daFundsTransfer.value = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Demands you stay on camera 24/7 without telling family", modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Switch(checked = stayOnline, onCheckedChange = { viewModel.daStayOnline.value = it })
                }
            }
        }

        Button(
            onClick = { viewModel.runDigitalArrestEvaluation() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("run_digital_arrest_eval_button"),
            colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
        ) {
            Icon(imageVector = Icons.Default.Gavel, contentDescription = "Defense")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Activate Digital Arrest Defense Protocol", fontWeight = FontWeight.Bold)
        }

        result?.let {
            ResultCard(result = it)
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Tool 7: Job & Task-Based Investment Fraud Calculator
// ------------------------------------------------------------------------------------------------
@Composable
fun Tool7JobScam(viewModel: ScamShieldViewModel) {
    val dailyPromise by viewModel.jobDailyPromise.collectAsState()
    val depositRequired by viewModel.jobDepositRequired.collectAsState()
    val isTelegram by viewModel.jobIsTelegram.collectAsState()
    val taskType by viewModel.jobTaskType.collectAsState()
    val result by viewModel.jobResult.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Tool 7: Job & Task Investment Fraud Calculator",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Calculates the deception index for 'Part-time work from home' offers (e.g. liking YouTube videos or rating Google Maps hotels) requiring deposits.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = dailyPromise,
            onValueChange = { viewModel.jobDailyPromise.value = it },
            label = { Text("Promised Daily Earnings (₹)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = depositRequired,
            onValueChange = { viewModel.jobDepositRequired.value = it },
            label = { Text("Initial Deposit Asked to Unlock VIP Task (₹)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Task managed via Telegram Group", fontSize = 13.sp)
            Switch(checked = isTelegram, onCheckedChange = { viewModel.jobIsTelegram.value = it })
        }

        Button(
            onClick = { viewModel.runJobScan() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("run_job_scan_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary)
        ) {
            Icon(imageVector = Icons.Default.Work, contentDescription = "Calculate")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Calculate Task Fraud Probability", fontWeight = FontWeight.Bold)
        }

        result?.let {
            ResultCard(result = it)
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Tool 8: Crypto & Ponzi Scheme Verifier
// ------------------------------------------------------------------------------------------------
@Composable
fun Tool8PonziVerifier(viewModel: ScamShieldViewModel) {
    val returnPct by viewModel.ponziMonthlyReturn.collectAsState()
    val hasReferral by viewModel.ponziHasReferral.collectAsState()
    val isRegistered by viewModel.ponziIsRegistered.collectAsState()
    val result by viewModel.ponziResult.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Tool 8: Crypto & High-Yield Ponzi Scheme Verifier",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Detects unsustainable Ponzi mathematics, multi-level recruitment commission pyramids, and unregistered high-yield crypto schemes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = returnPct,
            onValueChange = { viewModel.ponziMonthlyReturn.value = it },
            label = { Text("Guaranteed Monthly Return (%)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Multi-tier referral commissions offered", fontSize = 13.sp)
            Switch(checked = hasReferral, onCheckedChange = { viewModel.ponziHasReferral.value = it })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Officially registered with SEBI / RBI / SEC", fontSize = 13.sp)
            Switch(checked = isRegistered, onCheckedChange = { viewModel.ponziIsRegistered.value = it })
        }

        Button(
            onClick = { viewModel.runPonziScan() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("run_ponzi_scan_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary)
        ) {
            Icon(imageVector = Icons.Default.MoneyOff, contentDescription = "Verify")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Evaluate Scheme Viability", fontWeight = FontWeight.Bold)
        }

        result?.let {
            ResultCard(result = it)
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Tool 9: Call-Forwarding Secret USSD Inspector (*#21# & ##002#)
// ------------------------------------------------------------------------------------------------
@Composable
fun Tool9UssdForwarding() {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Tool 9: Secret Call-Forwarding USSD Inspector",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "A common cybercrime tactic: Scammers ask victims to dial *401*<scammer_phone># under the pretext of 'courier delivery' or 'SIM 5G upgrade'. This silently forwards all your incoming calls and voice OTPs to the scammer!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Step 1: Check Current Forwarding Status",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dial *#21# to inspect whether voice, SMS, or data forwarding is secretly active on your SIM card.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:*%2321%23")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary)
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = "Dial")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dial *#21# in Phone App", fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.08f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Step 2: Emergency Forwarding Eraser (Kill Switch)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AlertRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dial ##002# to instantly erase ALL unconditional and conditional call forwardings across Jio, Airtel, Vi, and BSNL networks.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:%23%23002%23")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                ) {
                    Icon(imageVector = Icons.Default.LockReset, contentDescription = "Erase")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dial ##002# (Erase All Forwardings)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Tool 10: Emergency Golden Hour Fund Freeze Protocol
// ------------------------------------------------------------------------------------------------
@Composable
fun Tool10GoldenHourProtocol() {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Tool 10: 'Golden Hour' Financial Freeze Protocol",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AlertRed
        )
        Text(
            text = "The first 2 hours after fraudulent fund deduction are critical. When reported in time, the Citizen Financial Cyber Fraud Reporting & Management System (CFCFRMS) halts beneficiary bank accounts before cash withdrawal.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val steps = listOf(
            "1. DIAL 1930 IMMEDIATELY" to "Call the Indian National Cyber Crime Helpline (1930) or report at www.cybercrime.gov.in. Provide your bank name, account number, transaction ID, and beneficiary account/UPI ID.",
            "2. CONTACT YOUR REMITTING BANK" to "Call your bank's 24x7 fraud desk immediately to block your compromised debit card, UPI ID, and NetBanking credentials.",
            "3. PRESERVE CRITICAL EVIDENCE" to "Do NOT delete SMS alerts, WhatsApp chats, call logs, or payment receipts. Take timestamped screenshots.",
            "4. GET COMPLAINT ACKNOWLEDGEMENT" to "Save the acknowledgement number (14-digit token) received via SMS from the 1930 portal for bank and police follow-up."
        )

        steps.forEach { (title, desc) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:1930")
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Call, contentDescription = "Dial 1930")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Dial 1930 Helpline Now", fontWeight = FontWeight.ExtraBold)
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Tool 11: Email Header & BEC Phishing Forensic Analyzer
// ------------------------------------------------------------------------------------------------
@Composable
fun Tool11EmailForensics(viewModel: ScamShieldViewModel) {
    val emailText by viewModel.emailInputText.collectAsState()
    val result by viewModel.emailScanResult.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    val samples = listOf(
        "Spoofed Bank Wire" to "From: Chase Bank Security <alerts@chase-portal.top>\nAuthentication-Results: spf=fail; dkim=fail; dmarc=fail\nSubject: URGENT: Wire transfer of $14,500 pending. Confirm credentials immediately.",
        "Free Gmail PayPal" to "From: PayPal Billing Support <paypal.billing.dept99@gmail.com>\nSubject: Invoice #8820: Bitcoin purchase of $899 charged. Call 1-888-555-0199 to dispute.",
        "Legit Corporate" to "From: Google Cloud <no-reply@accounts.google.com>\nAuthentication-Results: spf=pass; dkim=pass; dmarc=pass\nSubject: Security alert: New sign-in on Android device."
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Tool 11: Email Header & BEC Phishing Analyzer",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Exposes Business Email Compromise (BEC) and email spoofing by verifying SPF, DKIM, and DMARC authentication records and display-name spoofing.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        QuickSampleChips(
            samples = samples,
            onSelect = { sample ->
                viewModel.emailInputText.value = sample
                viewModel.runEmailScan()
            }
        )

        OutlinedTextField(
            value = emailText,
            onValueChange = { viewModel.emailInputText.value = it },
            label = { Text("Raw Email Headers or Email Body") },
            placeholder = { Text("Paste email headers (Authentication-Results, From, SPF...)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            trailingIcon = {
                IconButton(onClick = {
                    val clip = clipboardManager.getText()?.text
                    if (!clip.isNullOrBlank()) {
                        viewModel.emailInputText.value = clip
                    }
                }) {
                    Icon(imageVector = Icons.Default.ContentPaste, contentDescription = "Paste")
                }
            }
        )

        Button(
            onClick = { viewModel.runEmailScan() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary)
        ) {
            Icon(imageVector = Icons.Default.Security, contentDescription = "Analyze")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Analyze Email Authentication & Spoofing", fontWeight = FontWeight.Bold)
        }

        result?.let {
            ResultCard(result = it)
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Tool 12: AI Voice Clone & Kidnapping Extortion Defender
// ------------------------------------------------------------------------------------------------
@Composable
fun Tool12VoiceClone(viewModel: ScamShieldViewModel) {
    val immediateBail by viewModel.vcImmediateBail.collectAsState()
    val forbidCallingOthers by viewModel.vcForbidCallingOthers.collectAsState()
    val demandsCrypto by viewModel.vcDemandsCrypto.collectAsState()
    val voiceSlightlyOff by viewModel.vcVoiceSlightlyOff.collectAsState()
    val result by viewModel.vcResult.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Tool 12: AI Voice Clone & Kidnap Extortion Defender",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AlertRed
        )
        Text(
            text = "Defends against the fastest growing global extortion tactic: Scammers clone a loved one's voice using AI from social media and fabricate an emergency (car accident, police custody) demanding instant crypto/gift cards.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Distress Call Characteristics:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Demands immediate emergency bail / hospital money", modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Switch(checked = immediateBail, onCheckedChange = { viewModel.vcImmediateBail.value = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Orders you NOT to hang up or call other family members", modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Switch(checked = forbidCallingOthers, onCheckedChange = { viewModel.vcForbidCallingOthers.value = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Demands crypto, gift cards, or courier cash pickup", modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Switch(checked = demandsCrypto, onCheckedChange = { viewModel.vcDemandsCrypto.value = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Voice sounds slightly unnatural or brief before stranger speaks", modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Switch(checked = voiceSlightlyOff, onCheckedChange = { viewModel.vcVoiceSlightlyOff.value = it })
                }
            }
        }

        Button(
            onClick = { viewModel.runVoiceCloneScan() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
        ) {
            Icon(imageVector = Icons.Default.Gavel, contentDescription = "Defense")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Activate AI Voice Clone Defense Protocol", fontWeight = FontWeight.Bold)
        }

        result?.let {
            ResultCard(result = it)
        }
    }
}

