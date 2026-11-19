package com.example.data.remote

import com.example.data.local.AuthSession
import com.example.domain.model.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToLong

/**
 * Resilient JSON parser for the Malakul Jibal PHP 8.x REST API.
 * Supports both bare JSON payloads and common { "data": ... } API envelopes,
 * as well as stringified numeric values returned by MariaDB / PDO.
 */
object ApiResponseParser {

    fun extractErrorMessage(rawError: String?, fallback: String): String {
        if (rawError.isNullOrBlank()) return fallback
        return try {
            val obj = JSONObject(rawError)
            listOf("message", "error", "detail")
                .firstNotNullOfOrNull { key -> obj.optString(key).takeIf { it.isNotBlank() && it != "null" } }
                ?: fallback
        } catch (_: Exception) {
            fallback
        }
    }

    fun parseLoginSession(rawJson: String, fallbackEmail: String): AuthSession? {
        val root = JSONObject(rawJson)
        val data = root.optJSONObject("data") ?: root
        val token = firstNonBlank(
            data.optString("token"),
            data.optString("access_token"),
            data.optString("jwt"),
            root.optString("token"),
            root.optString("access_token")
        ) ?: return null

        val userObj = data.optJSONObject("user")
            ?: root.optJSONObject("user")
            ?: data

        val userId = firstNonBlank(
            userObj.optString("id"),
            userObj.optString("user_id"),
            data.optString("user_id")
        ) ?: "1"

        val familyId = firstNonBlank(
            userObj.optString("family_id"),
            data.optString("family_id"),
            root.optString("family_id"),
            userObj.optJSONObject("family")?.optString("id")
        ) ?: "1"

        val fullName = firstNonBlank(
            userObj.optString("full_name"),
            userObj.optString("name"),
            data.optString("full_name"),
            fallbackEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
        ) ?: "Family Member"

        val email = firstNonBlank(
            userObj.optString("email"),
            data.optString("email"),
            fallbackEmail
        ) ?: fallbackEmail

        val role = firstNonBlank(
            userObj.optString("role"),
            data.optString("role")
        ) ?: "SUPER_ADMIN"

        return AuthSession(
            token = token,
            userId = userId,
            familyId = familyId,
            fullName = fullName,
            email = email,
            role = role
        )
    }

    fun parseFinanceSummary(rawJson: String, fallback: FinancialSummary): FinancialSummary {
        val obj = unwrapObject(rawJson) ?: return fallback
        val income = readLong(obj, "total_income", "monthly_income", "income")
        val expense = readLong(obj, "total_expense", "total_expenses", "monthly_expenses", "expense", "expenses")
        val balance = readLong(obj, "balance", "net_balance", "available_cash", "cash")
        val savings = readLong(obj, "savings", "total_savings")
        val budget = readLong(obj, "monthly_budget", "budget_limit", "budget").let {
            if (it > 0L) it else maxOf(expense, income, 1L)
        }
        val monthLabel = firstNonBlank(obj.optString("month"), obj.optString("period"), "Live Household Ledger")!!
        val currency = firstNonBlank(obj.optString("currency_symbol"), obj.optString("currency")) ?: "$"

        return FinancialSummary(
            monthLabel = monthLabel,
            currencySymbol = if (currency.equals("IDR", true)) "Rp " else if (currency.equals("USD", true)) "$" else currency,
            monthlyIncome = income,
            monthlyExpenses = expense,
            availableCash = if (balance != 0L) balance else (income - expense).coerceAtLeast(0L),
            totalSavings = savings,
            monthlyBudgetLimit = budget
        )
    }

