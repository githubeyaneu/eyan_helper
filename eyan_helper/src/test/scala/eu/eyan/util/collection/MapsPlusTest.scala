package eu.eyan.util.collection

import org.fest.assertions.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
//import org.scalatest.junit.JUnitSuite

import eu.eyan.testutil.ScalaEclipseJunitRunner
import eu.eyan.testutil.TestPlus

@RunWith(classOf[ScalaEclipseJunitRunner])
class MapsPlusTest extends TestPlus {

  //  @Test def empty = assertThat(MapsPlus.newMap()).hasSize(0)
  //
  //  @Test def one = assertThat(MapsPlus.newMap("one", "1")).hasSize(1)
  //
  //  @Test def one_is_1 = assertThat(MapsPlus.newMap[String, String]("one", "1").get("one")).isEqualTo("1")
  //
  //  @Test def two_is_2 = assertThat(MapsPlus.newMap[String, String]("one", "1", "2", "two").get("2")).isEqualTo("two")
  //
  //  @Test def two = assertThat(MapsPlus.newMap[String, String]("one", "1", "2", "two")).hasSize(2)

//  @Test def maxSize_Java_HashMap = {
//    val map = MapsPlus.newMaxSizeHashMap[String, String](2)
//
//    map.put("", "0")
//    map.get("") ==> "0"
//    map.get("3") shouldBeNull
//
//    map.put("1", "egy")
//    assertThat(map.get("")).isEqualTo("0")
//    assertThat(map.get("1")).isEqualTo("egy")
//
//    map.put("2", null)
//    map.size ==> 2
//    // was removed because of max size
//    map.get("") shouldBeNull
//
//    map.get("1") ==> "egy"
//    map.get("2") shouldBeNull
//  }

  @Test def maxSize_MutableMap = {
    val map = MapsPlus.maxSizeMutableMap[String, String](2)
    map.size ==> 0

    map.put("", "0")
    map.size ==> 1
    map.get("") ==> Some("0")
    map.get("3") ==> None

    map.put("1", "egy")
    map.size ==> 2
    map.get("") ==> Some("0")
    map.get("1") ==> Some("egy")

    map.put("2", null)
    map.size ==> 2
    map.get("") ==> None
    map.get("1") ==> Some("egy")
    map.get("2") ==> Some(null)

    map.remove("2")
    map.size ==> 1
    map.get("") ==> None
    map.get("1") ==> Some("egy")
    map.get("2") ==> None

    map.put("3", "harom")
    map.size ==> 2
    map.get("") ==> None
    map.get("1") ==> Some("egy")
    map.get("2") ==> None
    map.get("3") ==> Some("harom")
  }

  @Test def maxSize_ImmutableMap = {
    var map = MapsPlus.maxSizeImmutableMap[String, String](2)
    map.size ==> 0

    map = map + ("" -> "0")
    map.size ==> 1
    map.get("") ==> Some("0")
    map.get("3") ==> None

    map = map + ("1"-> "egy")
    map.size ==> 2
    map.get("") ==> Some("0")
    map.get("1") ==> Some("egy")

    map = map + ("2" -> null)
    map.size ==> 2
    map.get("") ==> None
    map.get("1") ==> Some("egy")
    map.get("2") ==> Some(null)

    map = map - "2"
    map.size ==> 1
    map.get("") ==> None
    map.get("1") ==> Some("egy")
    map.get("2") ==> None

    map = map + ("3" -> "harom")
    map.size ==> 2
    map.get("") ==> None
    map.get("1") ==> Some("egy")
    map.get("2") ==> None
    map.get("3") ==> Some("harom")
  }
}