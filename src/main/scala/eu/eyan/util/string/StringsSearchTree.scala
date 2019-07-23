package eu.eyan.util.string

import StringsSearchTree.allSubStrings

object StringsSearchTree {
  def newTree[T](): StringsSearchTree[T] = new StringsSearchTree[T](None)

  def allSubStrings(line: String, depth: Int) = List("") ++ (for {size <- 1 to depth} yield line.sliding(size)).flatten
}

class StringsSearchTree[TYPE] private (
    char: Option[Char],
    values: Set[TYPE] = Set[TYPE](),
    nodes: Map[Char, StringsSearchTree[TYPE]] = Map[Char, StringsSearchTree[TYPE]]()) {
  def get(key: String): Set[TYPE] =
    if (key.isEmpty) values
    else if (nodes.contains(key.head)) nodes(key.head).get(key.tail)
    else Set()

  def add(key: String, value: TYPE): StringsSearchTree[TYPE] =
    {
      def keyEmpty = key.isEmpty
      def valuesContainsValue = values.contains(value)
      def addToValues = new StringsSearchTree(char, values + value, nodes)
      def nodesContainKey = nodes.contains(key.head)
      def addToNode = new StringsSearchTree(char, values, nodes.updated(key.head, nodes(key.head).add(key.tail, value)))
      def newNode = new StringsSearchTree(char, values, nodes + (key.head -> new StringsSearchTree(Option(key.head)).add(key.tail, value)))

      if (keyEmpty)
        if (valuesContainsValue) this
        else addToValues
      else if (nodesContainKey) addToNode
      else newNode
    }

  def addAll(key: String, value: TYPE): StringsSearchTree[TYPE] = allSubStrings(key, key.length).foldLeft(this)((tree, string) => tree.add(string, value))

  override def toString: String = toString("")

  def toString(prefix: String): String =
    prefix + char.getOrElse("") + ": " + values.mkString(", ") + nodes.toList.map(p => "\r\n" + p._2.toString(prefix + char.getOrElse(""))).mkString
}