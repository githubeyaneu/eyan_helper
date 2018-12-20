package eu.eyan.util.awt

import java.awt.image.BufferedImage
import java.awt.Graphics2D
import java.awt.Font
import java.awt.Color

object ImagePlus {
  def imageFromChar(c: Char, color: Color = Color.red, width: Int = 256, height: Int = 256) = {
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g2 = image.createGraphics.asInstanceOf[Graphics2D]
    g2.setFont(new Font(null, Font.BOLD, height))
    g2.setColor(color)
    g2.drawString(c + "", width / 6, height - height / 6)
    //			ImageIO.write(off_Image.asInstanceOf[RenderedImage], "png", new File("""C:\temp\A.png"""))
    image
  }

  def imageFromString(text: String, color: Color = Color.red, width: Int = 256, height: Int = 256) = {
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g2 = image.createGraphics.asInstanceOf[Graphics2D]
    g2.setFont(new Font(null, Font.BOLD, height))
    g2.setColor(color)
    g2.drawString(text, width / 6, height - height / 6)
    //			ImageIO.write(off_Image.asInstanceOf[RenderedImage], "png", new File("""C:\temp\A.png"""))
    image
  }
}