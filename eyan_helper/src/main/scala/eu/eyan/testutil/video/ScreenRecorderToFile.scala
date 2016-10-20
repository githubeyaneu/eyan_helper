package eu.eyan.testutil.video

import java.text.SimpleDateFormat
import org.monte.screenrecorder.ScreenRecorder
import org.monte.media.Format
import java.io.File
import java.awt.GraphicsConfiguration
import java.awt.Rectangle
import java.io.IOException
import java.util.Date
import org.monte.media.Registry

/**
 * Class that makes possible to save video files into the specified directory.
 */
class ScreenRecorderToFile(
  cfg: GraphicsConfiguration,
  captureArea: Rectangle,
  fileFormat: Format,
  screenFormat: Format,
  mouseFormat: Format,
  audioFormat: Format,
  folder: File,
  videoName: String)
    extends ScreenRecorder(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, folder) {

  if (!folder.exists()) folder.mkdirs()
  else if (!folder.isDirectory) throw new IOException("\"" + folder + "\" is not a directory.")

  val timeString = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date())
  val extension = Registry.getInstance().getExtension(fileFormat)
  val videoFile = new File(folder, videoName + "_" + timeString + "." + extension)

  override def createMovieFile(fileFormat: Format) = videoFile
  def getVideoFile = videoFile
}