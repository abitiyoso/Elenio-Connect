package com.example.data.repository

import com.example.R
import com.example.cctv.CctvEngine
import com.example.cctv.ConnectionTestResult
import com.example.data.db.CameraDao
import com.example.data.db.LocationDao
import com.example.data.model.CameraEntity
import com.example.data.model.DiscoveredDevice
import com.example.data.model.LocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class CctvRepository(
    private val cameraDao: CameraDao,
    private val locationDao: LocationDao,
    private val cctvEngine: CctvEngine = CctvEngine()
) {

    val allCameras: Flow<List<CameraEntity>> = cameraDao.getAllCameras()
    val allLocations: Flow<List<LocationEntity>> = locationDao.getAllLocations()
    val nonDemoCameraCount: Flow<Int> = cameraDao.getNonDemoCameraCount()

    suspend fun initializeDefaultDataIfEmpty() {
        // Initialize default locations if empty
        val locations = locationDao.getAllLocations().first()
        if (locations.isEmpty()) {
            locationDao.insertLocations(
                listOf(
                    LocationEntity(name = "Rumah", iconName = "home", orderIndex = 0),
                    LocationEntity(name = "Toko Utama", iconName = "store", orderIndex = 1),
                    LocationEntity(name = "Gudang", iconName = "warehouse", orderIndex = 2),
                    LocationEntity(name = "Kantor", iconName = "business", orderIndex = 3)
                )
            )
        }

        // Initialize realistic demo cameras if database is empty
        val totalCount = cameraDao.getTotalCameraCount()
        if (totalCount == 0) {
            loadDemoCameras()
        }
    }

    suspend fun loadDemoCameras() {
        val demoCameras = listOf(
            CameraEntity(
                id = "cam_demo_entrance",
                name = "Pintu Utama & Garasi",
                locationName = "Rumah",
                rtspUrl = "rtsp://192.168.1.120:554/live/ch0",
                ipAddress = "192.168.1.120",
                port = 554,
                channel = 1,
                transport = "TCP",
                manufacturer = "Hikvision",
                model = "DS-2CD2043G2-I",
                isFavorite = true,
                isOnline = true,
                fps = 25,
                resolution = "1920x1080 (HD)",
                substreamResolution = "640x360 (SD)",
                codec = "H.264",
                previewDrawableRes = R.drawable.img_cctv_entrance,
                isDemo = true,
                orderIndex = 0
            ),
            CameraEntity(
                id = "cam_demo_store",
                name = "Area Kasir & Display",
                locationName = "Toko Utama",
                rtspUrl = "rtsp://192.168.1.145:554/cam/realmonitor?channel=1&subtype=0",
                ipAddress = "192.168.1.145",
                port = 554,
                channel = 1,
                transport = "TCP",
                manufacturer = "Dahua",
                model = "IPC-HDW2431T-AS",
                isFavorite = true,
                isOnline = true,
                fps = 25,
                resolution = "2560x1440 (2K)",
                substreamResolution = "704x576 (SD)",
                codec = "H.265",
                previewDrawableRes = R.drawable.img_cctv_store,
                isDemo = true,
                orderIndex = 1
            ),
            CameraEntity(
                id = "cam_demo_warehouse",
                name = "Loading Bay & Rak Logistik",
                locationName = "Gudang",
                rtspUrl = "rtsp://192.168.1.190:554/h264Preview_01_main",
                ipAddress = "192.168.1.190",
                port = 554,
                channel = 1,
                transport = "TCP",
                manufacturer = "Reolink",
                model = "RLC-810A",
                isFavorite = false,
                isOnline = true,
                fps = 25,
                resolution = "3840x2160 (4K)",
                substreamResolution = "640x360 (SD)",
                codec = "H.265",
                previewDrawableRes = R.drawable.img_cctv_warehouse,
                isDemo = true,
                orderIndex = 2
            )
        )
        cameraDao.insertCameras(demoCameras)
    }

    suspend fun clearDemoCameras() {
        cameraDao.deleteDemoCameras()
    }

    suspend fun saveCamera(camera: CameraEntity) {
        cameraDao.insertCamera(camera)
    }

    suspend fun updateCamera(camera: CameraEntity) {
        cameraDao.updateCamera(camera)
    }

    suspend fun toggleFavorite(camera: CameraEntity) {
        cameraDao.updateCamera(camera.copy(isFavorite = !camera.isFavorite))
    }

    suspend fun deleteCamera(id: String) {
        cameraDao.deleteCameraById(id)
    }

    suspend fun getCameraById(id: String): CameraEntity? {
        return cameraDao.getCameraById(id)
    }

    // Locations
    suspend fun addLocation(name: String, iconName: String = "folder") {
        locationDao.insertLocation(LocationEntity(name = name, iconName = iconName))
    }

    suspend fun deleteLocation(name: String, fallbackLocation: String = "Rumah") {
        cameraDao.reassignCamerasLocation(oldLocation = name, newLocation = fallbackLocation)
        locationDao.deleteLocationByName(name)
    }

    // CCTV Engine Delegations
    suspend fun scanOnvifDevices(): List<DiscoveredDevice> {
        return cctvEngine.discoverLanOnvifDevices()
    }

    suspend fun testConnection(rtspUrl: String, transport: String): ConnectionTestResult {
        return cctvEngine.testRtspConnection(rtspUrl, transport)
    }
}
