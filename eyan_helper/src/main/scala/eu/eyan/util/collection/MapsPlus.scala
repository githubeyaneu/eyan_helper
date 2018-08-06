package eu.eyan.util.collection

import scala.collection.mutable.MutableList
import scala.collection.mutable.ListBuffer

object MapsPlus {
  def maxSizeMutableMap[K, V](maxSize: Int) = new scala.collection.mutable.Map[K, V] {
    private val map = scala.collection.mutable.Map[K, V]()
    private val keysOfMap = ListBuffer[K]()

    def +=(kv: (K, V)) = map.synchronized {
      keysOfMap += kv._1
      if (keysOfMap.size > maxSize) {
        val keyToRemove = keysOfMap.head
        keysOfMap -= keyToRemove
        map -= keyToRemove
      }
      map.+=(kv)
      this
    }

    def get(key: K): Option[V] = map.synchronized { map.get(key) }

    def iterator: Iterator[(K, V)] = map.synchronized { map.iterator }

    def -=(key: K) = map.synchronized {
      keysOfMap -= key
      map -= key
      this
    }
  }

  def maxSizeImmutableMap[K, V](
    maxSize: Int, myMap: Map[K, V] = Map[K, V](), keysOfMap: List[K] = List[K]()): Map[K, V] = new Map[K, V] {

    def +[V1 >: V](kv: (K, V1)): scala.collection.immutable.Map[K, V1] = {
      val newKeys = if (keysOfMap.size == maxSize) keysOfMap.tail :+ kv._1
      else keysOfMap :+ kv._1

      val newMap = if (keysOfMap.size == maxSize) myMap - keysOfMap.head + kv
      else myMap + kv

      maxSizeImmutableMap(maxSize, newMap, newKeys)
    }

    def -(key: K): scala.collection.immutable.Map[K, V] =
      maxSizeImmutableMap(maxSize, myMap - key, keysOfMap.filter(_ == key))

    def get(key: K): Option[V] = myMap.get(key)

    def iterator: Iterator[(K, V)] = myMap.iterator
  }
}