package com.example.data.simulator

data class DrillOption(
    val text: String,
    val isCorrect: Boolean,
    val explanation: String
)

data class ScamDrillScenario(
    val id: Int,
    val title: String,
    val category: String,
    val urgencyLevel: String,
    val callerOrSender: String,
    val situationText: String,
    val options: List<DrillOption>
)

object ScamDrillRepository {
    val drills = listOf(
        ScamDrillScenario(
            id = 1,
            title = "The 'Digital Arrest' Video Call",
            category = "Police Extortion",
            urgencyLevel = "HIGH",
            callerOrSender = "Caller claims to be DCP Cyber Crime Mumbai",
            situationText = "You receive a WhatsApp video call from a man in a police uniform in front of an Indian flag. He states your Aadhaar was used to send a courier with MDMA drugs and fake passports to Thailand. He demands you stay on Skype and transfer ₹2,00,000 to an 'RBI Escrow Account' for verification. What is your correct action?",
            options = listOf(
                DrillOption(
                    text = "Transfer the money immediately to avoid being arrested tonight.",
                    isCorrect = false,
                    explanation = "WRONG! Law enforcement never asks citizens to transfer money to prove innocence. Once transferred, the money is lost immediately."
                ),
                DrillOption(
                    text = "DISCONNECT IMMEDIATELY. Real police never arrest via video call; dial 1930.",
                    isCorrect = true,
                    explanation = "CORRECT! 'Digital Arrest' has zero legal validity. Summons must be in writing under Section 41A CrPC. Disconnecting immediately stops the extortion."
                ),
                DrillOption(
                    text = "Send your Aadhaar and passport photos to prove it wasn't you.",
                    isCorrect = false,
                    explanation = "WRONG! Sending identity documents gives scammers more ammunition to impersonate you and open mule accounts."
                )
            )
        ),
        ScamDrillScenario(
            id = 2,
            title = "The OLX Buyer Payment QR Code",
            category = "UPI / Payment Trap",
            urgencyLevel = "MEDIUM",
            callerOrSender = "Buyer posing as an Army Officer",
            situationText = "You are selling furniture online for ₹15,000. The buyer sends you a QR code on WhatsApp and says: 'Scan this QR code and enter your UPI PIN to receive the advance payment of ₹15,000 in your account.' What do you do?",
            options = listOf(
                DrillOption(
                    text = "Scan the QR code and enter your UPI PIN as instructed.",
                    isCorrect = false,
                    explanation = "WRONG! Entering your UPI PIN NEVER receives money; it DEBITS ₹15,000 from your bank account instantly."
                ),
                DrillOption(
                    text = "REFUSE! UPI PIN is only needed to SEND money, never to RECEIVE money.",
                    isCorrect = true,
                    explanation = "CORRECT! This is the #1 UPI reverse collect scam in India. Receiving money requires zero PIN entries."
                ),
                DrillOption(
                    text = "Ask him to send half the money through the QR code first.",
                    isCorrect = false,
                    explanation = "WRONG! Any PIN entry on that QR code will debit your account."
                )
            )
        ),
        ScamDrillScenario(
            id = 3,
            title = "The AI Voice Clone Kidnapping Call",
            category = "AI Voice Cloning",
            urgencyLevel = "CRITICAL",
            callerOrSender = "Voice sounds identical to your child/relative",
            situationText = "A tearful voice sounding identical to your daughter calls crying: 'Dad, I got into a terrible car crash, a man took my phone, they need $3,000 bail right now.' Then a stranger grabs the phone and demands immediate Bitcoin or Apple gift cards. What do you do?",
            options = listOf(
                DrillOption(
                    text = "Rush to the store to buy Apple gift cards to save your child.",
                    isCorrect = false,
                    explanation = "WRONG! Courthouses and hospitals never accept retail gift cards or Bitcoin."
                ),
                DrillOption(
                    text = "HANG UP, call your daughter directly on her phone, and ask the Family Secret Word.",
                    isCorrect = true,
                    explanation = "CORRECT! AI can clone voices in seconds from social media. Calling back directly or asking a private secret word instantly breaks the scam."
                ),
                DrillOption(
                    text = "Negotiate the ransom amount down to $1,000.",
                    isCorrect = false,
                    explanation = "WRONG! Engaging and showing panic encourages the extortionist to escalate demands."
                )
            )
        ),
        ScamDrillScenario(
            id = 4,
            title = "The Electricity Power Cut 9:30 PM SMS",
            category = "Utility Phishing",
            urgencyLevel = "HIGH",
            callerOrSender = "SMS from BZ-ELECT",
            situationText = "An SMS arrives: 'Dear Consumer, your electricity power will be disconnected tonight at 9:30 PM due to pending bill update. Immediately contact power officer Mr. Sharma at 9876543210.' What is your reaction?",
            options = listOf(
                DrillOption(
                    text = "Call the number in the SMS to ask what bill is pending.",
                    isCorrect = false,
                    explanation = "WRONG! Calling the number connects you to a fraudster who tricks you into installing AnyDesk or paying via fake UPI links."
                ),
                DrillOption(
                    text = "IGNORE the SMS number. Check your bill on the official state electricity board app.",
                    isCorrect = true,
                    explanation = "CORRECT! State electricity discoms never provide 10-digit mobile numbers for disconnection notices."
                ),
                DrillOption(
                    text = "Click the link in the message and pay the ₹10 verification fee.",
                    isCorrect = false,
                    explanation = "WRONG! The ₹10 link is a phishing portal that logs your debit card and OTP."
                )
            )
        )
    )
}
