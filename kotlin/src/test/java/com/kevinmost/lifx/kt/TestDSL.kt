package com.kevinmost.lifx.kt

import com.kevinmost.lifx.model.LifxColor
import com.kevinmost.lifx.model.Selector
import com.kevinmost.lifx.request.LifxRequests
import com.kevinmost.lifx.request.model.Operation
import org.junit.Test
import java.util.concurrent.TimeUnit

class TestDSL : BaseLifxTest() {
  @Test fun `test DSL makes sense`() {
    LifxRequests.setLights().plus(
        Operation.forSelector(Selector.forGroup("Office"))
            .duration(TimeUnit.SECONDS(30))
            .color(LifxColor.rgb(255, 0, 0))
            .brightness(0.3)
            .build(),
        Operation.forSelector(Selector.forGroup("Bedroom"))
            .duration(TimeUnit.SECONDS(5))
            .color(LifxColor.white(4000))
            .brightness(1.0)
            .build()
    ).execute().unwrap()
  }
}