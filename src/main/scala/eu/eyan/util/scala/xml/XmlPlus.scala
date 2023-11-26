package eu.eyan.util.scala.xml

import scala.xml.XML
import scala.xml.Node
import scala.language.postfixOps

object XmlPlus {
  implicit class StringToXml(string: String) {
    def asXml:Node = XML.loadString(string)
    def xPathText(xPath: String) = (string.asXml \\ xPath).text
  }

  implicit class Xml(node:Node) {
    def childDeepText(xPath: String) = (node.\\(xPath)).text
    def childText(child: String) = (node \ child) text
    def firstChildDeepText(child: String) = ((node \\ child).toList.headOption.map(_.text).getOrElse(""))
    def attributeText(attrName: String) = node.attribute(attrName).map(_.text).getOrElse("")
  }
}
