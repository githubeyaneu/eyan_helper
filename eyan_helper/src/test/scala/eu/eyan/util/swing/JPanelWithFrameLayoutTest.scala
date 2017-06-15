package eu.eyan.util.swing

import java.awt.Component

import org.fest.assertions.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.jgoodies.forms.layout.FormLayout

import eu.eyan.testutil.ScalaEclipseJunitRunner
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JViewport
import javax.swing.JLabel

@RunWith(classOf[ScalaEclipseJunitRunner])
class JPanelWithFrameLayoutTest() {

  val BORDER = JPanelWithFrameLayout.DEFAULT_BORDER_SIZE
  val SEPARATOR = JPanelWithFrameLayout.DEFAULT_SEPARATOR_SIZE

  @Before
  def setUp = {}

  @Test
  def test_JPanelWithFrameLayout = {
    val panel = new JPanelWithFrameLayout
    assertThat(panel.columnCount).isEqualTo(0)
    assertThat(panel.rowCount).isEqualTo(0)
  }

  @Test
  def test_JPanelWithFrameLayoutRowAndCol = {
    val panel = JPanelWithFrameLayout("10dlu:g", "f:20dlu:g")

    assertThat(panel.columnCount).isEqualTo(1)
    assertThat(panel.rowCount).isEqualTo(1)

    assertThat(panel.colSpec(1)).isEqualTo("10dlu:g")
    assertThat(panel.rowSpec(1)).isEqualTo("f:20dlu:g")
  }

  @Test
  def test_WithoutBorders_WithoutSeparators = {
    val panel = new JPanelWithFrameLayout()

    panel.newColumnFPG
    panel.assertColumnSpecs(List("p:g"))

    panel.newColumn
    panel.assertColumnSpecs(List("p:g", "p"))

    panel.newColumn("11dlu")
    panel.assertColumnSpecs(List("p:g", "p", "11dlu"))

    panel.newRowFPG
    panel.assertRowSpecs(List("f:p:g"))

    panel.newRow("1px")
    panel.assertRowSpecs(List("f:p:g", "1px"))

    panel.newRow("2px")
    panel.assertRowSpecs(List("f:p:g", "1px", "2px"))

    panel.newRow
    panel.assertRowSpecs(List("f:p:g", "1px", "2px", "p"))
  }

  @Test
  def test_WithBorders = {
    val panel = new JPanelWithFrameLayout()
    panel.withBorders

    panel.newColumnFPG
    panel.assertColumnSpecs(List(BORDER, "p:g", BORDER))

    panel.newColumn
    panel.assertColumnSpecs(List(BORDER, "p:g", "p", BORDER))

    panel.newColumn("11dlu")
    panel.assertColumnSpecs(List(BORDER, "p:g", "p", "11dlu", BORDER))

    panel.newRowFPG
    panel.assertRowSpecs(List(BORDER, "f:p:g", BORDER))

    panel.newRow("1px")
    panel.assertRowSpecs(List(BORDER, "f:p:g", "1px", BORDER))

    panel.newRow("2px")
    panel.assertRowSpecs(List(BORDER, "f:p:g", "1px", "2px", BORDER))

    panel.newRow
    panel.assertRowSpecs(List(BORDER, "f:p:g", "1px", "2px", "p", BORDER))
  }

  @Test
  def test_WithBorders_AndSeparators = {
    val panel = new JPanelWithFrameLayout()
    panel.withBorders
    panel.withSeparators

    panel.newColumnFPG
    panel.assertColumnSpecs(List(BORDER, "p:g", BORDER))

    panel.newColumn
    panel.assertColumnSpecs(List(BORDER, "p:g", SEPARATOR, "p", BORDER))

    panel.newColumn("11dlu")
    panel.assertColumnSpecs(List(BORDER, "p:g", SEPARATOR, "p", SEPARATOR, "11dlu", BORDER))

    panel.newRowFPG
    panel.assertRowSpecs(List(BORDER, "f:p:g", BORDER))

    panel.newRow("1px")
    panel.assertRowSpecs(List(BORDER, "f:p:g", SEPARATOR, "1px", BORDER))

    panel.newRow("2px")
    panel.assertRowSpecs(List(BORDER, "f:p:g", SEPARATOR, "1px", SEPARATOR, "2px", BORDER))

    panel.newRow
    panel.assertRowSpecs(List(BORDER, "f:p:g", SEPARATOR, "1px", SEPARATOR, "2px", SEPARATOR, "p", BORDER))
  }

