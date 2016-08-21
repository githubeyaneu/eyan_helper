package eu.eyan.util.collection

import org.junit.Test
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.fest.assertions.Assertions._
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.junit.JUnitSuite
import eu.eyan.testutil.ScalaEclipseJunitRunner

@RunWith(classOf[ScalaEclipseJunitRunner])
class MapsPlusTestS extends JUnitSuite {

  @Test def empty = assertThat(MapsPlus.newMap()).hasSize(0)

  @Test def one = assertThat(MapsPlus.newMap("one", "1")).hasSize(1)

  @Test def one_is_1 = assertThat(MapsPlus.newMap[String, String]("one", "1").get("one")).isEqualTo("1")

  @Test def two_is_2 = assertThat(MapsPlus.newMap[String, String]("one", "1", "2", "two").get("2")).isEqualTo("two")

  @Test def two = assertThat(MapsPlus.newMap[String, String]("one", "1", "2", "two")).hasSize(2)

  @Test def newMaxSizeHashMap = assertThat(MapsPlus.newMaxSizeHashMap[String, String](2)).isNotNull()

  @Test def newMaxSizeHashMapSize = {
    val map = MapsPlus.newMaxSizeHashMap[String, String](2)
    map.put("", "")
    map.put("1", "")
    map.put("2", "")
    assertThat(map).hasSize(2)
  }

}