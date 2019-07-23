package eu.eyan.util.jgoodies

import com.jgoodies.forms.layout.FormLayout
import java.awt.LayoutManager2
import com.jgoodies.forms.layout.ColumnSpec
import com.jgoodies.forms.layout.RowSpec
import com.jgoodies.forms.layout.CellConstraints
import java.awt.Component
import java.awt.Container
import com.jgoodies.forms.factories.CC

object FormLayoutPlus {
  def apply(container: Container, colSpec: String) = {
    val ret = new FormLayoutPlus(container, colSpec)
    container.setLayout(ret)
    ret
  }

  def apply(container: Container, colSpec: String, rowSpec: String, component: Component) = {
    val ret = new FormLayoutPlus(container, colSpec)
    container.setLayout(ret)
    ret.appendRow(rowSpec, component)
    ret
  }
}

class FormLayoutPlus private (container: Container, colSpec: String) extends LayoutManager2 {
  val formLayout = new FormLayout(colSpec)
  private var rows = 0

  override def hashCode() = formLayout.hashCode()

  override def equals(obj: Any): Boolean = formLayout.equals(obj)

  override def toString = formLayout.toString

  def getComponent = container

  def appendRow(rowSpecs: String*): Int = {
    for {rowSpec <- rowSpecs} { appendRow(RowSpec.decode(rowSpec)); rows += 1 }
    rows
  }

  def appendRow(rowSpec: String, component: Component): FormLayoutPlus = {
    appendRow(RowSpec.decode(rowSpec))
    rows += 1
    if (component != null) container.add(component, CC.xy(1, rows))
    this
  }

  def appendRow(rowSpec: String, formLayout: FormLayoutPlus): FormLayoutPlus = appendRow(rowSpec, formLayout.getComponent)

  /* DELEGATES */
  def getColumnCount = formLayout.getColumnCount

  def getColumnSpec(columnIndex: Int) = formLayout.getColumnSpec(columnIndex)

  def setColumnSpec(columnIndex: Int, columnSpec: ColumnSpec) = formLayout.setColumnSpec(columnIndex, columnSpec)

  def appendColumn(columnSpec: ColumnSpec) = formLayout.appendColumn(columnSpec)

  def insertColumn(columnIndex: Int, columnSpec: ColumnSpec) = formLayout.insertColumn(columnIndex, columnSpec)

  def removeColumn(columnIndex: Int) = formLayout.removeColumn(columnIndex)

  def getRowCount = formLayout.getRowCount

  def getRowSpec(rowIndex: Int) = formLayout.getRowSpec(rowIndex)

  def setRowSpec(rowIndex: Int, rowSpec: RowSpec) = formLayout.setRowSpec(rowIndex, rowSpec)

  def appendRow(rowSpec: RowSpec) = formLayout.appendRow(rowSpec)

  def insertRow(rowIndex: Int, rowSpec: RowSpec) = formLayout.insertRow(rowIndex, rowSpec)

  def removeRow(rowIndex: Int) = formLayout.removeRow(rowIndex)

  def getConstraints(component: Component) = formLayout.getConstraints(component)

  def setConstraints(component: Component, constraints: CellConstraints) = formLayout.setConstraints(component, constraints)

  def getColumnGroups = formLayout.getColumnGroups

  def setColumnGroups(colGroupIndices: Array[Array[Int]]) = formLayout.setColumnGroups(colGroupIndices)

  def addGroupedColumn(columnIndex: Int) = formLayout.addGroupedColumn(columnIndex)

  def getRowGroups = formLayout.getRowGroups

  def setRowGroups(rowGroupIndices: Array[Array[Int]]) = formLayout.setRowGroups(rowGroupIndices)

  def addGroupedRow(rowIndex: Int) = formLayout.addGroupedRow(rowIndex)

  def getHonorsVisibility = formLayout.getHonorsVisibility

  def setHonorsVisibility(b: Boolean) = formLayout.setHonorsVisibility(b)

  def setHonorsVisibility(component: Component, b: Boolean) = formLayout.setHonorsVisibility(component, b)

  def addLayoutComponent(name: String, component: Component) = formLayout.addLayoutComponent(name, component)

  def addLayoutComponent(comp: Component, constraints: Object) = formLayout.addLayoutComponent(comp, constraints)

  def removeLayoutComponent(comp: Component) = formLayout.removeLayoutComponent(comp)

  def minimumLayoutSize(parent: Container) = formLayout.minimumLayoutSize(parent)

  def preferredLayoutSize(parent: Container) = formLayout.preferredLayoutSize(parent)

  def maximumLayoutSize(target: Container) = formLayout.maximumLayoutSize(target)

  def getLayoutAlignmentX(parent: Container) = formLayout.getLayoutAlignmentX(parent)

  def getLayoutAlignmentY(parent: Container) = formLayout.getLayoutAlignmentY(parent)

  def invalidateLayout(target: Container) = formLayout.invalidateLayout(target)

  def layoutContainer(parent: Container) = formLayout.layoutContainer(parent)

  def getLayoutInfo(parent: Container) = formLayout.getLayoutInfo(parent)
  /* DELEGATES END */
}