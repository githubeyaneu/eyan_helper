package eu.eyan.util.collection

import org.fest.assertions.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitSuite

import eu.eyan.testutil.ScalaEclipseJunitRunner

@RunWith(classOf[ScalaEclipseJunitRunner])
class MapsPlusTest extends JUnitSuite {

//  @Test def empty = assertThat(MapsPlus.newMap()).hasSize(0)
//
//  @Test def one = assertThat(MapsPlus.newMap("one", "1")).hasSize(1)
//
//  @Test def one_is_1 = assertThat(MapsPlus.newMap[String, String]("one", "1").get("one")).isEqualTo("1")
//
//  @Test def two_is_2 = assertThat(MapsPlus.newMap[String, String]("one", "1", "2", "two").get("2")).isEqualTo("two")
//
//  @Test def two = assertThat(MapsPlus.newMap[String, String]("one", "1", "2", "two")).hasSize(2)

  @Test def newMaxSizeHashMapSize = {
    val map = MapsPlus.newMaxSizeHashMap[String, String](2)

    map.put("", "0")
    assertThat(map.get("")).isEqualTo("0")
    assertThat(map.get("3")).isNull
    
    map.put("1", "egy")
    assertThat(map.get("")).isEqualTo("0")
    assertThat(map.get("1")).isEqualTo("egy")
    
    map.put("2", null)
    assertThat(map).hasSize(2)
    assertThat(map.get("")).isNull  // was removed because of max size
    assertThat(map.get("1")).isEqualTo("egy")
    assertThat(map.get("2")).isNull
  }
}