package com.example.data.service

import com.example.model.CommunicationChannel
import com.example.model.DeliveryStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NetworkStatus(
    val isInternetAvailable: Boolean = true,
    val isCellularAvailable: Boolean = true,
    val isLoraMeshAvailable: Boolean = true,
    val isEmergencyGatewayAvailable: Boolean = true,
    val activeChannel: CommunicationChannel = CommunicationChannel.INTERNET,
    val gatewaySignalStrengthDbm: Int = -74, // dBm
    val connectedPeersCount: Int = 4,
    val isSimulatedOfflineMode: Boolean = false
)

class CommunicationManager {

    private val _networkStatus = MutableStateFlow(NetworkStatus())
    val networkStatus: StateFlow<NetworkStatus> = _networkStatus.asStateFlow()

    fun setSimulatedOfflineMode(offline: Boolean) {
        val current = _networkStatus.value
        if (offline) {
            _networkStatus.value = current.copy(
                isInternetAvailable = false,
                isCellularAvailable = false,
                isLoraMeshAvailable = true,
                isEmergencyGatewayAvailable = true,
                activeChannel = CommunicationChannel.LORA_MESH,
                isSimulatedOfflineMode = true
            )
        } else {
            _networkStatus.value = current.copy(
                isInternetAvailable = true,
                isCellularAvailable = true,
                isLoraMeshAvailable = true,
                isEmergencyGatewayAvailable = true,
                activeChannel = CommunicationChannel.INTERNET,
                isSimulatedOfflineMode = false
            )
        }
    }

    fun forceChannel(channel: CommunicationChannel) {
        val current = _networkStatus.value
        _networkStatus.value = current.copy(
            activeChannel = channel,
            isInternetAvailable = (channel == CommunicationChannel.INTERNET),
            isCellularAvailable = (channel == CommunicationChannel.CELLULAR_SMS || channel == CommunicationChannel.INTERNET),
            isLoraMeshAvailable = (channel == CommunicationChannel.LORA_MESH || channel == CommunicationChannel.EMERGENCY_GATEWAY),
            isEmergencyGatewayAvailable = true
        )
    }

    /**
     * Attempts to transmit an emergency packet through the best available priority channel:
     * 1. Internet
     * 2. Cellular SMS
     * 3. Local LoRa / BLE Mesh
     * 4. Emergency Gateway (ESP32 Bridge)
     * 5. Satellite Backhaul (via Gateway)
     */
    suspend fun transmitEmergencyPacket(
        payload: String,
        onStatusUpdate: suspend (DeliveryStatus, CommunicationChannel) -> Unit
    ): Boolean {
        val status = _networkStatus.value

        // Step 1: Check priority channels
        val selectedChannel = when {
            status.isInternetAvailable -> CommunicationChannel.INTERNET
            status.isCellularAvailable -> CommunicationChannel.CELLULAR_SMS
            status.isLoraMeshAvailable -> CommunicationChannel.LORA_MESH
            status.isEmergencyGatewayAvailable -> CommunicationChannel.EMERGENCY_GATEWAY
            else -> CommunicationChannel.SATELLITE_BACKHAUL
        }

        // Inform user: Sending
        onStatusUpdate(DeliveryStatus.SENDING, selectedChannel)
        delay(700)

        // If in total blackout simulation without mesh
        if (!status.isInternetAvailable && !status.isCellularAvailable && !status.isLoraMeshAvailable && !status.isEmergencyGatewayAvailable) {
            onStatusUpdate(DeliveryStatus.RETRYING, selectedChannel)
            delay(1000)
            onStatusUpdate(DeliveryStatus.NO_CONNECTION, selectedChannel)
            return false
        }

        // Simulate mesh hopping or direct upload
        if (selectedChannel == CommunicationChannel.LORA_MESH || selectedChannel == CommunicationChannel.EMERGENCY_GATEWAY) {
            delay(800) // LoRa packets take ~800ms airtime
        }

        // Successfully received & acknowledged by backend or emergency relay
        onStatusUpdate(DeliveryStatus.DELIVERED, selectedChannel)
        return true
    }
}
