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

import com.google.gson.internal.bind.util.ISO8601Utils;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MapUtils {
  public static Object mappify(final Object o) {
    if (o == null) {
      return null;
    }
    final Class<?> clazz = o.getClass();
    if (clazz.isPrimitive()
        || o instanceof Number
        || o instanceof CharSequence
        || o instanceof Character
        || o instanceof Boolean) {
      return o;
    }
    if (clazz.isEnum()) {
      return ((Enum) o).name();
    }
    if (o instanceof UUID) {
      return o.toString();
    }
    if (o instanceof Date) {
      return ISO8601Utils.format((Date) o, true);
    }
    if (clazz.isArray()) {
      return mappifyArray(o);
    } else if (o instanceof Map) {
      return mappifyMap(o);
    } else if (o instanceof Set) {
      return mappifySet(o);
    } else if (o instanceof Collection) {
      return mappifyCollection(o);
    } else {
      return mappifyBean(o);
    }
  }

  private static Object mappifyArray(final Object o) {
    final int l = Array.getLength(o);
    final List<Object> result = new ArrayList<>(l);
    for (int i = 0; i < l; ++i) {
      result.add(mappify(Array.get(o, i)));
    }
    return result;
  }

  private static Object mappifyMap(final Object o) {
    final Map<Object, Object> map = (Map<Object, Object>) o;
    final Map<Object, Object> result = new HashMap<>(map.size());
    for (final Map.Entry<Object, Object> e : map.entrySet()) {
      result.put(e.getKey(), mappify(e.getValue()));
    }
    return result;
  }

  private static Object mappifyCollection(final Object o) {
    final Collection<Object> coll = (Collection<Object>) o;
    final Collection<Object> result = new ArrayList<>(coll.size());
    for (final Object item : coll) {
      result.add(mappify(item));
    }
    return result;
  }

  private static Object mappifySet(final Object o) {
    final Set<Object> set = (Set<Object>) o;
    final Set<Object> result = new HashSet<>(set.size());
    for (final Object item : set) {
      result.add(mappify(item));
    }
    return result;
  }

  private static Object mappifyBean(final Object o) {
    try {
      final Map<String, Object> map = new HashMap<>();
      final Class clazz = o.getClass();
      for (final Method m : clazz.getMethods()) {
        final String name = m.getName();
        final boolean isGet = name.startsWith("get");
        if (!(isGet || name.startsWith("is"))
            || name.equals("getClass")
            || m.getParameterCount() != 0
            || !Modifier.isPublic(m.getModifiers())) {
          continue;
        }
        final String key =
            isGet
                ? Character.toLowerCase(name.charAt(3)) + name.substring(4) // get...
                : Character.toLowerCase(name.charAt(2)) + name.substring(3); // is...
        final Object value = mappify(m.invoke(o));
        map.put(key, mappify(value));
      }
      return map;
    } catch (final IllegalArgumentException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new RuntimeException(
          "Unexpected error during bean mappification. This should not happen!", e);
    }
  }

  public static <K, V> Map<K, V> mapOf(final K k1, final V v1) throws IllegalArgumentException {
    final Map<K, V> map = new HashMap<>();
    map.put(k1, v1);
    return map;
  }

  public static <K, V> Map<K, V> mapOf(final K k1, final V v1, final K k2, final V v2)
      throws IllegalArgumentException {
    final Map<K, V> map = new HashMap<>();
    map.put(k1, v1);
    map.put(k2, v2);
    return map;
  }
}
