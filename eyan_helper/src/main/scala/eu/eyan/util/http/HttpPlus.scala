package eu.eyan.util.http

import java.io.DataOutputStream
import java.net.CookieHandler
import java.net.CookieManager
import java.net.HttpURLConnection
import java.net.URL
import scala.io.Source
import eu.eyan.log.Log
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.net.ssl.SSLContext

object HttpPlus {
  private def activateCookieManager = if (CookieHandler.getDefault == null) CookieHandler.setDefault(new CookieManager())

  startSSLWorkaround

  // FIXME: this is not going to stop:
  def startSSLWorkaround = {
    val trustAllCerts = Array[TrustManager](new X509TrustManager {
      override def getAcceptedIssuers(): Array[java.security.cert.X509Certificate] = null
      override def checkClientTrusted(certs: Array[java.security.cert.X509Certificate], authType: String) = {}
      override def checkServerTrusted(certs: Array[java.security.cert.X509Certificate], authType: String) = {}
    })
    try {
      val sc = SSLContext.getInstance("SSL")
      sc.init(null, trustAllCerts, new java.security.SecureRandom())
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
      Log.debug("SSL OK")
    }
    catch {
      case t: Throwable => Log.error(t)
    }
  }

  def sendPost(urlPath: String, postParams: String = "") = {
    activateCookieManager
    Log.debug("Sending 'POST' request to URL : " + urlPath)
    Log.debug("  params: " + postParams)

    val url = new URL(urlPath)
    val conn = url.openConnection().asInstanceOf[HttpURLConnection]

    conn.setUseCaches(false)
    conn.setRequestMethod("POST")

    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
    conn.setRequestProperty("Connection", "keep-alive")
    conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()))
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
    conn.setRequestProperty("User-Agent", "Mozilla/5.0")

    conn.setDoOutput(true)
    conn.setDoInput(true)

    // Send post request
    val wr = new DataOutputStream(conn.getOutputStream)
    wr.writeBytes(postParams)
    wr.flush
    wr.close

    Log.debug("Response Code : " + conn.getResponseCode)
    Source.fromInputStream(conn.getInputStream)
  }

  def sendGet_responseAsStream(url: String) = {
    val obj = new URL(url)
    Log.debug("Sending 'GET' request to URL : " + url)

    val conn = obj.openConnection.asInstanceOf[HttpURLConnection]
    conn.setUseCaches(false)
    conn.setRequestMethod("GET")
    conn.setRequestProperty("User-Agent", "Mozilla/5.0")
    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5")

    Log.debug("Response Code : " + conn.getResponseCode)

    conn.getInputStream
  }
}