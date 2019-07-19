package eu.eyan.qubit

import org.junit.Test

import eu.eyan.util.string.StringPlus.StringPlusImplicit

/**
 * https://qubit.hu/2018/07/09/esz-ventura-meg-tudod-csinalni-ugyanezt-osszeadas-helyett-kivonassal
 */

class TortOsszeg19 {

  @Test
  def tortOsszegPluszMinusz(): Unit = {
    List(2, 3, 4, 5, 6, 7, 8, 9).permutations.foreach(numbers => {
      def n(tiz: Int, egy: Int) = numbers(tiz - 2) * 10 + numbers(egy - 2)

      if (n(2, 3) * n(8, 9) - n(6, 7) * n(4, 5) == n(4, 5) * n(8, 9)) println(s"1 = ${n(2, 3)} / ${n(4, 5)} - ${n(6, 7)} / ${n(8, 9)}")

      if (n(2, 3) * n(8, 9) + n(6, 7) * n(4, 5) == n(4, 5) * n(8, 9)) println(s"1 = ${n(2, 3)} / ${n(4, 5)} + ${n(6, 7)} / ${n(8, 9)}")
    })
    //   1 = 29 / 87 + 36 / 54
    //   1 = 36 / 54 + 29 / 87
    //   1 = 47 / 29 - 36 / 58
    //   1 = 48 / 23 - 75 / 69
    //   1 = 57 / 38 - 46 / 92
    //   1 = 59 / 38 - 42 / 76
    //   1 = 65 / 39 - 48 / 72
    //   1 = 87 / 39 - 64 / 52
    //   1 = 94 / 52 - 63 / 78
  }

  /** https://qubit.hu/2018/07/17/esz-ventura-letezik-ilyen-tort */
  @Test
  def kimaradoSzamjegy(): Unit = {
    List(1, 2, 3, 4, 5, 6, 7, 8, 9).permutations.foreach(numbers => {
      def n1(egy: Int): Int = numbers(egy - 1)
      def n(ezer: Int, szaz: Int, tiz: Int, egy: Int): Int = numbers(ezer - 1) * 1000 + numbers(szaz - 1) * 100 + numbers(tiz - 1) * 10 + n1(egy)

      if (n(1, 2, 3, 4) == n1(5) * n(6, 7, 8, 9)) println(s"${n(1, 2, 3, 4)} / ${n(6, 7, 8, 9)} = ${n1(5)}")

    })
    //   6952 / 1738 = 4
    //   7852 / 1963 = 4
  }
}