  @Test
  def test_WithSeparators = {
    val panel = new JPanelWithFrameLayout()
    panel.withSeparators

    panel.newColumnFPG
    panel.assertColumnSpecs(List("p:g"))

    panel.newColumn
    panel.assertColumnSpecs(List("p:g", SEPARATOR, "p"))

    panel.newColumn("11dlu")
    panel.assertColumnSpecs(List("p:g", SEPARATOR, "p", SEPARATOR, "11dlu"))

    panel.newRowFPG
    panel.assertRowSpecs(List("f:p:g"))

    panel.newRow("1px")
    panel.assertRowSpecs(List("f:p:g", SEPARATOR, "1px"))

    panel.newRow("2px")
    panel.assertRowSpecs(List("f:p:g", SEPARATOR, "1px", SEPARATOR, "2px"))

    panel.newRow
    panel.assertRowSpecs(List("f:p:g", SEPARATOR, "1px", SEPARATOR, "2px", SEPARATOR, "p"))
  }

  @Test
  def test_add_NoBorders_NoSeparators = {
    val panel = new JPanelWithFrameLayout()

    panel.addLabel("").assertPosition(1, 1)
    panel.assertColumnSpecs(List("p"))
    panel.assertRowSpecs(List("p"))

    panel.newColumn
    panel.addLabel("").assertPosition(2, 1)

    panel.newRow
    panel.addLabel("").assertPosition(1, 2)

    panel.nextColumn
    panel.addLabel("").assertPosition(2, 2)

    //Span
    panel.newRow
    panel.span
    panel.addLabel("").assertPosition(1, 3, 2, 1)

    panel.newColumn.newColumn.newRow
    panel.nextColumn.span(2)
    panel.addLabel("").assertPosition(2, 4, 3, 1)
  }

  @Test
  def test_add_WithBorders_NoSeparators = {
    val panel = new JPanelWithFrameLayout()
    panel.withBorders

    panel.addLabel("").assertPosition(2, 2)

    panel.newColumn
    panel.addLabel("").assertPosition(3, 2)

    panel.newColumn
    panel.addLabel("").assertPosition(4, 2)

    panel.newRow
    panel.addLabel("").assertPosition(2, 3)

    panel.nextColumn
    panel.addLabel("").assertPosition(3, 3)

    panel.newRow.nextColumn.nextColumn
    panel.addLabel("").assertPosition(4, 4)

    //Span
    panel.newRow
    panel.span
    panel.addLabel("").assertPosition(2, 5, 2, 1)

    panel.newColumn.newColumn.newRow
    panel.nextColumn.span(2)
    panel.addLabel("").assertPosition(3, 6, 3, 1)
  }

  @Test
  def test_add_NoBorders_WithSeparators = {
    val panel = new JPanelWithFrameLayout()
    panel.withSeparators

    panel.addLabel("").assertPosition(1, 1)

    panel.newColumn
    panel.addLabel("").assertPosition(3, 1)

    panel.newColumn
    panel.addLabel("").assertPosition(5, 1)

    panel.newRow
    panel.addLabel("").assertPosition(1, 3)

    panel.nextColumn
    panel.addLabel("").assertPosition(3, 3)

    panel.newRow.nextColumn.nextColumn
    panel.addLabel("").assertPosition(5, 5)

    //Span
    panel.newRow
    panel.span
    panel.addLabel("").assertPosition(1, 7, 3, 1)

    panel.newColumn.newColumn.newRow
    panel.nextColumn.span(2)
    panel.addLabel("").assertPosition(3, 9, 5, 1)
  }

  @Test
  def test_add_WithBorders_AndSeparators = {
    val panel = new JPanelWithFrameLayout()
    panel.withBorders
    panel.withSeparators

    panel.addLabel("").assertPosition(2, 2)

    panel.newColumn
    panel.addLabel("").assertPosition(4, 2)

    panel.newColumn
    panel.addLabel("").assertPosition(6, 2)

    panel.newRow
    panel.addLabel("").assertPosition(2, 4)

    panel.nextColumn
    panel.addLabel("").assertPosition(4, 4)

    panel.newRow.nextColumn.nextColumn
    panel.addLabel("").assertPosition(6, 6)

    //Span
    panel.newRow
    panel.span
    panel.addLabel("").assertPosition(2, 8, 3, 1)

    panel.newColumn.newColumn.newRow
    panel.nextColumn.span(2)
    panel.addLabel("").assertPosition(4, 10, 5, 1)
  }

