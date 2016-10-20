package eu.eyan.util.collection

object MapsPlus {

  //FIXME delete java 
  def newMaxSizeHashMap[K, V](maxSize: Int): java.util.Map[K, V] = new java.util.LinkedHashMap[K, V](maxSize) {
    override def removeEldestEntry(eldest: java.util.Map.Entry[K, V]) = size() > maxSize
  }

  //FIXME delete java 
  def newMap[KEY, VALUE](objects: Object*): java.util.Map[KEY, VALUE] = {
    val map = new java.util.HashMap[KEY, VALUE]()
    for (i <- 0 until objects.length / 2) { map.put(objects(i * 2).asInstanceOf[KEY], objects(i * 2 + 1).asInstanceOf[VALUE]) }
    map
  }
}