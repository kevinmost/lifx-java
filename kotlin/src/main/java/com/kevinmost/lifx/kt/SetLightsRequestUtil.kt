package com.kevinmost.lifx.kt

import com.kevinmost.lifx.model.LifxColor
import com.kevinmost.lifx.model.LifxEntity
import com.kevinmost.lifx.model.PowerState
import com.kevinmost.lifx.model.Selector
import com.kevinmost.lifx.request.SetLightsRequest
import com.kevinmost.lifx.request.model.Operation
import java.util.concurrent.TimeUnit

operator fun SetLightsRequest.plus(operation: Operation): SetLightsRequest {
  return plus(operation)
}

operator fun SetLightsRequest.plus(operations: Collection<Operation>): SetLightsRequest {
  return plus(operations)
}

fun Selector.lifxOperation(
    powerState: PowerState? = null,
    color: LifxColor? = null,
    brightness: Double? = null,
    duration: Duration? = null,
    infraredBrightness: Double? = null
): Operation = Operation.forSelector(this)
    .apply {
      powerState?.let { setPowerState(it) }
      color?.let { setColor(it) }
      brightness?.let { setBrightness(it) }
      duration?.let { setDuration(it.value, it.unit) }
      infraredBrightness?.let { setInfraredBrightness(it) }
    }

fun LifxEntity.lifxOperation(
    powerState: PowerState? = null,
    color: LifxColor? = null,
    brightness: Double? = null,
    duration: Duration? = null,
    infraredBrightness: Double? = null
): Operation = selector().lifxOperation(powerState, color, brightness, duration, infraredBrightness)


data class Duration(val value: Long, val unit: TimeUnit) {
  constructor(value: Int, unit: TimeUnit) : this(value.toLong(), unit)

  fun convertTo(unit: TimeUnit) = Duration(unit.convert(this.value, this.unit), unit)

  operator fun TimeUnit.invoke(value: Long): Duration = Duration(value, this)
  operator fun TimeUnit.invoke(value: Int): Duration = Duration(value, this)
}

