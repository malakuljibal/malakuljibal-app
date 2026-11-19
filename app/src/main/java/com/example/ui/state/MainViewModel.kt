package com.example.ui.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.config.ApiConfig
import com.example.data.local.AuthSession
import com.example.data.local.SessionManager
import com.example.data.mock.MockFamilyDataSource
import com.example.data.remote.ApiResult
import com.example.data.remote.LiveFamilyRepository
import com.example.data.remote.RetrofitClientProvider
import com.example.domain.model.*
import com.example.domain.rbac.AccessControl
import com.example.domain.repository.FamilyDataSource
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MalakulUiState(
    val apiConfig: ApiConfig = ApiConfig(),
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val authSession: AuthSession? = null,
    val isLiveMode: Boolean = false,
    val showAuthScreen: Boolean = true,
    val isAuthLoading: Boolean = false,
    val authError: String? = null,
    val isRefreshingAll: Boolean = false,
    val isLoadingFinance: Boolean = false,
    val isLoadingHealth: Boolean = false,
    val isLoadingDocuments: Boolean = false,
    val isLoadingBaby: Boolean = false,
    val isSubmitting: Boolean = false,
    val globalError: String? = null,
    val actionMessage: String? = null,
    val liveBabyId: String = "1",
    val family: Family,
    val members: List<FamilyMember>,
    val activeUserId: String,
    val selectedProfileId: String,
    val selectedHealthMemberId: String,
    val selectedChildMemberId: String,
    val announcements: List<FamilyAnnouncement>,
    val financialSummary: FinancialSummary,
    val monthlyComparisons: List<MonthlyComparison>,
    val transactions: List<TransactionItem>,
    val budgetCategories: List<BudgetCategoryItem>,
    val savingsGoals: List<SavingsGoal>,
    val medicalProfiles: List<MedicalProfile>,
    val healthMeasurements: List<HealthMeasurement>,
    val medicalRecords: List<MedicalRecord>,
    val medications: List<MedicationItem>,
    val labResults: List<LabResultPreview>,
    val upcomingAppointments: List<HealthAppointment>,
    val documents: List<FamilyDocument>,
    val childProfiles: List<ChildProfile>,
    val growthMeasurements: List<GrowthMeasurement>,
    val milestones: List<DevelopmentalMilestone>,
    val feedingLogs: List<FeedingLog>,
    val immunizations: List<ImmunizationRecord>,
    val babyJournalEntries: List<BabyJournalEntry>,
    val familyEvents: List<FamilyEvent>,
    val activityFeed: List<ActivityFeedItem>,
    // Interactive filters
    val documentSearchQuery: String = "",
    val selectedDocumentCategory: DocumentCategory = DocumentCategory.ALL,
    val selectedCalendarCategory: EventCategory? = null,
    val selectedFinanceFilter: AccessLevel? = null,
    val selectedGrowthMetric: Int = 0
) {
    val activeUser: FamilyMember
        get() {
            val session = authSession
            if (isLiveMode && session != null) {
                val parsedRole = when (session.role.uppercase()) {
                    "SUPER_ADMIN", "ADMIN", "FAMILY_ADMIN" -> UserRole.SUPER_ADMIN
                    "PARENT" -> UserRole.PARENT
                    else -> UserRole.SUPER_ADMIN
                }
                val initials = session.fullName
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .joinToString("") { it.first().uppercase() }
                    .ifBlank { "MJ" }
                return FamilyMember(
                    id = session.userId,
                    familyId = session.familyId,
                    fullName = session.fullName,
                    shortName = session.fullName.substringBefore(" "),
                    initials = initials,
                    avatarRes = null,
                    dateOfBirth = "Synced Account",
                    ageLabel = "Family #${session.familyId}",
                    gender = "-",
                    relationship = RelationshipType.HUSBAND,
                    role = parsedRole,
                    email = session.email,
                    phone = null,
                    hasIndependentAccount = true,
                    bio = "Authenticated live session on ${apiConfig.baseUrl} (Family ID: ${session.familyId}).",
                    financialContributionNote = "Connected to live household ledger.",
                    monthlyAllowanceOrShare = "Live API Member",
                    permissions = members.first().permissions,
                    personalHighlights = listOf("Authenticated via JWT · Family ID #${session.familyId}")
                )
            }
            return members.firstOrNull { it.id == activeUserId } ?: members.first()
        }

    val selectedProfile: FamilyMember
        get() = members.firstOrNull { it.id == selectedProfileId } ?: activeUser

    val selectedHealthMember: FamilyMember
        get() = members.firstOrNull { it.id == selectedHealthMemberId } ?: activeUser

    val selectedChildProfile: ChildProfile
        get() = childProfiles.firstOrNull { it.memberId == selectedChildMemberId } ?: childProfiles.first()

    val visibleTransactions: List<TransactionItem>
        get() = if (isLiveMode) {
            transactions.filter { selectedFinanceFilter == null || it.accessLevel == selectedFinanceFilter }
        } else {
            transactions.filter { tx ->
                AccessControl.canViewTransaction(activeUser, tx) &&
                    (selectedFinanceFilter == null || tx.accessLevel == selectedFinanceFilter)
            }
        }

    val visibleDocuments: List<FamilyDocument>
        get() = documents.filter { doc ->
            val allowed = isLiveMode || AccessControl.canViewDocument(activeUser, doc)
            allowed &&
                (selectedDocumentCategory == DocumentCategory.ALL || doc.category == selectedDocumentCategory) &&
                (documentSearchQuery.isBlank() ||
                    doc.title.contains(documentSearchQuery, ignoreCase = true) ||
                    doc.ownerName.contains(documentSearchQuery, ignoreCase = true) ||
                    doc.description.contains(documentSearchQuery, ignoreCase = true) ||
                    doc.tags.any { it.contains(documentSearchQuery, ignoreCase = true) })
        }

    val expiringDocumentsCount: Int
        get() = documents.count {
            (isLiveMode || AccessControl.canViewDocument(activeUser, it)) && it.status == DocumentStatus.EXPIRING_SOON
        }

    val visibleEvents: List<FamilyEvent>
        get() = familyEvents.filter { event ->
            (isLiveMode || AccessControl.canViewEvent(activeUser, event)) &&
                (selectedCalendarCategory == null || event.category == selectedCalendarCategory)
        }

    val visibleActivityFeed: List<ActivityFeedItem>
        get() = if (isLiveMode) activityFeed else activityFeed.filter { item ->
            when (item.accessLevel) {
                AccessLevel.FAMILY_SHARED -> true
                AccessLevel.ADMIN_ONLY -> activeUser.role == UserRole.SUPER_ADMIN
                AccessLevel.PARENT_MANAGED -> activeUser.role == UserRole.SUPER_ADMIN || activeUser.role == UserRole.PARENT
                AccessLevel.PRIVATE_PERSONAL -> item.relatedMemberId == activeUser.id
            }
        }
}

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val mockSource: FamilyDataSource = MockFamilyDataSource()
    private val sessionManager = SessionManager(application)
    private val clientProvider = RetrofitClientProvider(tokenProvider = { sessionManager.getToken() })
    private val liveRepository = LiveFamilyRepository(clientProvider)

    private val _uiState: MutableStateFlow<MalakulUiState>
    val uiState: StateFlow<MalakulUiState>

    init {
        val savedSession = sessionManager.loadSession()
        val savedUrl = sessionManager.getApiBaseUrl()
        val hasLiveSession = savedSession != null

        _uiState = MutableStateFlow(
            MalakulUiState(
                apiConfig = ApiConfig(
                    baseUrl = savedUrl,
                    useMockData = !hasLiveSession,
                    isBackendConnected = hasLiveSession,
                    syncStatusLabel = if (hasLiveSession) "Live API · Syncing" else "Sign in to connect"
                ),
                authSession = savedSession,
                isLiveMode = hasLiveSession,
                showAuthScreen = !hasLiveSession,
                family = mockSource.getFamily(),
                members = mockSource.getFamilyMembers(),
                activeUserId = MockFamilyDataSource.ID_TARIQ,
                selectedProfileId = MockFamilyDataSource.ID_TARIQ,
                selectedHealthMemberId = MockFamilyDataSource.ID_TARIQ,
                selectedChildMemberId = MockFamilyDataSource.ID_ZAYD,
                announcements = mockSource.getAnnouncements(),
                financialSummary = if (hasLiveSession) {
                    FinancialSummary("Live Household Ledger", "$", 0, 0, 0, 0, 1)
                } else {
                    mockSource.getFinancialSummary()
                },
                monthlyComparisons = mockSource.getMonthlyComparisons(),
                transactions = if (hasLiveSession) emptyList() else mockSource.getTransactions(),
                budgetCategories = mockSource.getBudgetCategories(),
                savingsGoals = mockSource.getSavingsGoals(),
                medicalProfiles = mockSource.getMedicalProfiles(),
                healthMeasurements = mockSource.getHealthMeasurements(),
                medicalRecords = if (hasLiveSession) emptyList() else mockSource.getMedicalRecords(),
                medications = mockSource.getMedications(),
                labResults = mockSource.getLabResults(),
                upcomingAppointments = mockSource.getUpcomingAppointments(),
                documents = if (hasLiveSession) emptyList() else mockSource.getDocuments(),
                childProfiles = mockSource.getChildProfiles(),
                growthMeasurements = if (hasLiveSession) emptyList() else mockSource.getGrowthMeasurements(),
                milestones = mockSource.getMilestones(),
                feedingLogs = mockSource.getFeedingLogs(),
                immunizations = mockSource.getImmunizations(),
                babyJournalEntries = mockSource.getBabyJournalEntries(),
                familyEvents = mockSource.getFamilyEvents(),
                activityFeed = mockSource.getActivityFeed()
            )
        )
        uiState = _uiState.asStateFlow()

        if (hasLiveSession) {
            refreshAllLiveData()
        }
    }

    // --- Authentication ---

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(authError = "Please enter both email and password.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authError = null) }
            val baseUrl = _uiState.value.apiConfig.baseUrl
            when (val result = liveRepository.login(baseUrl, email, password)) {
                is ApiResult.Success -> {
                    val session = result.data
                    sessionManager.saveSession(session)
                    _uiState.update { current ->
                        current.copy(
                            authSession = session,
                            isLiveMode = true,
                            showAuthScreen = false,
                            isAuthLoading = false,
                            authError = null,
                            globalError = null,
                            apiConfig = current.apiConfig.copy(
                                useMockData = false,
                                isBackendConnected = true,
                                syncStatusLabel = "Live API · Family #${session.familyId}"
                            ),
                            transactions = emptyList(),
                            medicalRecords = emptyList(),
                            documents = emptyList(),
                            growthMeasurements = emptyList()
                        )
                    }
                    refreshAllLiveData()
                }
                is ApiResult.Unauthorized -> {
                    _uiState.update { it.copy(isAuthLoading = false, authError = result.message) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isAuthLoading = false, authError = result.message) }
                }
            }
        }
    }

    fun logout(expiredMessage: String? = null) {
        sessionManager.clearSession()
        _uiState.update { current ->
            current.copy(
                authSession = null,
                isLiveMode = false,
                showAuthScreen = true,
                isAuthLoading = false,
                isRefreshingAll = false,
                isLoadingFinance = false,
                isLoadingHealth = false,
                isLoadingDocuments = false,
                isLoadingBaby = false,
                isSubmitting = false,
                authError = expiredMessage,
                globalError = null,
                actionMessage = null,
                apiConfig = current.apiConfig.copy(
                    isBackendConnected = false,
                    syncStatusLabel = if (expiredMessage != null) "Session Expired" else "Signed Out"
                )
            )
        }
    }

    fun enterOfflineDemoMode(memberId: String = MockFamilyDataSource.ID_TARIQ) {
        _uiState.update { current ->
            current.copy(
                isLiveMode = false,
                showAuthScreen = false,
                authError = null,
                globalError = null,
                activeUserId = memberId,
                selectedProfileId = memberId,
                selectedHealthMemberId = memberId,
                financialSummary = mockSource.getFinancialSummary(),
                transactions = mockSource.getTransactions(),
                medicalRecords = mockSource.getMedicalRecords(),
                documents = mockSource.getDocuments(),
                growthMeasurements = mockSource.getGrowthMeasurements(),
                apiConfig = current.apiConfig.copy(
                    useMockData = true,
                    isBackendConnected = false,
                    syncStatusLabel = "Offline Demo Mode"
                )
            )
        }
    }

    private fun handleTokenExpired(message: String) {
        logout(expiredMessage = "Session expired: $message Please sign in again.")
    }

    // --- Live API Data Loading ---

    fun refreshAllLiveData() {
        val session = _uiState.value.authSession ?: return
        if (!_uiState.value.isLiveMode) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRefreshingAll = true,
                    isLoadingFinance = true,
                    isLoadingHealth = true,
                    isLoadingDocuments = true,
                    isLoadingBaby = true,
                    globalError = null
                )
            }
            val baseUrl = _uiState.value.apiConfig.baseUrl
            val familyId = session.familyId
            val babyId = _uiState.value.liveBabyId

            coroutineScope {
                val summaryDeferred = async {
                    liveRepository.fetchFinanceSummary(baseUrl, familyId, _uiState.value.financialSummary)
                }
                val txDeferred = async {
                    liveRepository.fetchTransactions(baseUrl, familyId, session.userId, session.fullName)
                }
                val healthDeferred = async {
                    liveRepository.fetchHealthRecords(baseUrl, familyId, session.userId)
                }
                val docsDeferred = async {
                    liveRepository.fetchDocuments(baseUrl, familyId, session.userId, session.fullName)
                }
                val babyDeferred = async {
                    liveRepository.fetchBabyGrowth(baseUrl, familyId, babyId)
                }

                val summaryRes = summaryDeferred.await()
                val txRes = txDeferred.await()
                val healthRes = healthDeferred.await()
                val docsRes = docsDeferred.await()
                val babyRes = babyDeferred.await()

                val unauthorized = listOf(summaryRes, txRes, healthRes, docsRes, babyRes)
                    .filterIsInstance<ApiResult.Unauthorized>()
                    .firstOrNull()
                if (unauthorized != null) {
                    handleTokenExpired(unauthorized.message)
                    return@coroutineScope
                }

                val errors = listOf(summaryRes, txRes, healthRes, docsRes, babyRes)
                    .filterIsInstance<ApiResult.Error>()
                    .map { it.message }
                    .distinct()

                _uiState.update { current ->
                    val newSummary = (summaryRes as? ApiResult.Success)?.data ?: current.financialSummary
                    val newTx = (txRes as? ApiResult.Success)?.data ?: current.transactions
                    val newHealth = (healthRes as? ApiResult.Success)?.data ?: current.medicalRecords
                    val newDocs = (docsRes as? ApiResult.Success)?.data ?: current.documents
                    val newGrowth = (babyRes as? ApiResult.Success)?.data ?: current.growthMeasurements

                    val updatedChildren = syncChildLatestFromGrowth(current.childProfiles, newGrowth)

                    current.copy(
                        isRefreshingAll = false,
                        isLoadingFinance = false,
                        isLoadingHealth = false,
                        isLoadingDocuments = false,
                        isLoadingBaby = false,
                        financialSummary = newSummary,
                        transactions = newTx,
                        medicalRecords = newHealth,
                        documents = newDocs,
                        growthMeasurements = newGrowth,
                        childProfiles = updatedChildren,
                        globalError = errors.firstOrNull(),
                        apiConfig = current.apiConfig.copy(
                            isBackendConnected = errors.isEmpty(),
                            syncStatusLabel = if (errors.isEmpty()) {
                                "Live API · Synced (Family #$familyId)"
                            } else {
                                "Live API · Partial Sync"
                            }
                        )
                    )
                }
            }
        }
    }

    // --- 1. Finance Endpoints ---

    fun loadFinanceData() {
        val session = _uiState.value.authSession ?: return
        if (!_uiState.value.isLiveMode) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingFinance = true, globalError = null) }
            val baseUrl = _uiState.value.apiConfig.baseUrl
            coroutineScope {
                val sumDef = async {
                    liveRepository.fetchFinanceSummary(baseUrl, session.familyId, _uiState.value.financialSummary)
                }
                val txDef = async {
                    liveRepository.fetchTransactions(baseUrl, session.familyId, session.userId, session.fullName)
                }
                val sumRes = sumDef.await()
                val txRes = txDef.await()

                if (sumRes is ApiResult.Unauthorized) { handleTokenExpired(sumRes.message); return@coroutineScope }
                if (txRes is ApiResult.Unauthorized) { handleTokenExpired(txRes.message); return@coroutineScope }

                val err = (sumRes as? ApiResult.Error)?.message ?: (txRes as? ApiResult.Error)?.message
                _uiState.update { current ->
                    current.copy(
                        isLoadingFinance = false,
                        financialSummary = (sumRes as? ApiResult.Success)?.data ?: current.financialSummary,
                        transactions = (txRes as? ApiResult.Success)?.data ?: current.transactions,
                        globalError = err
                    )
                }
            }
        }
    }

    fun addTransaction(type: String, amount: Double, description: String, transactionDate: String) {
        val session = _uiState.value.authSession ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, globalError = null, actionMessage = null) }
            when (val res = liveRepository.createTransaction(
                baseUrl = _uiState.value.apiConfig.baseUrl,
                familyId = session.familyId,
                type = type,
                amount = amount,
                description = description,
                transactionDate = transactionDate
            )) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false, actionMessage = "Transaction saved to server.") }
                    loadFinanceData()
                }
                is ApiResult.Unauthorized -> handleTokenExpired(res.message)
                is ApiResult.Error -> _uiState.update { it.copy(isSubmitting = false, globalError = res.message) }
            }
        }
    }

    fun deleteTransaction(transactionId: String) {
        val session = _uiState.value.authSession ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, globalError = null, actionMessage = null) }
            when (val res = liveRepository.deleteTransaction(
                baseUrl = _uiState.value.apiConfig.baseUrl,
                familyId = session.familyId,
                transactionId = transactionId
            )) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false, actionMessage = "Transaction deleted.") }
                    loadFinanceData()
                }
                is ApiResult.Unauthorized -> handleTokenExpired(res.message)
                is ApiResult.Error -> _uiState.update { it.copy(isSubmitting = false, globalError = res.message) }
            }
        }
    }

    // --- 2. Health Endpoints ---

    fun loadHealthRecords() {
        val session = _uiState.value.authSession ?: return
        if (!_uiState.value.isLiveMode) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHealth = true, globalError = null) }
            when (val res = liveRepository.fetchHealthRecords(_uiState.value.apiConfig.baseUrl, session.familyId, session.userId)) {
                is ApiResult.Success -> _uiState.update { it.copy(isLoadingHealth = false, medicalRecords = res.data) }
                is ApiResult.Unauthorized -> handleTokenExpired(res.message)
                is ApiResult.Error -> _uiState.update { it.copy(isLoadingHealth = false, globalError = res.message) }
            }
        }
    }

    fun addHealthRecord(
        diagnosis: String,
        hospitalName: String,
        doctorName: String,
        notes: String,
        recordDate: String
    ) {
        val session = _uiState.value.authSession ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, globalError = null, actionMessage = null) }
            when (val res = liveRepository.createHealthRecord(
                baseUrl = _uiState.value.apiConfig.baseUrl,
                familyId = session.familyId,
                diagnosis = diagnosis,
                hospitalName = hospitalName,
                doctorName = doctorName,
                notes = notes,
                recordDate = recordDate
            )) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false, actionMessage = "Health record saved.") }
                    loadHealthRecords()
                }
                is ApiResult.Unauthorized -> handleTokenExpired(res.message)
                is ApiResult.Error -> _uiState.update { it.copy(isSubmitting = false, globalError = res.message) }
            }
        }
    }

    // --- 3. Documents Endpoints ---

    fun loadDocuments() {
        val session = _uiState.value.authSession ?: return
        if (!_uiState.value.isLiveMode) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDocuments = true, globalError = null) }
            when (val res = liveRepository.fetchDocuments(
                baseUrl = _uiState.value.apiConfig.baseUrl,
                familyId = session.familyId,
                defaultOwnerId = session.userId,
                defaultOwnerName = session.fullName
            )) {
                is ApiResult.Success -> _uiState.update { it.copy(isLoadingDocuments = false, documents = res.data) }
                is ApiResult.Unauthorized -> handleTokenExpired(res.message)
                is ApiResult.Error -> _uiState.update { it.copy(isLoadingDocuments = false, globalError = res.message) }
            }
        }
    }

    fun addDocument(title: String, category: String, documentDate: String, notes: String) {
        val session = _uiState.value.authSession ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, globalError = null, actionMessage = null) }
            when (val res = liveRepository.createDocument(
                baseUrl = _uiState.value.apiConfig.baseUrl,
                familyId = session.familyId,
                title = title,
                category = category,
                documentDate = documentDate,
                notes = notes
            )) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false, actionMessage = "Document added to vault.") }
                    loadDocuments()
                }
                is ApiResult.Unauthorized -> handleTokenExpired(res.message)
                is ApiResult.Error -> _uiState.update { it.copy(isSubmitting = false, globalError = res.message) }
            }
        }
    }

    fun deleteDocument(documentId: String) {
        val session = _uiState.value.authSession ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, globalError = null, actionMessage = null) }
            when (val res = liveRepository.deleteDocument(
                baseUrl = _uiState.value.apiConfig.baseUrl,
                familyId = session.familyId,
                documentId = documentId
            )) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false, actionMessage = "Document deleted.") }
                    loadDocuments()
                }
                is ApiResult.Unauthorized -> handleTokenExpired(res.message)
                is ApiResult.Error -> _uiState.update { it.copy(isSubmitting = false, globalError = res.message) }
            }
        }
    }

    // --- 4. Baby Growth Endpoints ---

    fun updateLiveBabyId(babyId: String) {
        val cleaned = babyId.trim().ifBlank { "1" }
        _uiState.update { it.copy(liveBabyId = cleaned) }
        if (_uiState.value.isLiveMode) {
            loadBabyGrowth(cleaned)
        }
    }

    fun loadBabyGrowth(babyId: String = _uiState.value.liveBabyId) {
        val session = _uiState.value.authSession ?: return
        if (!_uiState.value.isLiveMode) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBaby = true, globalError = null) }
            when (val res = liveRepository.fetchBabyGrowth(
                baseUrl = _uiState.value.apiConfig.baseUrl,
                familyId = session.familyId,
                babyId = babyId
            )) {
                is ApiResult.Success -> {
                    _uiState.update { current ->
                        current.copy(
                            isLoadingBaby = false,
                            growthMeasurements = res.data,
                            childProfiles = syncChildLatestFromGrowth(current.childProfiles, res.data)
                        )
                    }
                }
                is ApiResult.Unauthorized -> handleTokenExpired(res.message)
                is ApiResult.Error -> _uiState.update { it.copy(isLoadingBaby = false, globalError = res.message) }
            }
        }
    }

    fun addBabyGrowth(
        babyId: String,
        weightKg: Double,
        lengthCm: Double,
        headCircumferenceCm: Double,
        measurementDate: String
    ) {
        val session = _uiState.value.authSession ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, globalError = null, actionMessage = null) }
            when (val res = liveRepository.createBabyGrowth(
                baseUrl = _uiState.value.apiConfig.baseUrl,
                familyId = session.familyId,
                babyId = babyId,
                weightKg = weightKg,
                lengthCm = lengthCm,
                headCircumferenceCm = headCircumferenceCm,
                measurementDate = measurementDate
            )) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false, actionMessage = "Growth measurement recorded.") }
                    loadBabyGrowth(babyId)
                }
                is ApiResult.Unauthorized -> handleTokenExpired(res.message)
                is ApiResult.Error -> _uiState.update { it.copy(isSubmitting = false, globalError = res.message) }
            }
        }
    }

    // --- UI State & Filters ---

    fun switchActiveUser(memberId: String) {
        _uiState.update { current ->
            val target = current.members.firstOrNull { it.id == memberId && it.hasIndependentAccount }
                ?: return@update current
            current.copy(
                activeUserId = target.id,
                selectedProfileId = target.id,
                selectedHealthMemberId = target.id,
                showAuthScreen = false
            )
        }
    }

    fun selectProfile(memberId: String) {
        _uiState.update { it.copy(selectedProfileId = memberId) }
    }

    fun selectHealthMember(memberId: String) {
        _uiState.update { it.copy(selectedHealthMemberId = memberId) }
    }

    fun selectChildMember(memberId: String) {
        _uiState.update { it.copy(selectedChildMemberId = memberId) }
    }

    fun setThemeMode(mode: ThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun toggleQuickTheme() {
        _uiState.update { current ->
            val next = if (current.themeMode == ThemeMode.DARK) ThemeMode.LIGHT else ThemeMode.DARK
            current.copy(themeMode = next)
        }
    }

    fun updateDocumentSearch(query: String) {
        _uiState.update { it.copy(documentSearchQuery = query) }
    }

    fun selectDocumentCategory(category: DocumentCategory) {
        _uiState.update { it.copy(selectedDocumentCategory = category) }
    }

    fun selectCalendarCategory(category: EventCategory?) {
        _uiState.update { it.copy(selectedCalendarCategory = category) }
    }

    fun selectFinanceFilter(accessLevel: AccessLevel?) {
        _uiState.update { it.copy(selectedFinanceFilter = accessLevel) }
    }

    fun selectGrowthMetric(index: Int) {
        _uiState.update { it.copy(selectedGrowthMetric = index) }
    }

    fun updateApiBaseUrl(newUrl: String) {
        val cleaned = newUrl.trim().ifBlank { ApiConfig.DEFAULT_HOSTINGER_API_URL }
        sessionManager.saveApiBaseUrl(cleaned)
        _uiState.update { current ->
            current.copy(apiConfig = current.apiConfig.copy(baseUrl = cleaned))
        }
    }

    fun openAuthScreen() {
        _uiState.update { it.copy(showAuthScreen = true, authError = null) }
    }

    fun closeAuthScreen() {
        _uiState.update { it.copy(showAuthScreen = false) }
    }

    fun clearFeedbackMessages() {
        _uiState.update { it.copy(globalError = null, actionMessage = null) }
    }

    private fun syncChildLatestFromGrowth(
        profiles: List<ChildProfile>,
        growth: List<GrowthMeasurement>
    ): List<ChildProfile> {
        val latest = growth.lastOrNull() ?: return profiles
        return profiles.mapIndexed { index, child ->
            if (index == 0) {
                child.copy(
                    latestWeightKg = latest.weightKg,
                    latestLengthCm = latest.lengthCm,
                    latestHeadCircumferenceCm = latest.headCircumferenceCm,
                    lastMeasuredDate = latest.dateLabel
                )
            } else {
                child
            }
        }
    }
}
