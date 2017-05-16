package eu.eyan.util.http;

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.net.CookieManager
import java.net.CookieHandler

/**
 * Should be deleted, use HttpPlus instead!
 */
@Deprecated
object HttpHelper {
  @Deprecated
  @throws(classOf[Exception]) 
  /**
   * Should be deleted, use HttpPlus instead!
   */
  def postUrl(request: String, urlParameters: String): String = {
    val obj = new URL(request)
    val httpURLConnection = obj.openConnection().asInstanceOf[HttpURLConnection]

    httpURLConnection.setRequestMethod("POST")
    httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
    httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

    httpURLConnection.setDoOutput(true)
    val dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())
    dataOutputStream.writeBytes(urlParameters)
    dataOutputStream.flush()
    dataOutputStream.close()

    val bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), Charset.forName("UTF-8")))

    val ret = new StringBuffer()
    var inputLine: String = bufferedReader.readLine()
    while (inputLine != null) {
      ret.append(inputLine + "\r\n");
      inputLine = bufferedReader.readLine()
    }
    bufferedReader.close()

    ret.toString()
  }
}