package com.kevinmost.lifx.kt

import com.kevinmost.lifx.model.LifxColor

fun lifxColorRGB(r: Int, g: Int, b: Int) = LifxColor.createRGB(r, g, b)
fun lifxColorHSV(hue: Int, saturation: Double, brightness: Double) = LifxColor.createHSV(hue, saturation, brightness)
fun lifxColorWhite(kelvin: Int) = LifxColor.createWhite(kelvin)
