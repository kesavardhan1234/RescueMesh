package com.example.ui.localization

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    TELUGU("te", "Telugu", "తెలుగు"),
    HINDI("hi", "Hindi", "हिंदी")
}

object StringsProvider {
    fun get(key: String, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.ENGLISH -> englishStrings[key] ?: key
            AppLanguage.TELUGU -> teluguStrings[key] ?: englishStrings[key] ?: key
            AppLanguage.HINDI -> hindiStrings[key] ?: englishStrings[key] ?: key
        }
    }

    private val englishStrings = mapOf(
        "app_title" to "FLOODGUARD AI",
        "where_am_i" to "WHERE AM I?",
        "am_i_in_danger" to "AM I IN DANGER?",
        "where_should_i_go" to "WHERE SHOULD I GO?",
        "how_do_i_get_help" to "HOW DO I GET HELP?",
        
        "you_are_safe" to "YOU ARE SAFE",
        "caution" to "CAUTION",
        "high_flood_risk" to "HIGH FLOOD RISK",
        "extreme_flood_risk" to "EXTREME FLOOD RISK",
        "move_to_safety" to "MOVE TO SAFETY",
        "rescue_active" to "RESCUE REQUEST ACTIVE",
        
        "rainfall" to "Rainfall",
        "inundation_risk" to "Inundation Risk",
        "expected_time" to "Expected",
        "next_3_hours" to "Next 3 Hours",
        "next_2_hours" to "Next 2 Hours",
        
        "flood_map" to "FLOOD MAP",
        "safe_place" to "SAFE PLACE",
        "sos" to "EMERGENCY SOS",
        "hold_for_sos" to "HOLD FOR SOS",
        "hold_2_sec" to "Hold 2.5s to dispatch live GPS rescue",
        "holding" to "HOLDING...",
        
        "recommended_route" to "RECOMMENDED ROUTE",
        "flooded_route" to "FLOODED ROUTE",
        "route_safer_note" to "The recommended route is safer, even if it is longer.",
        "find_safe_route" to "FIND SAFE ROUTE",
        "safe_shelter" to "SAFE SHELTER",
        "low_risk" to "Low Risk Ground",
        "capacity_available" to "Capacity Available",
        "get_safe_route" to "GET SAFE ROUTE",
        
        "connected" to "CONNECTED",
        "weak_connection" to "WEAK CONNECTION",
        "no_network" to "NO MOBILE NETWORK",
        "offline_mode_desc" to "Emergency mode is active. Your GPS still determines your exact location. Relaying via LoRa Mesh / Emergency Gateway.",
        "emergency_net_available" to "EMERGENCY NETWORK AVAILABLE",
        "sos_waiting" to "SOS WAITING TO SEND",
        "sos_sent" to "SOS SENT TO RESCUE TEAM",
        
        "report_flood" to "REPORT FLOOD",
        "hazard_flooded_area" to "Flooded Area",
        "hazard_water_level" to "High Water Level",
        "hazard_road_blocked" to "Road Blocked",
        "hazard_building_flooded" to "Building Flooded",
        "hazard_other" to "Other Hazard",
        "submit_report" to "SUBMIT REPORT",
        
        "what_to_do" to "WHAT TO DO IMMEDIATELY",
        "action_1" to "Move away from low-lying drains and riverbanks.",
        "action_2" to "Never drive or walk through moving flood water.",
        "action_3" to "Move towards the nearest safe shelter or high ground.",
        
        "cancel_sos" to "CANCEL SOS",
        "update_location" to "UPDATE GPS LOCATION",
        "emergency_speed_dial" to "EMERGENCY SPEED DIAL"
    )

    private val teluguStrings = mapOf(
        "app_title" to "ఫ్లడ్‌గార్డ్ AI",
        "where_am_i" to "నేను ఎక్కడ ఉన్నాను?",
        "am_i_in_danger" to "నేను ప్రమాదంలో ఉన్నానా?",
        "where_should_i_go" to "నేను ఎక్కడికి వెళ్ళాలి?",
        "how_do_i_get_help" to "సహాయం ఎలా పొందాలి?",
        
        "you_are_safe" to "మీరు సురక్షితంగా ఉన్నారు",
        "caution" to "జాగ్రత్త వహించండి",
        "high_flood_risk" to "అధిక వరద ప్రమాదం",
        "extreme_flood_risk" to "అత్యంత తీవ్రమైన వరద ప్రమాదం",
        "move_to_safety" to "సురక్షిత ప్రాంతానికి వెళ్ళండి",
        "rescue_active" to "సహాయక బృందం అభ్యర్థన యాక్టివ్",
        
        "rainfall" to "వర్షపాతం",
        "inundation_risk" to "వరద ముంపు ప్రమాదం",
        "expected_time" to "అంచనా సమయం",
        "next_3_hours" to "రాబోయే 3 గంటల్లో",
        "next_2_hours" to "రాబోయే 2 గంటల్లో",
        
        "flood_map" to "వరద మ్యాప్",
        "safe_place" to "సురక్షిత స్థలం",
        "sos" to "అత్యవసర SOS",
        "hold_for_sos" to "సహాయం కోసం నొక్కి పట్టుకోండి",
        "hold_2_sec" to "రెస్క్యూ పంపడానికి 2.5 సెకన్లు నొక్కి పట్టుకోండి",
        "holding" to "పట్టుకుని ఉన్నారు...",
        
        "recommended_route" to "సిఫార్సు చేయబడిన సురక్షిత మార్గం",
        "flooded_route" to "వరద ముంపు మార్గం (ప్రమాదకరం)",
        "route_safer_note" to "సిఫార్సు చేసిన మార్గం పొడవుగా ఉన్నప్పటికీ చాలా సురక్షితమైనది.",
        "find_safe_route" to "సురక్షిత మార్గం కనుగొనండి",
        "safe_shelter" to "సురక్షిత పునరావాస కేంద్రం",
        "low_risk" to "ఎత్తైన సురక్షిత ప్రాంతం",
        "capacity_available" to "స్థలం అందుబాటులో ఉంది",
        "get_safe_route" to "సురక్షిత రూట్ పొందండి",
        
        "connected" to "నెట్‌వర్క్ కనెక్ట్ అయింది",
        "weak_connection" to "బలహీనమైన నెట్‌వర్క్",
        "no_network" to "మొబైల్ నెట్‌వర్క్ లేదు",
        "offline_mode_desc" to "ఎమర్జెన్సీ మోడ్ యాక్టివ్‌గా ఉంది. మీ GPS లొకేషన్ పనిచేస్తోంది. LoRa మెష్ ద్వారా సమాచారం చేరుతుంది.",
        "emergency_net_available" to "అత్యవసర నెట్‌వర్క్ అందుబాటులో ఉంది",
        "sos_waiting" to "SOS పంపడానికి సిద్ధంగా ఉంది",
        "sos_sent" to "SOS రెస్క్యూ టీమ్‌కి చేరింది",
        
        "report_flood" to "వరద ప్రమాదాన్ని నివేదించండి",
        "hazard_flooded_area" to "వరద ముంపు ప్రాంతం",
        "hazard_water_level" to "ఎక్కువ నీటి మట్టం",
        "hazard_road_blocked" to "రోడ్డు మూసివేయబడింది",
        "hazard_building_flooded" to "భవనంలోకి నీరు చేరింది",
        "hazard_other" to "ఇతర ప్రమాదం",
        "submit_report" to "రిపోర్ట్ సమర్పించండి",
        
        "what_to_do" to "వెంటనే ఏమి చేయాలి",
        "action_1" to "పల్లపు ప్రాంతాలు, కాలువల నుండి దూరంగా వెళ్ళండి.",
        "action_2" to "వరద నీరు ప్రవహించే రోడ్లపై ఎట్టి పరిస్థితుల్లో నడవకండి, డ్రైవ్ చేయకండి.",
        "action_3" to "సమీపంలోని సురక్షిత పునరావాస కేంద్రానికి లేదా ఎత్తైన ప్రదేశానికి వెళ్ళండి.",
        
        "cancel_sos" to "SOS రద్దు చేయండి",
        "update_location" to "లొకేషన్ అప్‌డేట్ చేయండి",
        "emergency_speed_dial" to "అత్యవసర హెల్ప్‌లైన్ నంబర్లు"
    )

    private val hindiStrings = mapOf(
        "app_title" to "फ्लडगार्ड AI",
        "where_am_i" to "मैं कहाँ हूँ?",
        "am_i_in_danger" to "क्या मैं खतरे में हूँ?",
        "where_should_i_go" to "मुझे कहाँ जाना चाहिए?",
        "how_do_i_get_help" to "मुझे मदद कैसे मिलेगी?",
        
        "you_are_safe" to "आप सुरक्षित हैं",
        "caution" to "सावधानी बरतें",
        "high_flood_risk" to "उच्च बाढ़ जोखिम",
        "extreme_flood_risk" to "अत्यधिक बाढ़ का खतरा",
        "move_to_safety" to "सुरक्षित स्थान पर जाएं",
        "rescue_active" to "बचाव अनुरोध सक्रिय है",
        
        "rainfall" to "वर्षा",
        "inundation_risk" to "जलभराव जोखिम",
        "expected_time" to "अनुमानित समय",
        "next_3_hours" to "अगले 3 घंटे में",
        "next_2_hours" to "अगले 2 घंटे में",
        
        "flood_map" to "बाढ़ का नक्शा",
        "safe_place" to "सुरक्षित स्थान",
        "sos" to "आपातकालीन SOS",
        "hold_for_sos" to "मदद के लिए दबाकर रखें",
        "hold_2_sec" to "लाइव GPS बचाव अनुरोध भेजने के लिए 2.5 सेकंड दबाएं",
        "holding" to "दबाए रखा गया है...",
        
        "recommended_route" to "अनुशंसित सुरक्षित मार्ग",
        "flooded_route" to "जलमग्न मार्ग (खतरनाक)",
        "route_safer_note" to "अनुशंसित मार्ग लंबा होने पर भी अधिक सुरक्षित है।",
        "find_safe_route" to "सुरक्षित मार्ग खोजें",
        "safe_shelter" to "सुरक्षित आश्रय स्थल",
        "low_risk" to "ऊँचा सुरक्षित स्थान",
        "capacity_available" to "क्षमता उपलब्ध है",
        "get_safe_route" to "सुरक्षित रास्ता देखें",
        
        "connected" to "नेटवर्क जुड़ा हुआ है",
        "weak_connection" to "कमजोर नेटवर्क",
        "no_network" to "मोबाइल नेटवर्क उपलब्ध नहीं है",
        "offline_mode_desc" to "आपातकालीन मोड सक्रिय है। आपका GPS सटीक स्थान ले रहा है। LoRa मेश के माध्यम से संदेश भेजा जा रहा है।",
        "emergency_net_available" to "आपातकालीन नेटवर्क उपलब्ध",
        "sos_waiting" to "SOS भेजने की प्रतीक्षा में",
        "sos_sent" to "SOS बचाव दल को भेजा गया",
        
        "report_flood" to "बाढ़ की सूचना दें",
        "hazard_flooded_area" to "जलभराव क्षेत्र",
        "hazard_water_level" to "उच्च जल स्तर",
        "hazard_road_blocked" to "सड़क बंद है",
        "hazard_building_flooded" to "इमारत में पानी भरा है",
        "hazard_other" to "अन्य खतरा",
        "submit_report" to "रिपोर्ट सबमिट करें",
        
        "what_to_do" to "तुरंत क्या करें",
        "action_1" to "निचले इलाकों और नदी तटों से तुरंत दूर जाएं।",
        "action_2" to "बहते बाढ़ के पानी में कभी भी वाहन न चलाएं और न ही चलें।",
        "action_3" to "निकटतम सुरक्षित आश्रय स्थल या ऊँचे स्थान की ओर जाएं।",
        
        "cancel_sos" to "SOS रद्द करें",
        "update_location" to "स्थान अपडेट करें",
        "emergency_speed_dial" to "आपातकालीन हेल्पलाइन नंबर"
    )
}
