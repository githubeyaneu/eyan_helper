package eu.eyan.util.excel

import eu.eyan.log.Log
import jxl.Sheet
import jxl.Workbook
import jxl.WorkbookSettings
import java.io.InputStream
import java.io.File
import java.nio.charset.Charset

case class ExcelColumn(index: Int)
case class ExcelRow(index: Int)
case class ExcelCell(column: ExcelColumn, row: ExcelRow, content: Option[String])

case class ExcelSheet(val columns: IndexedSeq[ExcelColumn], val rows: IndexedSeq[ExcelRow], private val cells: Map[(ExcelColumn, ExcelRow), String]) {
  private val EMPTY_STRING = ""

  def firstRowCells = rowCells(ExcelRow(0))
  def firstColumnCells = columnCells(ExcelColumn(0))

  def rowFromFirstColumn(contentToSearchInFirstColumn: String) = firstColumnCells.filter(_.content == Option(contentToSearchInFirstColumn)).map(_.row).lift(0)
  def columnFromFirstRow(contentToSearchInFirstRow: String) = firstRowCells.filter(_.content == Option(contentToSearchInFirstRow)).map(_.column).lift(0)

  def getCell(columnRow: (ExcelColumn, ExcelRow)) = ExcelCell(columnRow._1, columnRow._2, cells.get(columnRow))
  def getContentOrEmpty(columnRow: (ExcelColumn, ExcelRow)) = getCell(columnRow).content.getOrElse(EMPTY_STRING)

  private def columnCells(column: ExcelColumn) = rows.map(row => getCell((column, row)))
  private def rowCells(row: ExcelRow) = columns.map(column => getCell((column, row)))
}

object ExcelPlus {
  def readExcelFromFile(file: File, sheetName: String) = {
    Log.info(s"$file exists: ${file.exists}")
    val workbook = Workbook.getWorkbook(file, WORKBOOK_SETTINGS)
    val excel = workbookSheetToExcel(workbook, sheetName)
    workbook.close
    excel
  }

  def readExcelFromStream(data: InputStream, sheetName: String) = {
    Log.info
    val workbook = Workbook.getWorkbook(data, WORKBOOK_SETTINGS)
    val excel = workbookSheetToExcel(workbook, sheetName)
    workbook.close
    excel
  }

  def sheetToExcel(sheet: Sheet) = {
    val columns = for (columnIndex <- 0 until sheet.getColumns) yield ExcelColumn(columnIndex)
    val rows = for (rowIndex <- 0 until sheet.getRows) yield ExcelRow(rowIndex)
    val table = for (column <- columns; row <- rows) yield ((column, row), sheet.getCell(column.index, row.index).getContents)
    ExcelSheet(columns, rows, table.toMap)
  }

  def WORKBOOK_SETTINGS = { val ws = new WorkbookSettings(); ws.setEncoding("Cp1252"); ws }

  private def workbookSheetToExcel(workbook: Workbook, sheetName: String) = {
    Log.info(s"Sheets: ${workbook.getSheetNames.mkString}")
    val sheet = getSheet(WORKBOOK_SETTINGS, workbook, sheetName)
    sheetToExcel(sheet)
  }

  private def getSheet(ws: WorkbookSettings, workbook: Workbook, string: String) = {
    val sheet = workbook.getSheet(new String(string.getBytes(Charset.forName(ws.getEncoding()))))
    if (sheet == null) workbook.getSheet(string)
    else sheet
  }
}