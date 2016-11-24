package com.kevinmost.lifx.kt

import com.kevinmost.lifx.model.LifxColor
import com.kevinmost.lifx.model.LifxEntity
import com.kevinmost.lifx.model.PowerState
import com.kevinmost.lifx.model.Selector
import com.kevinmost.lifx.request.LifxRequests
import com.kevinmost.lifx.request.SetLightsRequest
import com.kevinmost.lifx.request.model.Operation
import java.util.concurrent.TimeUnit

inline fun lifxSetLightsRequest(builder: SetLightsRequestBuilder.() -> Unit): SetLightsRequest {
  return SetLightsRequestBuilder().apply(builder).__inner
}

class SetLightsRequestBuilder {
  val __inner: SetLightsRequest = LifxRequests.setLights()

  inline fun set(selector: Selector, builder: OperationBuilder.() -> Unit) {
    __inner.plus(OperationBuilder().apply(builder).buildFor(selector))
  }

  inline fun set(entity: LifxEntity, builder: OperationBuilder.() -> Unit) {
    set(entity.selector(), builder)
  }

}

class OperationBuilder {

  var powerState: PowerState? = null
  var color: LifxColor? = null
  var brightness: Double? = null
  var duration: Duration? = null
  var infraredBrightness: Double? = null

  fun buildFor(selector: Selector) = Operation.forSelector(selector)
      .powerState(powerState)
      .color(color)
      .brightness(brightness)
      .apply { duration?.let { duration(it.value, it.unit) } }
      .infraredBrightness(infraredBrightness)
      .build()
}


data class Duration(val value: Long, val unit: TimeUnit) {
  constructor(value: Int, unit: TimeUnit) : this(value.toLong(), unit)

  fun convertTo(unit: TimeUnit) = Duration(unit.convert(this.value, this.unit), unit)
}

operator fun TimeUnit.invoke(value: Long): Duration = Duration(value, this)
operator fun TimeUnit.invoke(value: Int): Duration = Duration(value, this)

