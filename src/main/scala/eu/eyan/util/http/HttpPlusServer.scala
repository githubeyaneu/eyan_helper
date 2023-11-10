package eu.eyan.util.http

import eu.eyan.util.string.StringPlus.StringPlusImplicit

object HttpPlusServer {
  def https(serverUrl: String) = new HttpsServer(serverUrl)
}

class HttpsServer(serverUrl: String) {
  def page(pageUrl: String) = new HttpsServerPage(serverUrl + "/" + pageUrl)

  def post(subUrl: String, postParams: String = "dontcare=dontcare"): String = new HttpsServerPage(serverUrl + "/" + subUrl).post(postParams)
}

class HttpsServerPage(url: String) {
  def post(postParams: String = "dontcare=dontcare"): String = {
    val pageUrl = "https://" + url
    val pageContent = pageUrl.asUrlPost(postParams).mkString
    println(pageUrl, pageContent)
    pageContent
  }
}