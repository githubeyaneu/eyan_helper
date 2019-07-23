package org.slf4j

import java.io.Serializable
import java.util
import java.util.Iterator

trait Marker extends Serializable {
  val ANY_MARKER: String = "*"
  val ANY_NON_NULL_MARKER: String = "+"
  def getName: String
  def add(reference: Marker): Unit
  def remove(reference: Marker): Boolean
  @Deprecated
  def hasChildren: Boolean
  def hasReferences: Boolean
  def iterator(): util.Iterator[Marker]
  def contains(other: Marker): Boolean
  def contains(name: String): Boolean
  override def equals(o: Any): Boolean
  override def hashCode(): Int
}