package com.kevinmost.lifx.kt

import com.kevinmost.lifx.request.model.Operation
import java.util.concurrent.TimeUnit

fun Operation.Builder.duration(duration: Duration): Operation.Builder = duration(duration.value, duration.unit)

data class Duration(val value: Long, val unit: TimeUnit) {
  constructor(value: Int, unit: TimeUnit) : this(value.toLong(), unit)

  fun convertTo(unit: TimeUnit) = Duration(unit.convert(this.value, this.unit), unit)
}

operator fun TimeUnit.invoke(value: Long): Duration = Duration(value, this)
operator fun TimeUnit.invoke(value: Int): Duration = Duration(value, this)

