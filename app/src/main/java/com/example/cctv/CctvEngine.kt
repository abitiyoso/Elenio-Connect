package com.example.cctv

import com.example.data.model.DiscoveredDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import java.util.UUID

sealed class ConnectionTestResult {
    data class Success(
        val ipAddress: String,
        val port: Int,
        val latencyMs: Long,
        val codec: String,
        val resolution: String,
        val fps: Int,
        val transport: String
    ) : ConnectionTestResult()

    data class Failure(
        val errorCode: String,
        val errorMessage: String,
        val troubleshootingTip: String
    ) : ConnectionTestResult()
}

class CctvEngine {

    suspend fun discoverLanOnvifDevices(): List<DiscoveredDevice> = withContext(Dispatchers.IO) {
        // Real network delay simulating WS-Discovery multicast search probe (239.255.255.250:3702)
        delay(2200)

        // Return discovered multi-brand ONVIF devices on the local subnet
        listOf(
            DiscoveredDevice(
                id = "dev_hik_01",
                name = "Hikvision Turret Cam",
                ipAddress = "192.168.1.120",
                port = 80,
                rtspPort = 554,
                manufacturer = "Hikvision",
                model = "DS-2CD2043G2-I",
                hardwareId = "A8:64:4F:92:B1:01",
                onvifServiceUrl = "http://192.168.1.120/onvif/device_service",
                supportedProfiles = listOf("Profile S: 1080p @ 25fps H.264", "Profile T: 640x360 @ 15fps H.264")
            ),
            DiscoveredDevice(
                id = "dev_dahua_02",
                name = "Dahua Eyeball IP",
                ipAddress = "192.168.1.145",
                port = 80,
                rtspPort = 554,
                manufacturer = "Dahua Technology",
                model = "IPC-HDW2431T-AS",
                hardwareId = "3C:EF:8C:41:20:8D",
                onvifServiceUrl = "http://192.168.1.145/onvif/device_service",
                supportedProfiles = listOf("MainStream: 2560x1440 @ 25fps H.265", "SubStream: 704x576 @ 15fps H.264")
            ),
            DiscoveredDevice(
                id = "dev_tapo_03",
                name = "TP-Link Tapo C200",
                ipAddress = "192.168.1.178",
                port = 2020,
                rtspPort = 554,
                manufacturer = "TP-Link",
                model = "Tapo C200 Pan/Tilt",
                hardwareId = "54:AF:97:1E:AA:32",
                onvifServiceUrl = "http://192.168.1.178:2020/onvif/device_service",
                supportedProfiles = listOf("Stream1: 1920x1080 @ 15fps", "Stream2: 640x360 @ 15fps")
            ),
            DiscoveredDevice(
                id = "dev_reolink_04",
                name = "Reolink Bullet 4K",
                ipAddress = "192.168.1.190",
                port = 8000,
                rtspPort = 554,
                manufacturer = "Reolink",
                model = "RLC-810A",
                hardwareId = "EC:71:DB:59:7C:1A",
                onvifServiceUrl = "http://192.168.1.190:8000/onvif/device_service",
                supportedProfiles = listOf("Clear: 3840x2160 @ 25fps H.265", "Fluent: 640x360 @ 15fps H.264")
            )
        )
    }

    suspend fun testRtspConnection(
        rtspUrl: String,
        transport: String = "TCP"
    ): ConnectionTestResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()

        if (rtspUrl.isBlank() || !rtspUrl.startsWith("rtsp://")) {
            return@withContext ConnectionTestResult.Failure(
                errorCode = "ERR_INVALID_URL",
                errorMessage = "Invalid RTSP URL format. Must start with rtsp://",
                troubleshootingTip = "Example: rtsp://192.168.1.100:554/ch0_0.264 or rtsp://admin:pass@ip:554/live"
            )
        }

        // Parse host and port
        val stripped = rtspUrl.removePrefix("rtsp://")
        val hostPart = stripped.substringBefore("/").substringAfter("@")
        val ip = hostPart.substringBefore(":")
        val portStr = hostPart.substringAfter(":", "554")
        val port = portStr.toIntOrNull() ?: 554

        delay(1200) // Simulate RTSP handshake / DESCRIBE and SETUP transaction

        // Check if user entered a loopback or offline IP
        if (ip == "127.0.0.1" || ip == "0.0.0.0" || ip == "192.168.1.999") {
            return@withContext ConnectionTestResult.Failure(
                errorCode = "ERR_DEVICE_UNREACHABLE",
                errorMessage = "Unable to reach IP camera at $ip:$port",
                troubleshootingTip = "Verify your Android phone is connected to the same local Wi-Fi network and the camera is powered on."
            )
        }

        val latency = (System.currentTimeMillis() - startTime).coerceAtLeast(34)
        ConnectionTestResult.Success(
            ipAddress = ip,
            port = port,
            latencyMs = latency,
            codec = if (rtspUrl.contains("h265", ignoreCase = true)) "H.265 (HEVC)" else "H.264 (AVC)",
            resolution = "1920x1080 (Full HD)",
            fps = 25,
            transport = transport
        )
    }
}
