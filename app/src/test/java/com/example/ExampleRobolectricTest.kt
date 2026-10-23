package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.verifier.ScanRiskLevel
import com.example.data.verifier.SmsScannerEngine
import com.example.data.verifier.UpiVerifierEngine
import com.example.data.verifier.UrlDomainVerifierEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Suraksha Shield", appName)
  }

  @Test
  fun `verify electricity scam detection`() {
    val scamMsg = "Dear Customer, your electricity power will be disconnected tonight at 9:30 PM due to unpaid previous month bill. Call immediate electricity officer at 9876543210 or update KYC at http://bijli-pay.top"
    val result = SmsScannerEngine.analyze(scamMsg)
    assertTrue("Risk score should be high", result.riskScore >= 70)
    assertEquals(ScanRiskLevel.CRITICAL, result.riskLevel)
  }

  @Test
  fun `verify upi reverse collect scam detection`() {
    val fakeQr = "upi://pay?pa=scam.win@icici&pn=Lottery%20Prize%20Cashback&am=25000&cu=INR"
    val result = UpiVerifierEngine.analyze(fakeQr)
    assertTrue(result.riskScore >= 60)
  }

  @Test
  fun `verify phishing url detection`() {
    val badUrl = "https://sbi-online-kyc-panupdate.top/login"
    val result = UrlDomainVerifierEngine.analyze(badUrl)
    assertTrue(result.riskScore >= 70)
  }

  @Test
  fun `verify email spoofing detection`() {
    val email = "From: Chase Bank <alerts@chase-portal.top>\nAuthentication-Results: spf=fail; dkim=fail; dmarc=fail"
    val result = com.example.data.verifier.EmailForensicsEngine.analyze(email)
    assertTrue("Should flag authentication failure", result.riskScore >= 70)
  }

  @Test
  fun `verify global jurisdictions loaded`() {
    val countries = com.example.data.global.GlobalJurisdictionRepository.getAllCountries()
    assertTrue(countries.size >= 5)
    val us = com.example.data.global.GlobalJurisdictionRepository.getJurisdiction("US")
    assertEquals("United States", us.countryName)
  }
}

