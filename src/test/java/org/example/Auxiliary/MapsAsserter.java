package org.example.Auxiliary;

import org.junit.jupiter.api.Assertions;

import java.util.Map;

public class MapsAsserter {
  public static <T1, T2> boolean compareEqual(Map<T1, T2> a,  Map<T1, T2> b) {
    if (a.size() != b.size())
      return false;

    for (var key : a.keySet())
      if (!b.containsKey(key) || !a.get(key).equals(b.get(key)))
        return false;

    return true;
  }

  public static <T1, T2> void assertEqual(Map<T1, T2> expected, Map<T1, T2> actual) {
    Assertions.assertTrue(compareEqual(expected, actual));
  }
}
