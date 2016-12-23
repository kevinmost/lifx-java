package com.kevinmost.lifx;

import com.kevinmost.internal.Util;
import com.kevinmost.lifx.model.LifxColor;
import com.kevinmost.lifx.model.Light;
import com.kevinmost.lifx.request.LifxRequests;
import org.junit.Test;

import java.util.List;

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

  /**
   * Tests that the .toHSV() function on RGB colors is accurate for many RGB colors
   */
  @Test public void testRGBToHSVFidelity() {
    int max = 255;
    int increments = 30;
    for (int r = 0; r < max; r += increments) {
      for (int g = 0; g < max; g += increments) {
        for (int b = 0; b < max; b += increments) {
          logger.info("Testing RGB of {},{},{}", r, g, b);
          final LifxColor calculatedHSV = LifxColor.rgb(r, g, b);
          final LifxColor returnedHSV =
              LifxRequests.verifyColor(String.format("rgb:%d,%d,%d", r, g, b)).execute().unwrap();
          logger.info("Server-calculated HSV: {}\n Our calculated HSV: {}", returnedHSV, calculatedHSV);
          assertFuzzyEquals(returnedHSV.hue(), calculatedHSV.hue());
          assertFuzzyEquals(returnedHSV.saturation(), calculatedHSV.saturation());
          assertFuzzyEquals(returnedHSV.brightness(), calculatedHSV.brightness());
          Util.sleep(1000); // rate limits are 60/min
        }
      }
    }
  }
}
