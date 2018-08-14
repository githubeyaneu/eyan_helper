package eu.eyan.util.collection

import scala.collection.mutable.MutableList
import scala.collection.mutable.ListBuffer
import scala.collection.GenTraversableOnce

object MapsPlus {
  def maxSizeMutableMap[K, V](maxSize: Int) = new MaxSizeMutableMap[K, V](maxSize)
  def maxSizeImmutableMap[K, V](maxSize: Int): Map[K, V] = new MaxSizeImmutableMap(maxSize, Map[K, V](), List[K]())

  class MaxSizeMutableMap[K, V](maxSize: Int) extends scala.collection.mutable.Map[K, V] {
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

  class MaxSizeImmutableMap[K, V](maxSize: Int, myMap: Map[K, V] = Map[K, V](), keysOfMap: List[K] = List[K]()) extends Map[K, V] {

    override def ++[V1 >: V](kv: GenTraversableOnce[(K, V1)]): Map[K,V1]  = {
      val newItems = kv.toList.unzip
      val newKeys = newItems._1
      val newValues = newItems._2
      
      val nrToDrop = keysOfMap.size+newKeys.size-maxSize
      val addedKeysOfMap = keysOfMap++newKeys
      val keysToDrop = addedKeysOfMap.take(nrToDrop)
      
      val newKeysOfMap = addedKeysOfMap drop nrToDrop

      val smallerMap = myMap -- keysToDrop 
      val newMap = smallerMap ++ kv 

      new MaxSizeImmutableMap(maxSize, newMap, newKeysOfMap)
    }
             
    def +[V1 >: V](kv: (K, V1)): scala.collection.immutable.Map[K, V1] = {
      def newKeys =
        if (keysOfMap.size == maxSize) keysOfMap.tail :+ kv._1
        else keysOfMap :+ kv._1

      def newMap =
        if (keysOfMap.size == maxSize) myMap - keysOfMap.head + kv
        else myMap + kv

      new MaxSizeImmutableMap(maxSize, newMap, newKeys)
    }

    def -(key: K): scala.collection.immutable.Map[K, V] =
      new MaxSizeImmutableMap(maxSize, myMap - key, keysOfMap.filter(_ == key))

    def get(key: K): Option[V] = myMap.get(key)

    def iterator: Iterator[(K, V)] = myMap.iterator
  }
}