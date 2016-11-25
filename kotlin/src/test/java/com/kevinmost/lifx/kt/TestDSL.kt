package com.kevinmost.lifx.kt

import com.kevinmost.lifx.model.Selector
import com.kevinmost.lifx.request.LifxRequests
import com.kevinmost.lifx.request.model.Operation
import org.junit.Test
import java.util.concurrent.TimeUnit

class TestDSL : BaseLifxTest() {
  @Test fun `test DSL makes sense`() {
    LifxRequests.setLights().plus(
        Operation.forSelector(Selector.forGroupLabel("Office"))
            .duration(TimeUnit.SECONDS(30))
            .color(lifxColorRGB(255, 0, 0))
            .brightness(0.3)
            .build(),
        Operation.forSelector(Selector.forGroupLabel("Bedroom"))
            .duration(TimeUnit.SECONDS(5))
            .color(lifxColorWhite(kelvin = 4000))
            .brightness(1.0)
            .build()
    ).execute().unwrap()
  }
}