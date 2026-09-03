# FloodGuard AI 🌊🚨
### AI-Powered Early Flood Warning, GIS Hazard Mapping & Disaster Rescue Coordination Platform

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android_14+-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose_M3-4285F4?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Database](https://img.shields.io/badge/Storage-Room_SQLite-005C84?style=flat&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![AI](https://img.shields.io/badge/AI-Gemini_Multimodal-EA4335?style=flat&logo=google&logoColor=white)](https://ai.google.dev)
[![Offline Mesh](https://img.shields.io/badge/Offline-LoRa_/_BLE_Mesh-FF6F00?style=flat)](https://en.wikipedia.org/wiki/LoRa)

---

## 📌 Overview

**FloodGuard AI** is a next-generation Android disaster management platform engineered to predict, monitor, and coordinate emergency responses during severe flooding and extreme hydro-meteorological events. 

Built with **Modern Kotlin**, **Jetpack Compose (Material 3)**, **Room Database**, and **Gemini AI**, FloodGuard AI provides life-saving tools that function both online and in zero-connectivity environments via decentralized mesh/LoRa fallback protocols.

---

## 🌟 Key Features

### 1. 🛰️ Multi-Layer GIS Flood Inundation Engine
- **Interactive High-Performance Vector/Raster Canvas**: Real-time rendering with pinch-to-zoom, pan, and dynamic scaling.
- **3 Basemap Modes**:
  - `🛰️ Satellite Hybrid`: Realistic terrain satellite imagery with high-contrast hydro overlays.
  - `⛰️ Topographic DEM`: Digital elevation contour lines, slope grading, and elevation benchmarks.
  - `🗺️ Street Vector`: Clean emergency transit and road network layer.
- **8 Diverse Regional Scenarios**:
  - 🏘️ *Amaravathi Town & Rural* (Canal bunds, market bazaar lowlands, panchayat shelters)
  - 🏙️ *Vijayawada Metro Delta* (High-density urban wards, Prakasam Barrage, NH-16 highway)
  - 🏝️ *Diviseema Delta Island* (River estuary, ferry terminals, cyclone towers)
  - ⛰️ *Tirupati Foothills Town* (Flash flood gullies, temple hill basins, reservoir weirs)
  - 🏭 *Kandaleru Industrial Hub* (Thermal power plants, hazmat containment dykes)
  - 🏞️ *Polavaram River Gorge* (Deep mountain gorge, reservoir dam spillways)
  - 🌲 *Rampachodavaram Forest Hamlets* (Hill stream rivulets, highland ITDA schools)
  - 🌊 *Machilipatnam Port Town* (Coastal storm surges, sea dykes, hovercraft patrols)
- **Time-Lapse Surge Simulation Slider**: Simulate flood inundation progression from **Baseline Now** up to **+6h Peak (+2.2m surge)** with live marker water-depth updates.
- **Geodesic Distance & Elevation Ruler**: Instantly calculate straight-line distance, estimated walking evacuation time, and relative elevation difference ($+\Delta h$) to the nearest safe shelter.

### 2. 🆘 Resilient SOS & Off-Grid Mesh Emergency Broadcast
- **Multi-Channel Fallback System**:
  - 🌐 High-speed 4G/5G/Wi-Fi Internet
  - 📱 Low-bandwidth Cellular SMS Emergency Gateway
  - 📻 Off-Grid **LoRa / BLE Mesh** (868/915 MHz) for zero-cellular disaster zones
  - 🛰️ Satellite Backhaul Relay
- **One-Tap Distress Beacon**: Broadcasts GPS coordinates, battery level, medical emergency status, and trapped headcount.
- **Audible Emergency Siren & Strobe Torch**: Pulsing acoustic alarm with Morse SOS flashlight signaling for night rescues.
- **Real-Time Rescue Lifecycle Tracking**: From `WAITING` → `ASSIGNED` → `EN ROUTE` → `REACHED` → `RESCUED`.

### 3. 🛡️ Safe Haven Shelter Navigation & Evacuation Routes
- **Live Shelter Capacities**: Real-time occupancy status, dry ground indicators, generator power, medical aid availability, and clean water supplies.
- **Turn-by-Turn Safe Route Recommendations**: Avoids inundated roads, breached bridges, and landslide chokepoints.
- **Interactive Route Step Inspector**: Elevation profiles, hazard alerts, and emergency contact dispatchers.

### 4. 🧠 Gemini Multimodal AI Damage Assessment & Citizen Reports
- **AI Hazard Image Triage**: Analyze flood photos, damaged bridges, or submerged infrastructure using Gemini Vision to assess hazard severity and recommend evacuation actions.
- **Crowdsourced Hazard Submissions**: Submit geotagged citizen reports (water levels, fallen trees, road blockages) that automatically update local risk heatmaps.

### 5. 🔔 Early Warning Alerts & Automated Evacuation Guidance
- Multi-tier alert hierarchy: `NORMAL`, `WATCH`, `ALERT`, and `EXTREME DANGER`.
- Push notifications with actionable safety checklists (essential documents, emergency kits, first aid, power bank prep).
- Automated SMS alerts for local community volunteers and emergency response teams.

### 6. 🎖️ Incident Command & Rescue Operations Dashboard
- Dedicated portal for NDRF / SDRF / Civil Defense personnel.
- Live queue of active rescue requests prioritized by medical severity, water ingress velocity, and vulnerable demographics (elderly, infants).
- One-touch dispatch, team assignment, and direct responder communications.

---

## 🏗️ Architecture & Technology Stack

```
FloodGuard AI
├── presentation (UI)
│   ├── screens/
│   │   ├── HomeScreen.kt             # Overview, weather telemetry & quick actions
│   │   ├── LiveFloodMapScreen.kt     # High-performance custom GIS Canvas map
│   │   ├── SheltersAndRouteScreen.kt # Shelter locator & safe evacuation routing
│   │   ├── SosScreen.kt              # Emergency beacon, siren & off-grid mesh
│   │   ├── AlertsScreen.kt           # Warning bulletins & Gemini damage assessment
│   │   ├── RescueDashboardScreen.kt  # SDRF/NDRF incident command portal
│   │   └── ProfileScreen.kt          # Emergency contacts, medical profile & offline caches
│   └── theme/                       # Material 3 dark/light responsive palette
├── model/                            # Domain entities & telemetry data contracts
└── data/local/                       # Room Database (Shelters, Zones, SOS requests, Reports)
```

| Component | Technology | Description |
|---|---|---|
| **Language** | Kotlin 2.0+ | Modern, concise, null-safe language |
| **UI Framework** | Jetpack Compose | Declarative UI adhering to Material Design 3 (M3) |
| **Architecture** | MVVM / Clean Architecture | Unidirectional Data Flow (UDF) with Coroutines & StateFlow |
| **Local Persistence** | Android Room Database | Robust offline-first SQLite database for shelters, SOS logs, and offline maps |
| **GIS Rendering** | Android Canvas / DrawScope | Custom 60fps hardware-accelerated vector rendering with gestural pan/zoom |
| **AI Integration** | Google Gemini API | Multimodal vision & situational intelligence for disaster response |
| **Asynchronous Ops** | Kotlin Coroutines & Flow | Non-blocking background streams and telemetry synchronization |

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Ladybug (2024.2+) or newer
- **JDK 17** or **JDK 21**
- Android SDK **35** (Minimum SDK **26** / Android 8.0 Oreo+)

### Build & Run
1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/FloodGuard-AI.git
   cd FloodGuard-AI
   ```

2. **Open in Android Studio**:
   - Open Android Studio, select **Open**, and navigate to the cloned project folder.

3. **Configure API Keys (Optional for Gemini AI)**:
   - Create a `.env` file in the root directory (refer to `.env.example`):
     ```properties
     GEMINI_API_KEY="your_api_key_here"
     ```

4. **Compile and Run**:
   - Select an emulator (API 34+) or a physical Android device with USB debugging enabled.
   - Click **Run** (`Shift + F10`) or execute via Gradle:
     ```bash
     gradle assembleDebug
     ```

---

## 🔒 Permissions Declared
- `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION`: For accurate GPS distress coordinates and proximity calculations to shelters.
- `INTERNET` & `ACCESS_NETWORK_STATE`: For remote disaster telemetry sync and AI evaluation.
- `CAMERA`: For citizen damage reporting and multimodal flood hazard analysis.
- `VIBRATE` & `WAKE_LOCK`: For critical emergency sirens and strobe alarms.

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License
This project is licensed under the **Apache License 2.0** or **MIT License** - see the `LICENSE` file for details.
