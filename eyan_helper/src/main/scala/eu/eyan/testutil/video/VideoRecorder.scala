package eu.eyan.testutil.video

import java.io.IOException
import org.fest.swing.timing.Pause
import java.awt.Component
import java.awt.GraphicsConfiguration
import java.awt.Rectangle
import java.io.File
import org.monte.media.Format
import java.awt.GraphicsEnvironment
import org.monte.media.FormatKeys.EncodingKey
import org.monte.media.FormatKeys.FrameRateKey
import org.monte.media.FormatKeys.KeyFrameIntervalKey
import org.monte.media.FormatKeys.MIME_AVI
import org.monte.media.FormatKeys.MediaTypeKey
import org.monte.media.FormatKeys.MimeTypeKey
import org.monte.media.VideoFormatKeys.CompressorNameKey
import org.monte.media.VideoFormatKeys.DepthKey
import org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE
import org.monte.media.VideoFormatKeys.QualityKey
import org.monte.media.FormatKeys.MediaType
import org.monte.media.math.Rational
import java.awt.AWTException
import eu.eyan.log.Log

class VideoRecorder {

  private var screenRecorder: ScreenRecorderToFile = null

  def start(component: Component, fileLocation: String, videoName: String) = {
    try {
      val gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()

      val fileFormat = new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI)

      val FIFTEEN = 15

      val screenFormat = new Format(
        MediaTypeKey,
        MediaType.VIDEO,
        EncodingKey,
        ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
        CompressorNameKey,
        ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
        DepthKey,
        24.asInstanceOf[Object],
        FrameRateKey,
        Rational.valueOf(FIFTEEN),
        QualityKey, 1.0f.asInstanceOf[Object],
        KeyFrameIntervalKey,
        (15 * 60).asInstanceOf[Object])

      val mouseFormat = new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(2 * FIFTEEN))

      val capture = new Rectangle(component.getLocationOnScreen(), component.getSize())

      screenRecorder = new ScreenRecorderToFile(gc, capture, fileFormat, screenFormat, mouseFormat, null, new File(fileLocation), videoName)
      screenRecorder.start()
    }
    catch {
      case e: IOException  => Log.error(e)
      case e: AWTException => Log.error(e)
    }
  }

  def stopAndSaveVideo = {
    val WAIT_TIME_FOR_ENDING_VIDEO_MS = 3000
    Pause.pause(WAIT_TIME_FOR_ENDING_VIDEO_MS)
    try stop
    catch {
      case e: IOException => Log.error(e)
    }
  }

  def stopAndDeleteVideo = {
    val videoFile = screenRecorder.getVideoFile // must be the first line, because screenRecorder set to null
    try stop
    catch { case e: IOException => Log.error(e) }
    finally if (videoFile != null && videoFile.exists() && videoFile.isFile()) videoFile.delete()
  }

  private def stop = if (screenRecorder != null) { screenRecorder.stop; screenRecorder = null }
}