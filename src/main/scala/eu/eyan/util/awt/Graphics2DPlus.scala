package eu.eyan.util.awt

import java.awt.Graphics2D
import java.awt.Color

object Graphics2DPlus {
  implicit class Graphics2DImplicit[G<:Graphics2D](g:G){
    def drawString(text:String, color:Color, x:Int, y:Int) = {
      g.setColor(color)
      g.drawString(text, 10, 20)
      g
    }
  }
}