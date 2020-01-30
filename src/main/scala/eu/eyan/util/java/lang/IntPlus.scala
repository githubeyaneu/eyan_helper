package eu.eyan.util.java.lang

object IntPlus {
  implicit class IntImplicit(int: Int) {
	  private def sieve(s: Stream[Int]): Stream[Int] = {
		  //println("s "+s.head)
	    s.head #:: sieve(s.tail filter (_ % s.head != 0))
	  }
    def primes = {
  		//println("p "+int)
      if(int<2) Stream.Empty else if(int==2) Stream(2) else sieve(Stream.range(2, int+1))
    }

    def primeFactors: Stream[Int] = {
      //println("pf "+int)
      def primeFactors(int: Int, primes: Stream[Int]): Stream[Int] = {
        if (int == 1 || primes.isEmpty) Stream.Empty
        else if (int % primes.head == 0) Stream.cons(primes.head, primeFactors(int / primes.head, primes))
        else primeFactors(int, primes.tail)
      }
      
      if(int<2) Stream.Empty else primeFactors(int, int.primes)
    }
  }
}