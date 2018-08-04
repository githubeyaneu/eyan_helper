package eu.eyan.util.collection

import scala.collection.mutable.MutableList
import scala.collection.mutable.ListBuffer

object MapsPlus {
  // FIXME delete java
  def newMaxSizeHashMap[K, V](maxSize: Int): java.util.Map[K, V] = new java.util.LinkedHashMap[K, V](maxSize) {
    override def removeEldestEntry(eldest: java.util.Map.Entry[K, V]) = size() > maxSize
  }

  def newMaxSizeMutableMap[K, V](maxSize: Int): scala.collection.mutable.Map[K, V] = new scala.collection.mutable.Map[K, V] {
    var map = scala.collection.mutable.Map[K, V]()
    var keysOfMap = ListBuffer[K]()

    def +=(kv: (K, V)) = {
      keysOfMap += kv._1
      if (keysOfMap.size > maxSize) {
        val keyToRemove = keysOfMap.head
        keysOfMap -= keyToRemove
        map -= keyToRemove
      }
      map.+=(kv)
      this
    }

    def get(key: K): Option[V] = map.get(key)

    def iterator: Iterator[(K, V)] = map.iterator

    def -=(key: K) = {
      keysOfMap -= key
      map -= key
      this
    }
  }

  def newMaxSizeImmutableMap[K, V](maxSize: Int): Map[K, V] = new Map[K, V] {
    var map = Map[K, V]()
    var keysOfMap = List[K]() /// TODO SLOW

    def +[V1 >: V](kv: (K, V1)): scala.collection.immutable.Map[K, V1] = {
      keysOfMap = keysOfMap :+ kv._1
      if (keysOfMap.size > maxSize) {
        val keyToRemove = keysOfMap.head
        keysOfMap = keysOfMap.tail
        map -= keyToRemove
      }
      map = map.updated(kv._1, kv._2.asInstanceOf[V])
      this
    }

    def -(key: K): scala.collection.immutable.Map[K, V] = {
      keysOfMap = keysOfMap.filter(_ == key)
      map -= key
      this
    }

    def get(key: K): Option[V] = map.get(key)

    def iterator: Iterator[(K, V)] = map.iterator
  }
}