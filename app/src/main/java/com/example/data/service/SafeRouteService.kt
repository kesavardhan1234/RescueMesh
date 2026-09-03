package com.example.data.service

data class RoutePoint(val lat: Double, val lon: Double, val elevationM: Double, val hazardDesc: String? = null)

data class SafeRouteOption(
    val routeId: String,
    val title: String,
    val distanceKm: Double,
    val estimatedMinutes: Int,
    val riskLevel: String, // "LOW", "MODERATE", "HIGH", "EXTREME"
    val isRecommended: Boolean,
    val recommendationBadge: String, // "✓ RECOMMENDED (HIGHEST SAFETY)", "⚠️ NOT RECOMMENDED (DANGEROUS FLOODING)"
    val rationale: String,
    val elevationProfile: String,
    val waypoints: List<RoutePoint>,
    val blockedRoadsAvoided: Int
)

class SafeRouteService {

    fun calculateRoutes(
        originLat: Double = 16.4960,
        originLon: Double = 80.6275,
        destinationShelterName: String = "St. Jude Elevated Community Center",
        destLat: Double = 16.5120,
        destLon: Double = 80.6480
    ): List<SafeRouteOption> {
        val routeA = SafeRouteOption(
            routeId = "ROUTE_DIRECT",
            title = "Direct Canal Road (Shortest)",
            distanceKm = 2.0,
            estimatedMinutes = 9,
            riskLevel = "HIGH",
            isRecommended = false,
            recommendationBadge = "⚠️ NOT RECOMMENDED (HIGH FLOOD HAZARD)",
            rationale = "Crosses Zone 04 Lowland Causeway with active 1.2m water logging and 1 blocked underpass. Extreme hydroplaning risk.",
            elevationProfile = "Lowland Dip (Min 5.2m Elevation)",
            waypoints = listOf(
                RoutePoint(originLat, originLon, 6.8, "Origin: Inundation Watch"),
                RoutePoint(16.5010, 80.6320, 5.2, "Culvert Underpass Submerged"),
                RoutePoint(16.5080, 80.6410, 9.4, "Standing water 40cm"),
                RoutePoint(destLat, destLon, 32.0, "Destination Shelter")
            ),
            blockedRoadsAvoided = 0
        )

        val routeB = SafeRouteOption(
            routeId = "ROUTE_HIGHLAND",
            title = "North Ridge Bypass (Flood-Protected)",
            distanceKm = 3.2,
            estimatedMinutes = 14,
            riskLevel = "LOW",
            isRecommended = true,
            recommendationBadge = "✓ RECOMMENDED SAFEST ROUTE",
            rationale = "Follows elevated ridge highway at 28-35m elevation. Completely bypasses canal overflow zones and flooded intersections.",
            elevationProfile = "Elevated Ridge (Min 24.5m Elevation)",
            waypoints = listOf(
                RoutePoint(originLat, originLon, 6.8, "Origin: Head Northward to Incline"),
                RoutePoint(16.5150, 80.6290, 24.5, "Hill Road Elevated Access"),
                RoutePoint(16.5220, 80.6410, 31.0, "Highland Flyover (Dry)"),
                RoutePoint(destLat, destLon, 32.0, "Destination Shelter Arrival")
            ),
            blockedRoadsAvoided = 2
        )

        return listOf(routeB, routeA)
    }
}
