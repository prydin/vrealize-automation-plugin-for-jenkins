/*
 * Copyright (c) 2020 VMware, Inc
 *
 *  SPDX-License-Identifier: MIT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.vmware.vra.jenkinsplugin.util;

import static com.vmware.vra.jenkinsplugin.util.MapUtils.mapOf;
import static com.vmware.vra.jenkinsplugin.util.MapUtils.mappify;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class MapUtilsTest {
  @Test
  public void testMappifyBasic() {
    final TestCase1 o = new TestCase1();
    final Map<Object, Object> map = (Map<Object, Object>) mappify(o);
    assertEquals("A String", map.get("someString"));
    assertEquals(42, map.get("someInt"));
    assertEquals(4242L, map.get("someLong"));
    assertEquals(42.42, map.get("someDouble"));
    assertEquals(true, map.get("someTrue"));
    assertEquals(false, map.get("someFalse"));
    assertEquals("FOO", map.get("someEnum"));
    assertEquals(HashSet.class, map.get("someSet").getClass());
    assertEquals(HashMap.class, map.get("simpleMap").getClass());
    assertEquals(HashMap.class, map.get("mapOfMaps").getClass());
  }

  private static enum SomeEnum {
    FOO,
    BAR,
  }

  private static class TestCase1 {
    private final Map<String, String> simpleMap = mapOf("Foo", "Bar");
    private final Map<String, Map<String, String>> mapOfMaps = mapOf("Foo", mapOf("Bar", "Baz"));
    private final Map<String, String[]> mapOfArrays = mapOf("Foo", new String[] {"Bar", "Baz"});
    private final boolean someTrue = true;
    private final boolean someFalse = false;
    private final int someInt = 42;
    private final long someLong = 4242;
    private final String someString = "A String";
    private final double someDouble = 42.42;
    private final SomeEnum someEnum = SomeEnum.FOO;
    private final Set<String> someSet = new HashSet();

    public TestCase1() {
      someSet.add("foo");
      someSet.add("bar");
    }

    public Map<String, String> getSimpleMap() {
      return simpleMap;
    }

    public Map<String, Map<String, String>> getMapOfMaps() {
      return mapOfMaps;
    }

    public Map<String, String[]> getMapOfArrays() {
      return mapOfArrays;
    }

    public boolean isSomeTrue() {
      return someTrue;
    }

    public boolean isSomeFalse() {
      return someFalse;
    }

    public int getSomeInt() {
      return someInt;
    }

    public long getSomeLong() {
      return someLong;
    }

    public String getSomeString() {
      return someString;
    }

    public double getSomeDouble() {
      return someDouble;
    }

    public SomeEnum getSomeEnum() {
      return someEnum;
    }

    public Set<String> getSomeSet() {
      return someSet;
    }
  }
}