  @Test
  def test_addButton = {
    val button = new JPanelWithFrameLayout().addButton("123")
    button.assertPosition(1, 1)
    assertThat(button.getText).isEqualTo("123")
  }

  @Test
  def test_addTextField = {
    val tf = new JPanelWithFrameLayout().addTextField("123", 123)
    tf.assertPosition(1, 1)
    assertThat(tf.getText).isEqualTo("123")
    assertThat(tf.getColumns).isEqualTo(123)
  }

  @Test
  def test_addTextArea = {
    val ta = new JPanelWithFrameLayout().addTextArea("123")
    ta.assertClass(classOf[JTextAreaPlus])
    ta.getParent.assertClass(classOf[JViewport])
    ta.getParent.getParent.assertClass(classOf[JScrollPane])
    ta.getParent.getParent.getParent.assertClass(classOf[JPanelWithFrameLayout])

    // TODO why does not work: tf.getParent.getParent.getParent.getParent.assertClass(classOf[JPanelWithFrameLayout])
    //      tf.getParent.getParent.getParent.assertPosition(1, 1)

    assertThat(ta.getText).isEqualTo("123")
  }

  @Test
  def test_addLabel = {
    val label = new JPanelWithFrameLayout().addLabel("123")
    label.assertPosition(1, 1)
    assertThat(label.getText).isEqualTo("123")
  }

  @Test
  def test_addList = {
    val list = new JPanelWithFrameLayout().addList
    list.assertPosition(1, 1)
  }

  @Test
  def test_addTable = {
    val table = new JPanelWithFrameLayout().addTable[Int]
    table.assertClass(classOf[JTablePlus[String]])
    table.getParent.assertClass(classOf[JViewport])
    table.getParent.getParent.assertClass(classOf[JScrollPane])
    table.getParent.getParent.assertPosition(1, 1)
  }

  @Test
  def test_addPanelWithFormLayout = {
    val panel = new JPanelWithFrameLayout().addPanelWithFormLayout
    panel.assertClass(classOf[JPanelWithFrameLayout])
    panel.getParent.assertClass(classOf[JPanelWithFrameLayout])
    panel.assertPosition(1, 1)
  }

  @Test
  def test_addSeparatorWithTitle = {
    val thiss = new JPanelWithFrameLayout().addSeparatorWithTitle("asd")
    thiss.assertClass(classOf[JPanelWithFrameLayout])
    // complicated to test asd
  }

  implicit class ComponentPlusTest[TYPE <: Component](component: TYPE) {
    def assertClass[T](clazz: Class[T]) = { assertThat(component.getClass).isEqualTo(clazz); component }

    def assertPosition(x: Int, y: Int, w: Int = 1, h: Int = 1) = {
      val constraints = component.getParent.asInstanceOf[JPanelWithFrameLayout].formLayout.getConstraints(component)
      assertThat(constraints.gridX).as("CC.x").isEqualTo(x)
      assertThat(constraints.gridY).as("CC.y").isEqualTo(y)
      assertThat(constraints.gridWidth).as("CC.w").isEqualTo(w)
      assertThat(constraints.gridHeight).as("CC.h").isEqualTo(h)
      component
    }
  }

  implicit class JPanelWithFrameLayoutTestImplicit(panel: JPanelWithFrameLayout) {
    def formLayout = panel.getLayout.asInstanceOf[FormLayout]

    def columnCount = panel.formLayout.getColumnCount
    def rowCount = panel.formLayout.getRowCount

    def rowSpec(row: Int) = panel.formLayout.getRowSpec(row).encode()
    def colSpec(row: Int) = panel.formLayout.getColumnSpec(row).encode()

    def columnSpecs = (for (col <- 1 to columnCount) yield colSpec(col)).toList
    def rowSpecs = (for (row <- 1 to rowCount) yield rowSpec(row)).toList

    def assertColumnSpecs(specs: List[String]) = assertThat(panel.columnSpecs).isEqualTo(specs)
    def assertRowSpecs(specs: List[String]) = assertThat(panel.rowSpecs).isEqualTo(specs)
  }
}