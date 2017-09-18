package eu.eyan.testutil.assertt

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert._
import org.hamcrest.CoreMatchers._
import org.hamcrest.CoreMatchers

object AssertPlus {
  implicit class AssertIterableImplicit[T <: Iterable[_]](obj: T) {
    def shouldBe(m: Matcher[Iterable[_]]) = { assertThat(obj, m) }
    
    def shouldHaveSize(size: Int) = {
      assertThat(obj, new BaseMatcher[Iterable[_]] {
        def matches(set: Any): Boolean = set.asInstanceOf[Iterable[_]].size == size
        def describeTo(d: Description): Unit = d.appendText("size = " + size)
      })
    }
    
    def shouldContain(that: Any) = {
      assertThat(obj, new BaseMatcher[Iterable[_]] {
        def matches(set: Any): Boolean = set.asInstanceOf[Iterable[_]].exists(f=> f == that)
        def describeTo(d: Description): Unit = d.appendText("contains " + that)
      })
    }
  }

  implicit class AssertAnyImplicit(obj: Any) {
    def sholdBeEqualTo(that: Any) = { assertThat(obj, equalTo(that)) }
    def sholdBeNotEqualTo(that: Any) = { assertThat(obj, not(equalTo(that))) }
  }

  implicit class AssertBooleanImplicit(obj: Boolean) {
	  def shouldBeTrue = { assertThat(obj, equalTo(true)) }
	  def shouldBeFalse = { assertThat(obj, equalTo(false)) }
  }

  def empty: Matcher[Iterable[_]] = new BaseMatcher[Iterable[_]] {
    def matches(set: Any): Boolean = set.asInstanceOf[Iterable[_]].isEmpty
    def describeTo(d: Description): Unit = d.appendText("Empty set")
  }

  def nonEmpty: Matcher[Iterable[_]] = new BaseMatcher[Iterable[_]] {
    def matches(set: Any): Boolean = set.asInstanceOf[Iterable[_]].nonEmpty
    def describeTo(d: Description): Unit = d.appendText("Non empty set")
  }
}