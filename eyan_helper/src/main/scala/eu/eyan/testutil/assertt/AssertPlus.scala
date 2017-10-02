package eu.eyan.testutil.assertt

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert._
import org.hamcrest.CoreMatchers._
import org.hamcrest.CoreMatchers
import org.junit.Assert

object AssertPlus {
  implicit class AssertIterableImplicit[T <: Iterable[_]](obj: T) {
    def shouldBe(m: Matcher[Iterable[_]]) = { assertThat(obj, m) }

    def shouldHaveSize(size: Int) = {
      assertThat(obj, new BaseMatcher[Iterable[_]] {
        def matches(set: Any): Boolean = set.asInstanceOf[Iterable[_]].size == size
        def describeTo(d: Description): Unit = d.appendText("size = " + size)
      })
    }

    def shouldBeEmpty = {
      assertThat(obj, new BaseMatcher[Iterable[_]] {
        def matches(set: Any): Boolean = set.asInstanceOf[Iterable[_]].size == 0
        def describeTo(d: Description): Unit = d.appendText("empty")
      })
    }

    def shouldContain(that: Any) = {
      assertThat(obj, new BaseMatcher[Iterable[_]] {
        def matches(set: Any): Boolean = set.asInstanceOf[Iterable[_]].exists(f => f == that)
        def describeTo(d: Description): Unit = d.appendText("contains " + that)
      })
    }

    def shouldNotContain(that: Any) = {
      assertThat(obj, not(new BaseMatcher[Iterable[_]] {
        def matches(set: Any): Boolean = set.asInstanceOf[Iterable[_]].exists(f => f == that)
        def describeTo(d: Description): Unit = d.appendText("contains " + that)
      }))
    }
  }

  implicit class AssertAnyImplicit(obj: Any) {
    def shouldBeEqualTo(expected: Any) = { assertThat(obj, equalTo(expected)) }
    def shouldNotBeEqualTo(expected: Any) = { assertThat(obj, not(equalTo(expected))) }
    def shouldBeSameInstanceTo(expected: Any) = { assertThat(obj, sameInstance(expected)) }
    def shouldNotBeSameInstanceTo(expected: Any) = { assertThat(obj, not(sameInstance(expected))) }
  }

  implicit class AssertBooleanImplicit(obj: Boolean) {
    def shouldBeTrue = { assertThat(obj, equalTo(true)) }
    def shouldBeFalse = { assertThat(obj, equalTo(false)) }
  }

  def expectException(klazz: Class[_], exp: => Unit) = {
    val success = try {
      exp
      true
    } catch {
      case e: Throwable => {
        if (e.getClass != klazz) Assert.fail(s"Exception type $e.getClass wrong. Expected: $klazz")
        false
      }
    }

    if (success) Assert.fail(s"Expected exception $klazz was not thrown.")
  }

  def expectNoException(exp: => Unit) = {
    try {
      exp
    } catch {
      case e: Throwable => Assert.fail(s"Exception happened $e.getClass.")
    }
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