    fun parseTransactions(rawJson: String, activeUserId: String, activeUserName: String): List<TransactionItem> {
        val array = unwrapArray(rawJson, "transactions", "items", "data") ?: return emptyList()
        return buildList {
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = firstNonBlank(obj.optString("id"), obj.optString("transaction_id")) ?: "tx-live-$i"
                val rawType = firstNonBlank(obj.optString("type"), "expense")!!.lowercase()
                val type = if (rawType.contains("inc")) TransactionType.INCOME else TransactionType.EXPENSE
                val amount = readLong(obj, "amount", "value")
                val desc = firstNonBlank(obj.optString("description"), obj.optString("title"), "Household Transaction")!!
                val category = firstNonBlank(obj.optString("category"), if (type == TransactionType.INCOME) "Income" else "Expense")!!
                val date = firstNonBlank(obj.optString("transaction_date"), obj.optString("date"), obj.optString("created_at"), "-")!!
                val memberName = firstNonBlank(obj.optString("member_name"), obj.optString("user_name"), activeUserName)!!
                val payment = firstNonBlank(obj.optString("payment_method"), "Household Account")!!
                add(
                    TransactionItem(
                        id = id,
                        title = desc,
                        category = category,
                        amount = amount,
                        type = type,
                        date = date.take(10),
                        paymentMethod = payment,
                        responsibleMemberId = firstNonBlank(obj.optString("user_id"), activeUserId)!!,
                        responsibleMemberName = memberName,
                        description = desc,
                        hasReceipt = obj.optBoolean("has_receipt", false),
                        accessLevel = parseAccessLevel(obj.optString("access_level"), AccessLevel.FAMILY_SHARED)
                    )
                )
            }
        }
    }

    fun parseHealthRecords(rawJson: String, defaultMemberId: String): List<MedicalRecord> {
        val array = unwrapArray(rawJson, "records", "health_records", "items", "data") ?: return emptyList()
        return buildList {
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = firstNonBlank(obj.optString("id"), obj.optString("record_id")) ?: "hr-live-$i"
                val diagnosis = firstNonBlank(obj.optString("diagnosis"), "Medical Consultation")!!
                val hospital = firstNonBlank(obj.optString("hospital_name"), obj.optString("facility"), "Medical Clinic")!!
                val doctor = firstNonBlank(obj.optString("doctor_name"), "Attending Physician")!!
                val notes = firstNonBlank(obj.optString("notes"), obj.optString("recommendations"), "No additional notes.")!!
                val date = firstNonBlank(obj.optString("record_date"), obj.optString("consultation_date"), obj.optString("created_at"), "-")!!
                add(
                    MedicalRecord(
                        id = id,
                        memberId = firstNonBlank(obj.optString("member_id"), obj.optString("user_id"), defaultMemberId)!!,
                        consultationDate = date.take(10),
                        facility = hospital,
                        doctorName = doctor,
                        reportedSymptoms = firstNonBlank(obj.optString("symptoms"), notes)!!,
                        recordedDiagnosis = diagnosis,
                        prescribedSummary = firstNonBlank(obj.optString("prescribed_medication"), "See clinician notes")!!,
                        doctorRecommendations = notes,
                        accessLevel = parseAccessLevel(obj.optString("access_level"), AccessLevel.PRIVATE_PERSONAL)
                    )
                )
            }
        }
    }

    fun parseDocuments(rawJson: String, defaultOwnerId: String, defaultOwnerName: String): List<FamilyDocument> {
        val array = unwrapArray(rawJson, "documents", "items", "data") ?: return emptyList()
        return buildList {
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = firstNonBlank(obj.optString("id"), obj.optString("document_id")) ?: "doc-live-$i"
                val title = firstNonBlank(obj.optString("title"), "Untitled Document")!!
                val catRaw = firstNonBlank(obj.optString("category"), "OTHER")!!
                val category = matchDocumentCategory(catRaw)
                val docDate = firstNonBlank(obj.optString("document_date"), obj.optString("issue_date"), obj.optString("created_at"), "-")!!
                val expDate = firstNonBlank(obj.optString("expiration_date"), obj.optString("expiry_date"))
                val notes = firstNonBlank(obj.optString("notes"), obj.optString("description"), "Stored in Malakul Jibal Vault")!!
                val ownerName = firstNonBlank(obj.optString("owner_name"), defaultOwnerName)!!
                add(
                    FamilyDocument(
                        id = id,
                        title = title,
                        category = category,
                        ownerMemberId = firstNonBlank(obj.optString("owner_id"), obj.optString("user_id"), defaultOwnerId)!!,
                        ownerName = ownerName,
                        issueDate = docDate.take(10),
                        expirationDate = expDate?.take(10),
                        daysUntilExpiry = null,
                        description = notes,
                        tags = listOf(catRaw),
                        uploadedBy = firstNonBlank(obj.optString("uploaded_by"), ownerName)!!,
                        uploadTimestamp = firstNonBlank(obj.optString("created_at"), docDate)!!.take(16),
                        fileFormat = firstNonBlank(obj.optString("file_format"), "DOC")!!.uppercase(),
                        fileSizeLabel = firstNonBlank(obj.optString("file_size"), "Vault Record")!!,
                        versionLabel = firstNonBlank(obj.optString("version"), "v1.0")!!,
                        status = DocumentStatus.ACTIVE,
                        accessLevel = parseAccessLevel(obj.optString("access_level"), AccessLevel.FAMILY_SHARED),
                        recentAuditEntries = listOf("Synced from live API · ${docDate.take(10)}")
                    )
                )
            }
        }
    }

    fun parseBabyGrowth(rawJson: String, babyId: String): List<GrowthMeasurement> {
        val array = unwrapArray(rawJson, "growth", "measurements", "records", "items", "data") ?: return emptyList()
        return buildList {
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = firstNonBlank(obj.optString("id"), obj.optString("measurement_id")) ?: "gm-live-$i"
                val weight = readDouble(obj, "weight_kg", "weight").toFloat()
                val length = readDouble(obj, "length_cm", "height_cm", "length").toFloat()
                val head = readDouble(obj, "head_circumference_cm", "head_cm", "head_circumference").toFloat()
                val date = firstNonBlank(obj.optString("measurement_date"), obj.optString("date"), obj.optString("created_at"), "-")!!
                val note = firstNonBlank(obj.optString("notes"), obj.optString("note"), "Recorded via live API")!!
                add(
                    GrowthMeasurement(
                        id = id,
                        childMemberId = babyId,
                        ageMonths = obj.optInt("age_months", i),
                        dateLabel = date.take(10),
                        weightKg = weight,
                        lengthCm = length,
                        headCircumferenceCm = head,
                        note = note
                    )
                )
            }
        }
    }

    private fun unwrapObject(rawJson: String): JSONObject? {
        val trimmed = rawJson.trim()
        if (!trimmed.startsWith("{")) return null
        val root = JSONObject(trimmed)
        return root.optJSONObject("data") ?: root.optJSONObject("summary") ?: root
    }

    private fun unwrapArray(rawJson: String, vararg keys: String): JSONArray? {
        val trimmed = rawJson.trim()
        if (trimmed.startsWith("[")) return JSONArray(trimmed)
        if (!trimmed.startsWith("{")) return null
        val root = JSONObject(trimmed)
        for (key in keys) {
            root.optJSONArray(key)?.let { return it }
        }
        val nestedData = root.optJSONObject("data")
        if (nestedData != null) {
            for (key in keys) {
                nestedData.optJSONArray(key)?.let { return it }
            }
        }
        return null
    }

    private fun firstNonBlank(vararg values: String?): String? {
        return values.firstOrNull { !it.isNullOrBlank() && !it.equals("null", ignoreCase = true) }
    }

    private fun readDouble(obj: JSONObject, vararg keys: String): Double {
        for (key in keys) {
            if (obj.has(key) && !obj.isNull(key)) {
                val raw = obj.opt(key)
                when (raw) {
                    is Number -> return raw.toDouble()
                    is String -> raw.toDoubleOrNull()?.let { return it }
                }
            }
        }
        return 0.0
    }

    private fun readLong(obj: JSONObject, vararg keys: String): Long {
        return readDouble(obj, *keys).roundToLong()
    }

    private fun parseAccessLevel(raw: String?, fallback: AccessLevel): AccessLevel {
        val norm = raw?.uppercase().orEmpty()
        return when {
            norm.contains("ADMIN") -> AccessLevel.ADMIN_ONLY
            norm.contains("PRIVATE") -> AccessLevel.PRIVATE_PERSONAL
            norm.contains("PARENT") -> AccessLevel.PARENT_MANAGED
            norm.contains("SHARED") || norm.contains("FAMILY") -> AccessLevel.FAMILY_SHARED
            else -> fallback
        }
    }

    private fun matchDocumentCategory(raw: String): DocumentCategory {
        val norm = raw.lowercase()
        return when {
            norm.contains("passport") -> DocumentCategory.PASSPORT
            norm.contains("birth") -> DocumentCategory.BIRTH_CERT
            norm.contains("marriage") -> DocumentCategory.MARRIAGE_CERT
            norm.contains("id") || norm.contains("identity") || norm.contains("kk") -> DocumentCategory.IDENTITY
            norm.contains("edu") || norm.contains("school") -> DocumentCategory.EDUCATION
            norm.contains("insur") -> DocumentCategory.INSURANCE
            norm.contains("prop") || norm.contains("house") -> DocumentCategory.PROPERTY
            norm.contains("veh") || norm.contains("car") || norm.contains("sim") -> DocumentCategory.VEHICLE
            norm.contains("fin") || norm.contains("tax") -> DocumentCategory.FINANCIAL
            norm.contains("med") || norm.contains("health") -> DocumentCategory.MEDICAL
            else -> DocumentCategory.OTHER
        }
    }
}
