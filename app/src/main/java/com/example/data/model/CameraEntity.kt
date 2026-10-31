package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cameras")
data class CameraEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val locationName: String,
    val rtspUrl: String,
    val ipAddress: String,
    val port: Int = 554,
    val channel: Int = 1,
    val transport: String = "TCP", // TCP or UDP
    val manufacturer: String = "ONVIF Generic",
    val model: String = "IP Camera",
    val isFavorite: Boolean = false,
    val isOnline: Boolean = true,
    val fps: Int = 25,
    val resolution: String = "1920x1080 (HD)",
    val substreamResolution: String = "640x360 (SD)",
    val codec: String = "H.264",
    val previewDrawableRes: Int = 0,
    val lastSeenTimestamp: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false,
    val orderIndex: Int = 0
)

data class DiscoveredDevice(
    val id: String,
    val name: String,
    val ipAddress: String,
    val port: Int = 80,
    val rtspPort: Int = 554,
    val manufacturer: String,
    val model: String,
    val hardwareId: String,
    val onvifServiceUrl: String,
    val supportedProfiles: List<String> = listOf("MainStream (1080p, 25fps)", "SubStream (360p, 15fps)")
)
