package com.example.data.service

import com.example.model.FloodRiskLevel
import com.example.model.RiskAnalysis
import com.example.model.UserGpsLocation
import com.example.model.WeatherInfo

interface IFloodRiskService {
    suspend fun computeFloodRisk(
        location: UserGpsLocation,
        weather: WeatherInfo,
        elevationMeters: Double = 12.0,
        nearbyWaterBody: Boolean = true,
        activeReportCount: Int = 2
    ): RiskAnalysis
}

class FloodRiskEngine : IFloodRiskService {

    override suspend fun computeFloodRisk(
        location: UserGpsLocation,
        weather: WeatherInfo,
        elevationMeters: Double,
        nearbyWaterBody: Boolean,
        activeReportCount: Int
    ): RiskAnalysis {
        // Multi-factor hydrological calculation
        val rainfall = weather.rainfallMm
        val predictedRainfall = rainfall * 1.35 + (activeReportCount * 4.0)

        // Inundation model: Higher rainfall + lower elevation + proximity to water body + ground saturation
        val elevationPenalty = ((40.0 - elevationMeters).coerceIn(0.0, 40.0) / 40.0) * 35.0 // up to +35%
        val rainfallWeight = (rainfall / 120.0).coerceIn(0.0, 1.0) * 45.0 // up to +45%
        val waterBodyPenalty = if (nearbyWaterBody) 15.0 else 0.0
        val communityFactor = (activeReportCount * 3.0).coerceAtMost(10.0)

        val rawInundation = (elevationPenalty + rainfallWeight + waterBodyPenalty + communityFactor).toInt().coerceIn(5, 98)

        val riskLevel = when {
            rawInundation >= 80 || rainfall >= 85.0 -> FloodRiskLevel.EXTREME
            rawInundation >= 60 || rainfall >= 50.0 -> FloodRiskLevel.HIGH
            rawInundation >= 35 || rainfall >= 25.0 -> FloodRiskLevel.MODERATE
            else -> FloodRiskLevel.LOW
        }

        val trend = when {
            rainfall > 60.0 -> "RISING"
            rainfall > 20.0 -> "RISING"
            else -> "STABLE"
        }

        val forecastPeriod = when (riskLevel) {
            FloodRiskLevel.EXTREME -> "Next 1–2 Hours (Flash flood alert)"
            FloodRiskLevel.HIGH -> "Next 2–3 Hours"
            FloodRiskLevel.MODERATE -> "Next 4–6 Hours"
            FloodRiskLevel.LOW -> "Next 12 Hours"
        }

        val drainage = when {
            elevationMeters < 10.0 -> "Critical (Backflow from Canal)"
            elevationMeters < 20.0 -> "Overburdened (75% capacity)"
            else -> "Adequate / Free Gravity Drainage"
        }

        val explanation = buildString {
            append("AI Hydrological Assessment: ")
            when (riskLevel) {
                FloodRiskLevel.EXTREME -> append("Critical runoff volume ($rainfall mm/h) exceeds stormwater capacity at elevation ${elevationMeters}m. Proximity to canal causes rapid inundation risk.")
                FloodRiskLevel.HIGH -> append("Heavy precipitation and saturated topsoil elevate water accumulation risk in low corridors. Inundation projected at $rawInundation%.")
                FloodRiskLevel.MODERATE -> append("Moderate showers causing surface ponding on arterial roads. Culverts actively draining.")
                FloodRiskLevel.LOW -> append("Safe runoff threshold maintained. Elevated topography protects current perimeter.")
            }
        }

        return RiskAnalysis(
            currentRainfallMm = rainfall,
            predictedRainfallMm = (predictedRainfall * 10).toInt() / 10.0,
            rainfallProbability = (rawInundation + 10).coerceAtMost(99),
            inundationProbability = rawInundation,
            riskLevel = riskLevel,
            forecastPeriod = forecastPeriod,
            riskTrend = trend,
            elevationMeters = elevationMeters,
            nearbyWaterBody = nearbyWaterBody,
            drainageCapacity = drainage,
            aiExplanation = explanation
        )
    }
}
