package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cctv.ConnectionTestResult
import com.example.data.db.AppDatabase
import com.example.data.model.CameraEntity
import com.example.data.model.DiscoveredDevice
import com.example.data.model.LocationEntity
import com.example.data.preferences.UserPreferences
import com.example.data.repository.BillingRepository
import com.example.data.repository.CctvRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    CAMERAS_HOME,
    LIVE_GRID,
    SINGLE_CAMERA,
    ADD_CAMERA_METHOD,
    ONVIF_SCAN,
    MANUAL_RTSP,
    TEST_PREVIEW,
    CAMERA_DETAILS,
    MANAGE_LOCATIONS,
    UPGRADE_PRO,
    SETTINGS
}

enum class ScanState {
    IDLE,
    SCANNING,
    DISCOVERED,
    TESTING_STREAM,
    SUCCESS,
    ERROR
}

class CctvViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val userPreferences = UserPreferences(application)
    private val repository = CctvRepository(database.cameraDao(), database.locationDao())
    val billingRepository = BillingRepository(userPreferences)

    // Current navigation screen
    private val _currentScreen = MutableStateFlow(AppScreen.CAMERAS_HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Screen navigation stack for BackHandler
    private val screenStack = mutableListOf<AppScreen>()

    // User preferences flows
    val isProUser: StateFlow<Boolean> = userPreferences.isProUser
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val hasCompletedOnboarding: StateFlow<Boolean> = userPreferences.hasCompletedOnboarding
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val isDemoEnabled: StateFlow<Boolean> = userPreferences.isDemoEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val themeMode: StateFlow<String> = userPreferences.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, "DARK")

    val language: StateFlow<String> = userPreferences.language
        .stateIn(viewModelScope, SharingStarted.Eagerly, "ID")

    val keepScreenAwake: StateFlow<Boolean> = userPreferences.keepScreenAwake
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val autoReconnect: StateFlow<Boolean> = userPreferences.autoReconnect
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val defaultStreamQuality: StateFlow<String> = userPreferences.defaultStreamQuality
        .stateIn(viewModelScope, SharingStarted.Eagerly, "HD")

    val gridLayoutMode: StateFlow<Int> = userPreferences.gridLayoutMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, 2)

    // Cameras and Locations
    val allCameras: StateFlow<List<CameraEntity>> = repository.allCameras
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allLocations: StateFlow<List<LocationEntity>> = repository.allLocations
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Filtering & Search
    private val _selectedLocation = MutableStateFlow("Semua")
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredCameras: StateFlow<List<CameraEntity>> = combine(
        allCameras,
        _selectedLocation,
        _searchQuery
    ) { cameras, loc, query ->
        cameras.filter { cam ->
            val matchesLocation = loc == "Semua" || loc == "All" || cam.locationName.equals(loc, ignoreCase = true)
            val matchesQuery = query.isBlank() || cam.name.contains(query, ignoreCase = true) || cam.locationName.contains(query, ignoreCase = true) || cam.ipAddress.contains(query)
            matchesLocation && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Currently viewed single camera
    private val _activeCamera = MutableStateFlow<CameraEntity?>(null)
    val activeCamera: StateFlow<CameraEntity?> = _activeCamera.asStateFlow()

    // Single camera player controls
    private val _isAudioMuted = MutableStateFlow(true)
    val isAudioMuted: StateFlow<Boolean> = _isAudioMuted.asStateFlow()

    private val _currentStreamQuality = MutableStateFlow("HD")
    val currentStreamQuality: StateFlow<String> = _currentStreamQuality.asStateFlow()

    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    private val _snapshotMessage = MutableStateFlow<String?>(null)
    val snapshotMessage: StateFlow<String?> = _snapshotMessage.asStateFlow()

    // ONVIF discovery states
    private val _scanState = MutableStateFlow(ScanState.IDLE)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<DiscoveredDevice>> = _discoveredDevices.asStateFlow()

    private val _selectedDiscoveredDevice = MutableStateFlow<DiscoveredDevice?>(null)
    val selectedDiscoveredDevice: StateFlow<DiscoveredDevice?> = _selectedDiscoveredDevice.asStateFlow()

    // Add camera wizard state
    private val _wizardCameraName = MutableStateFlow("")
    val wizardCameraName: StateFlow<String> = _wizardCameraName.asStateFlow()

    private val _wizardRtspUrl = MutableStateFlow("")
    val wizardRtspUrl: StateFlow<String> = _wizardRtspUrl.asStateFlow()

    private val _wizardUsername = MutableStateFlow("admin")
    val wizardUsername: StateFlow<String> = _wizardUsername.asStateFlow()

    private val _wizardPassword = MutableStateFlow("")
    val wizardPassword: StateFlow<String> = _wizardPassword.asStateFlow()

    private val _wizardTransport = MutableStateFlow("TCP")
    val wizardTransport: StateFlow<String> = _wizardTransport.asStateFlow()

    private val _wizardLocation = MutableStateFlow("Rumah")
    val wizardLocation: StateFlow<String> = _wizardLocation.asStateFlow()

    private val _testConnectionResult = MutableStateFlow<ConnectionTestResult?>(null)
    val testConnectionResult: StateFlow<ConnectionTestResult?> = _testConnectionResult.asStateFlow()

    private val _isTestingConnection = MutableStateFlow(false)
    val isTestingConnection: StateFlow<Boolean> = _isTestingConnection.asStateFlow()

    // Limit warning
    private val _showLimitNotice = MutableStateFlow(false)
    val showLimitNotice: StateFlow<Boolean> = _showLimitNotice.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    // Navigation helpers
    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
            true
        } else if (_currentScreen.value != AppScreen.CAMERAS_HOME) {
            _currentScreen.value = AppScreen.CAMERAS_HOME
            true
        } else {
            false
        }
    }

    fun setLocationFilter(loc: String) {
        _selectedLocation.value = loc
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun openSingleCamera(camera: CameraEntity) {
        _activeCamera.value = camera
        _currentStreamQuality.value = defaultStreamQuality.value
        navigateTo(AppScreen.SINGLE_CAMERA)
    }

    fun openCameraDetails(camera: CameraEntity) {
        _activeCamera.value = camera
        navigateTo(AppScreen.CAMERA_DETAILS)
    }

    fun toggleAudioMute() {
        _isAudioMuted.value = !_isAudioMuted.value
    }

    fun setStreamQuality(quality: String) {
        _currentStreamQuality.value = quality
    }

    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
    }

    fun captureSnapshot(cameraName: String) {
        viewModelScope.launch {
            _snapshotMessage.value = "Tangkapan layar '$cameraName' berhasil disimpan ke Galeri."
            kotlinx.coroutines.delay(2800)
            _snapshotMessage.value = null
        }
    }

    fun setGridLayout(columns: Int) {
        // Enforce Pro feature for 3x3 layout
        if (columns == 3 && !isProUser.value) {
            navigateTo(AppScreen.UPGRADE_PRO)
            return
        }
        viewModelScope.launch {
            userPreferences.setGridLayoutMode(columns)
        }
    }

    fun toggleFavorite(camera: CameraEntity) {
        // Pro feature: favorite cameras
        if (!isProUser.value) {
            navigateTo(AppScreen.UPGRADE_PRO)
            return
        }
        viewModelScope.launch {
            repository.toggleFavorite(camera)
        }
    }

    fun checkCanAddCamera(): Boolean {
        val total = allCameras.value.size
        val maxAllowed = if (isProUser.value) 8 else 2
        return if (total >= maxAllowed) {
            _showLimitNotice.value = true
            navigateTo(AppScreen.UPGRADE_PRO)
            false
        } else {
            true
        }
    }

    fun dismissLimitNotice() {
        _showLimitNotice.value = false
    }

    // ONVIF Discovery
    fun startOnvifScan() {
        _scanState.value = ScanState.SCANNING
        _discoveredDevices.value = emptyList()
        viewModelScope.launch {
            try {
                val devices = repository.scanOnvifDevices()
                _discoveredDevices.value = devices
                _scanState.value = if (devices.isNotEmpty()) ScanState.DISCOVERED else ScanState.IDLE
            } catch (e: Exception) {
                _scanState.value = ScanState.ERROR
            }
        }
    }

    fun selectDiscoveredDevice(device: DiscoveredDevice) {
        _selectedDiscoveredDevice.value = device
        _wizardCameraName.value = device.name
        _wizardRtspUrl.value = "rtsp://${device.ipAddress}:${device.rtspPort}/live/ch0"
        _wizardUsername.value = "admin"
        _wizardPassword.value = ""
        _wizardLocation.value = "Rumah"
        _testConnectionResult.value = null
    }

    // Manual RTSP setup inputs
    fun updateWizardFields(
        name: String? = null,
        url: String? = null,
        user: String? = null,
        pass: String? = null,
        transport: String? = null,
        location: String? = null
    ) {
        name?.let { _wizardCameraName.value = it }
        url?.let { _wizardRtspUrl.value = it }
        user?.let { _wizardUsername.value = it }
        pass?.let { _wizardPassword.value = it }
        transport?.let { _wizardTransport.value = it }
        location?.let { _wizardLocation.value = it }
    }

    fun testConnection() {
        _isTestingConnection.value = true
        _testConnectionResult.value = null
        viewModelScope.launch {
            val result = repository.testConnection(
                rtspUrl = _wizardRtspUrl.value,
                transport = _wizardTransport.value
            )
            _testConnectionResult.value = result
            _isTestingConnection.value = false
        }
    }

    fun saveWizardCamera() {
        val device = _selectedDiscoveredDevice.value
        val newCam = CameraEntity(
            id = "cam_${UUID.randomUUID().toString().take(8)}",
            name = _wizardCameraName.value.ifBlank { "Kamera Baru" },
            locationName = _wizardLocation.value.ifBlank { "Rumah" },
            rtspUrl = _wizardRtspUrl.value,
            ipAddress = _wizardRtspUrl.value.substringAfter("://").substringBefore("/").substringAfter("@").substringBefore(":"),
            port = 554,
            channel = 1,
            transport = _wizardTransport.value,
            manufacturer = device?.manufacturer ?: "ONVIF IP Cam",
            model = device?.model ?: "Universal RTSP",
            isFavorite = false,
            isOnline = true,
            fps = 25,
            resolution = "1920x1080 (HD)",
            substreamResolution = "640x360 (SD)",
            codec = "H.264",
            previewDrawableRes = com.example.R.drawable.img_cctv_entrance,
            isDemo = false
        )
        viewModelScope.launch {
            repository.saveCamera(newCam)
            _activeCamera.value = newCam
            _currentScreen.value = AppScreen.CAMERAS_HOME
        }
    }

    fun updateCameraSettings(camera: CameraEntity) {
        viewModelScope.launch {
            repository.updateCamera(camera)
            _activeCamera.value = camera
        }
    }

    fun deleteCamera(camera: CameraEntity) {
        viewModelScope.launch {
            repository.deleteCamera(camera.id)
            if (_activeCamera.value?.id == camera.id) {
                _activeCamera.value = null
            }
            _currentScreen.value = AppScreen.CAMERAS_HOME
        }
    }

    // Locations
    fun addLocation(name: String) {
        viewModelScope.launch {
            repository.addLocation(name)
        }
    }

    fun deleteLocation(name: String) {
        viewModelScope.launch {
            repository.deleteLocation(name)
        }
    }

    // Settings actions
    fun setThemeMode(mode: String) {
        viewModelScope.launch { userPreferences.setThemeMode(mode) }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch { userPreferences.setLanguage(lang) }
    }

    fun setKeepScreenAwake(keep: Boolean) {
        viewModelScope.launch { userPreferences.setKeepScreenAwake(keep) }
    }

    fun setAutoReconnect(reconnect: Boolean) {
        viewModelScope.launch { userPreferences.setAutoReconnect(reconnect) }
    }

    fun toggleDemoMode(enable: Boolean) {
        viewModelScope.launch {
            userPreferences.setDemoEnabled(enable)
            if (enable) {
                repository.loadDemoCameras()
            } else {
                repository.clearDemoCameras()
            }
        }
    }

    fun purchasePro() {
        viewModelScope.launch {
            billingRepository.purchaseProLifetime()
        }
    }

    fun restorePurchase() {
        viewModelScope.launch {
            billingRepository.restorePurchases()
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferences.setOnboardingCompleted(true)
            _currentScreen.value = AppScreen.CAMERAS_HOME
        }
    }
}
