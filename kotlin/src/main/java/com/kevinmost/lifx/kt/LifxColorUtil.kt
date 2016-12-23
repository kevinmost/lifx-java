package com.kevinmost.lifx.kt

import com.kevinmost.lifx.model.LifxColor

fun lifxColor(
    hue: Double? = null,
    saturation: Double? = null,
    brightness: Double? = null,
    kelvin: Int? = null
): LifxColor = LifxColor.create()
    .withHue(hue)
    .withSaturation(saturation)
    .withBrightness(brightness)
    .withKelvin(kelvin)

fun lifxColor(
    r: Int,
    g: Int,
    b: Int
): LifxColor = LifxColor.rgb(r, g, b)
