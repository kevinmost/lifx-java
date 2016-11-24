package com.kevinmost.lifx;

import com.kevinmost.internal.Util;
import com.kevinmost.lifx.model.LifxColor;
import com.kevinmost.lifx.model.Light;
import com.kevinmost.lifx.request.LifxRequests;
import com.kevinmost.lifx.request.model.Operation;
import com.kevinmost.lifx.request.model.OperationResult;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static com.kevinmost.lifx.model.LifxColor.White.KELVIN_MAX;
import static com.kevinmost.lifx.model.LifxColor.White.KELVIN_MIN;
import static org.junit.Assert.assertEquals;

public class TestRequests extends BaseLifxTest {

  @Test public void testGetAllLights() {
    final List<Light> lights = LifxRequests.listLights().execute().unwrap();
    for (int i = 0; i < lights.size(); i++) {
      logger.info("Light # {}: {}", i, lights.get(i));
    }
  }

  // Lights should ignore time-since-last-seen fields when comparing for equality since those can never
  // be equal across network requests
  @Test public void testLightEquality() {
    final Light firstLight = LifxRequests.listLights().execute().unwrap().get(0);
    Util.sleep(2000); // makes sure the timestamps are different by at least a second
    final Light sameLight = LifxRequests.listLights(firstLight).execute().unwrap().get(0);
    assertEquals(firstLight, sameLight);
  }

  @Ignore // we know it works
  @Test public void testKelvinToHSVFidelity() {
    for (int i = KELVIN_MIN; i < KELVIN_MAX; i += 500) {
      logger.info("Testing Kelvin of {}", i);
      final LifxColor.White kelvin = LifxColor.createKelvin(i);
      final LifxColor.HSV calculatedHSV = kelvin.toHSV();
      final LifxColor.HSV returnedHSV = LifxRequests.verifyColor(kelvin).execute().unwrap().toHSV();
      logger.info("Server-calculated HSV: {}\n Our calculated HSV: {}", returnedHSV, calculatedHSV);
      assertEquals(returnedHSV, calculatedHSV);
      Util.sleep(1000); // rate limits are 60/min
    }
  }

  /**
   * Tests that the .toHSV() function on RGB colors is accurate for many RGB colors
   */
  @Ignore // this takes so long, we know it works now
  @Test public void testRGBToHSVFidelity() {
    int max = 255;
    int increments = 30;
    for (int r = 0; r < max; r += increments) {
      for (int g = 0; g < max; g += increments) {
        for (int b = 0; b < max; b += increments) {
          logger.info("Testing RGB of {},{},{}", r, g, b);
          final LifxColor.RGB rgb = LifxColor.createRGB(r, g, b);
          final LifxColor.HSV calculatedHSV = rgb.toHSV();
          final LifxColor returnedHSV = LifxRequests.verifyColor(rgb).execute().unwrap();
          logger.info("Server-calculated HSV: {}\n Our calculated HSV: {}", returnedHSV, calculatedHSV);
          assertEquals(returnedHSV, calculatedHSV);
          Util.sleep(1000); // rate limits are 60/min
        }
      }
    }
  }

  @Test public void testMakeLightsRed() {
    final Light firstLight = LifxRequests.listLights().execute().unwrap().get(0);
    final LifxColor red = LifxColor.createRGB(255, 0, 0);

    final List<OperationResult> result = LifxRequests.setLights().plus(
        Operation.forEntity(firstLight).setColor(red)
    )
        .execute().unwrap();
  }
}
