package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.config.ApiConfig
import com.example.data.local.AuthSession
import com.example.data.local.SessionManager
import com.example.data.mock.MockFamilyDataSource
import com.example.data.remote.ApiResponseParser
import com.example.domain.model.FinancialSummary
import com.example.domain.rbac.AccessControl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name matches Malakul Jibal identity`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Malakul Jibal", appName)
    }

    @Test
    fun `default api base url points to live malakuljibal api`() {
        val config = ApiConfig()
        assertEquals("https://api.malakuljibal.com", config.baseUrl)
        assertEquals("https://api.malakuljibal.com/", config.normalizedBaseUrl)
        assertFalse(config.useMockData)
    }

    @Test
    fun `session manager persists and clears jwt session`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = SessionManager(context)
        manager.clearSession()
        assertNull(manager.loadSession())

        val expected = AuthSession(
            token = "eyJhbGciOiJIUzI1NiJ9.payload.sig",
            userId = "10",
            familyId = "42",
            fullName = "Tariq Al-Jibal",
            email = "tariq@malakuljibal.com",
            role = "SUPER_ADMIN"
        )
        manager.saveSession(expected)
        val loaded = manager.loadSession()
        assertNotNull(loaded)
        assertEquals(expected.token, loaded?.token)
        assertEquals("42", loaded?.familyId)

        manager.clearSession()
        assertNull(manager.loadSession())
    }

    @Test
    fun `api response parser handles live backend payloads cleanly`() {
        val loginJson = """
            {"data":{"token":"jwt-abc-123","user":{"id":7,"family_id":12,"name":"Tariq","email":"tariq@malakuljibal.com","role":"SUPER_ADMIN"}}}
        """.trimIndent()
        val session = ApiResponseParser.parseLoginSession(loginJson, "fallback@example.com")
        assertNotNull(session)
        assertEquals("jwt-abc-123", session?.token)
        assertEquals("12", session?.familyId)

        val summaryJson = """{"data":{"total_income":"9500.00","total_expense":"4200.00","balance":"5300.00"}}"""
        val summary = ApiResponseParser.parseFinanceSummary(
            summaryJson,
            FinancialSummary("Now", "$", 0, 0, 0, 0, 1)
        )
        assertEquals(9500L, summary.monthlyIncome)
        assertEquals(4200L, summary.monthlyExpenses)
        assertEquals(5300L, summary.netBalance)

        val growthJson = """{"data":[{"id":1,"weight_kg":"10.2","length_cm":"79.0","head_circumference_cm":"46.9","measurement_date":"2026-11-19"}]}"""
        val growth = ApiResponseParser.parseBabyGrowth(growthJson, "1")
        assertEquals(1, growth.size)
        assertEquals(10.2f, growth.first().weightKg, 0.01f)
    }

    @Test
    fun `rbac enforces private medical records and finance boundaries`() {
        val dataSource = MockFamilyDataSource()
        val members = dataSource.getFamilyMembers()
        val profiles = dataSource.getMedicalProfiles()

        val tariq = members.first { it.id == MockFamilyDataSource.ID_TARIQ }
        val hafiz = members.first { it.id == MockFamilyDataSource.ID_HAFIZ }
        val zayd = members.first { it.id == MockFamilyDataSource.ID_ZAYD }

        val hafizMed = profiles.first { it.memberId == hafiz.id }
        val zaydMed = profiles.first { it.memberId == zayd.id }

        assertFalse(AccessControl.canViewMedicalProfile(tariq, hafiz, hafizMed))
        assertTrue(AccessControl.canViewMedicalProfile(hafiz, hafiz, hafizMed))
        assertTrue(AccessControl.canViewMedicalProfile(tariq, zayd, zaydMed))
        assertFalse(AccessControl.canViewFinanceOverview(hafiz))
        assertTrue(AccessControl.canViewFinanceOverview(tariq))
    }
}
