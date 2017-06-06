package eu.eyan.util.collection

import scala.collection.MapLike

object MapsPlus {
  // FIXME delete java
  def newMaxSizeHashMap[K, V](maxSize: Int): java.util.Map[K, V] = new java.util.LinkedHashMap[K, V](maxSize) {
    override def removeEldestEntry(eldest: java.util.Map.Entry[K, V]) = size() > maxSize
  }
}