package com.example.data.global

data class EmergencyContact(
    val title: String,
    val number: String,
    val description: String,
    val isTollFree: Boolean = true
)

data class PortalLink(
    val title: String,
    val url: String,
    val agency: String
)

data class CountryJurisdiction(
    val countryCode: String, // "IN", "US", "UK", "AU", "CA", "SG", "AE", "GLOBAL"
    val countryName: String,
    val flagEmoji: String,
    val currencySymbol: String,
    val primaryEmergencyNumber: String,
    val primaryEmergencyLabel: String,
    val cyberCrimeAgency: String,
    val goldenHourLimitHours: Int,
    val legalProtectionNotice: String,
    val commonRegionalScams: List<String>,
    val emergencyContacts: List<EmergencyContact>,
    val officialPortals: List<PortalLink>
)

object GlobalJurisdictionRepository {
    private val jurisdictions = mapOf(
        "IN" to CountryJurisdiction(
            countryCode = "IN",
            countryName = "India",
            flagEmoji = "🇮🇳",
            currencySymbol = "₹",
            primaryEmergencyNumber = "1930",
            primaryEmergencyLabel = "National Cyber Crime Helpline (1930)",
            cyberCrimeAgency = "Indian Cyber Crime Coordination Centre (I4C) & MHA",
            goldenHourLimitHours = 2,
            legalProtectionNotice = "Section 41A CrPC: Police or courts NEVER arrest citizens via Skype or WhatsApp video calls. 'Digital Arrest' has zero legal validity in Indian law.",
            commonRegionalScams = listOf(
                "Digital Arrest Extortion (Fake CBI/Mumbai Police)",
                "Electricity Bill Disconnection SMS",
                "Reverse UPI Collect Request Fraud",
                "Fake PM-Kisan & Welfare APKs",
                "Telegram Video-Liking Task Fraud"
            ),
            emergencyContacts = listOf(
                EmergencyContact("National Cyber Crime Helpline", "1930", "Immediate fund freezing across Indian banking network"),
                EmergencyContact("Emergency Police Response", "112", "National all-in-one emergency helpline"),
                EmergencyContact("TRAI Spam & Phishing Reporting", "1909", "Report SMS headers and harassing telemarketers"),
                EmergencyContact("DoT Chakshu Lost/Stolen Mobile", "14422", "CEIR IMEI blocking and handset recovery")
            ),
            officialPortals = listOf(
                PortalLink("National Cyber Crime Portal", "https://cybercrime.gov.in", "Ministry of Home Affairs"),
                PortalLink("Sanchar Saathi (Chakshu)", "https://sancharsaathi.gov.in", "Department of Telecommunications"),
                PortalLink("RBI Sachet Fraud Portal", "https://sachet.rbi.org.in", "Reserve Bank of India")
            )
        ),
        "US" to CountryJurisdiction(
            countryCode = "US",
            countryName = "United States",
            flagEmoji = "🇺🇸",
            currencySymbol = "$",
            primaryEmergencyNumber = "1-877-382-4357",
            primaryEmergencyLabel = "FTC Fraud Helpline (1-877-FTC-HELP)",
            cyberCrimeAgency = "Federal Bureau of Investigation (FBI) IC3 & FTC",
            goldenHourLimitHours = 24,
            legalProtectionNotice = "4th & 5th Amendments: Federal agencies (IRS, FBI, DEA, Social Security) never demand gift cards, Bitcoin, or wire transfers to cancel arrest warrants.",
            commonRegionalScams = listOf(
                "IRS / Social Security Administration Suspension Scams",
                "AI Voice Clone Grandparent Kidnapping Extortion",
                "Crypto Pig Butchering (Sha Zhu Pan)",
                "Zelle / Venmo Bank Impersonation Fraud",
                "USPS Incomplete Address Delivery Phishing"
            ),
            emergencyContacts = listOf(
                EmergencyContact("FTC Fraud Reporting Line", "18773824357", "Federal Trade Commission identity theft & fraud"),
                EmergencyContact("Emergency 911", "911", "Immediate physical distress or active threat"),
                EmergencyContact("FCC Scam Call Center", "18882255322", "Federal Communications Commission robocall report")
            ),
            officialPortals = listOf(
                PortalLink("FBI Internet Crime Complaint Center", "https://www.ic3.gov", "Federal Bureau of Investigation"),
                PortalLink("FTC Report Fraud", "https://reportfraud.ftc.gov", "Federal Trade Commission"),
                PortalLink("IdentityTheft.gov", "https://www.identitytheft.gov", "Federal Trade Commission Recovery")
            )
        ),
        "UK" to CountryJurisdiction(
            countryCode = "UK",
            countryName = "United Kingdom",
            flagEmoji = "🇬🇧",
            currencySymbol = "£",
            primaryEmergencyNumber = "0300 123 2040",
            primaryEmergencyLabel = "Action Fraud Police UK (0300 123 2040)",
            cyberCrimeAgency = "National Fraud Intelligence Bureau & NCSC",
            goldenHourLimitHours = 12,
            legalProtectionNotice = "Contingent Reimbursement Model (CRM): UK banks must reimburse authorized push payment (APP) fraud victims who acted with reasonable care.",
            commonRegionalScams = listOf(
                "HMRC Tax Rebate / Arrest Warrant Calls",
                "Royal Mail Delivery Redirection Fee Scams",
                "Authorized Push Payment (APP) Bank Spoofing",
                "WhatsApp 'Hi Mum / Hi Dad' Emergency Loan Fraud",
                "Crypto Investment VIP Telegram Groups"
            ),
            emergencyContacts = listOf(
                EmergencyContact("Action Fraud Police Line", "03001232040", "UK national reporting centre for fraud & cybercrime"),
                EmergencyContact("Emergency 999", "999", "Active police emergencies"),
                EmergencyContact("Police Non-Emergency", "101", "Report cyber incidents that already occurred"),
                EmergencyContact("NCSC Phishing Forwarder", "7726", "Forward suspicious SMS free of charge to 7726")
            ),
            officialPortals = listOf(
                PortalLink("Action Fraud UK", "https://www.actionfraud.police.uk", "City of London Police"),
                PortalLink("National Cyber Security Centre", "https://www.ncsc.gov.uk", "NCSC GCHQ"),
                PortalLink("FCA Warning List", "https://www.fca.org.uk/scamsmart", "Financial Conduct Authority")
            )
        ),
        "AU" to CountryJurisdiction(
            countryCode = "AU",
            countryName = "Australia",
            flagEmoji = "🇦🇺",
            currencySymbol = "A$",
            primaryEmergencyNumber = "1300 795 995",
            primaryEmergencyLabel = "Scamwatch Hotline (1300 795 995)",
            cyberCrimeAgency = "National Anti-Scam Centre (NASC) & ACCC",
            goldenHourLimitHours = 24,
            legalProtectionNotice = "Australian Cyber Security Centre (ACSC): Police and ATO never request payments via cryptocurrency or retail vouchers.",
            commonRegionalScams = listOf(
                "ATO Tax Debt / Arrest Extortion",
                "myGov & Medicare Login Phishing Links",
                "Australia Post Missed Delivery SMS",
                "Remote Access TeamViewer Bank Takeover",
                "Superannuation Early Release Investment Scams"
            ),
            emergencyContacts = listOf(
                EmergencyContact("Scamwatch Support", "1300795995", "Australian Competition and Consumer Commission"),
                EmergencyContact("Australian Cyber Security Centre", "1300292371", "ReportCyber 24/7 hotline"),
                EmergencyContact("Triple Zero Emergency", "000", "Immediate danger")
            ),
            officialPortals = listOf(
                PortalLink("Scamwatch Portal", "https://www.scamwatch.gov.au", "ACCC National Anti-Scam Centre"),
                PortalLink("ReportCyber ACSC", "https://www.cyber.gov.au/report-and-recover/report", "Australian Cyber Security Centre"),
                PortalLink("IDCARE Identity Support", "https://www.idcare.org", "National Identity & Cyber Support")
            )
        ),
        "CA" to CountryJurisdiction(
            countryCode = "CA",
            countryName = "Canada",
            flagEmoji = "🇨🇦",
            currencySymbol = "C$",
            primaryEmergencyNumber = "1-888-495-8501",
            primaryEmergencyLabel = "Canadian Anti-Fraud Centre (1-888-495-8501)",
            cyberCrimeAgency = "RCMP National Cybercrime Coordination Unit (NC3)",
            goldenHourLimitHours = 24,
            legalProtectionNotice = "CRA / RCMP: Canadian Revenue Agency never sends Interac e-transfers or threatens immediate deportation/arrest over the phone.",
            commonRegionalScams = listOf(
                "CRA Tax Refund / Arrest Warrant Extortion",
                "Interac e-Transfer Phishing SMS",
                "Border Services (CBSA) Seized Parcel Calls",
                "Job Offer Remote Secret Shopper Cheque Fraud",
                "Grandparent Emergency Bail Scam"
            ),
            emergencyContacts = listOf(
                EmergencyContact("Canadian Anti-Fraud Centre", "18884958501", "CAFC / RCMP toll-free fraud intake"),
                EmergencyContact("Emergency 911", "911", "Immediate physical safety risk")
            ),
            officialPortals = listOf(
                PortalLink("Canadian Anti-Fraud Centre", "https://www.antifraudcentre-centreantifraude.ca", "RCMP / CAFC"),
                PortalLink("Get Cyber Safe Canada", "https://www.getcybersafe.gc.ca", "Canadian Centre for Cyber Security")
            )
        ),
        "SG" to CountryJurisdiction(
            countryCode = "SG",
            countryName = "Singapore",
            flagEmoji = "🇸🇬",
            currencySymbol = "S$",
            primaryEmergencyNumber = "1800-255-0000",
            primaryEmergencyLabel = "Anti-Scam Helpline (1800-255-0000)",
            cyberCrimeAgency = "Singapore Police Force Anti-Scam Command (ASCom)",
            goldenHourLimitHours = 6,
            legalProtectionNotice = "Monetary Authority of Singapore (MAS): Official government agencies never send clickable links in SMS.",
            commonRegionalScams = listOf(
                "Government Officials Impersonation (SPF / ICA)",
                "Singpass QR Code Phishing & Login Theft",
                "Job / E-commerce Commission Task Scams",
                "Malicious Android APK Loan/Cleaning Service Malware",
                "Love / Pig Butchering Investment Traps"
            ),
            emergencyContacts = listOf(
                EmergencyContact("Anti-Scam Helpline", "18002550000", "Singapore Police Force 24/7 line"),
                EmergencyContact("Police Emergency 999", "999", "Urgent crimes in progress")
            ),
            officialPortals = listOf(
                PortalLink("Scam Alert Singapore", "https://www.scamalert.sg", "National Crime Prevention Council"),
                PortalLink("Singapore Police E-Services", "https://www.police.gov.sg", "SPF Cybercrime")
            )
        )
    )

    fun getAllCountries(): List<CountryJurisdiction> = jurisdictions.values.toList()

    fun getJurisdiction(code: String): CountryJurisdiction {
        return jurisdictions[code] ?: jurisdictions["IN"]!!
    }
}
