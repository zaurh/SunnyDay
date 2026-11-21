package com.zaurh.sunnyday.domain.model

/** App-level weather condition, derived from WeatherAPI's stable numeric condition codes. */
enum class WeatherCondition {
    CLEAR,
    PARTLY_CLOUDY,
    CLOUDY,
    OVERCAST,
    MIST,
    FOG,
    PATCHY_RAIN,
    RAIN,
    PATCHY_SNOW,
    SNOW,
    PATCHY_SLEET,
    SLEET,
    THUNDER,
    PATCHY_RAIN_THUNDER,
    RAIN_THUNDER,
    PATCHY_SNOW_THUNDER,
    SNOW_THUNDER;

    companion object {
        /** See https://www.weatherapi.com/docs/weather_conditions.json */
        fun fromCode(code: Int): WeatherCondition = when (code) {
            1000 -> CLEAR
            1003 -> PARTLY_CLOUDY
            1006 -> CLOUDY
            1009 -> OVERCAST
            1030 -> MIST
            1135, 1147 -> FOG
            1063, 1180, 1186, 1192, 1240, 1243 -> PATCHY_RAIN
            1150, 1153, 1183, 1189, 1195, 1246 -> RAIN
            1066, 1210, 1216, 1222, 1255 -> PATCHY_SNOW
            1114, 1117, 1213, 1219, 1225, 1258 -> SNOW
            1069, 1249, 1252 -> PATCHY_SLEET
            1072, 1168, 1171, 1198, 1201, 1204, 1207, 1237, 1261, 1264 -> SLEET
            1087 -> THUNDER
            1273 -> PATCHY_RAIN_THUNDER
            1276 -> RAIN_THUNDER
            1279 -> PATCHY_SNOW_THUNDER
            1282 -> SNOW_THUNDER
            else -> CLOUDY
        }
    }
}
