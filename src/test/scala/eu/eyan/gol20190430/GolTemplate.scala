package eu.eyan.gol20190430

import eu.eyan.testutil.TestPlus
import org.junit.Test
import org.junit.runner.RunWith
import eu.eyan.testutil.ScalaEclipseJunitRunnerTheories
import javax.swing.JPanel
import java.awt.Graphics
import javax.swing.JFrame
import eu.eyan.util.swing.JFramePlus.JFramePlusImplicit
import eu.eyan.util.swing.JPanelPlus.JPanelImplicit


@RunWith(classOf[ScalaEclipseJunitRunnerTheories])
class GolTest extends TestPlus {
  @Test def cellExists = 2 === 3

}
