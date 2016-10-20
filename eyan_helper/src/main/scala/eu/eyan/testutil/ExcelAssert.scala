package eu.eyan.testutil

import java.io.File
import java.io.IOException

import org.fest.assertions.Assertions.assertThat

import jxl.Workbook
import jxl.WorkbookSettings
import jxl.read.biff.BiffException

object ExcelAssert {
  def getWorkbookSettings = {
    val ws = new WorkbookSettings
    ws.setEncoding("Cp1252")
    ws
  }

  def assertExcelCell(sourceFile: File, sheetName: String, column: Int, row: Int, expected: String) = {
    var workbook: Workbook = null
    try {
      workbook = Workbook.getWorkbook(sourceFile, getWorkbookSettings)
      assertThat(workbook.getSheet(sheetName).getCell(column - 1, row - 1).getContents()).isEqualTo(expected)
    }
    catch {
      case e: BiffException => e.printStackTrace
      case e: IOException   => e.printStackTrace
    }
    finally {
      if (workbook != null) workbook.close 
    }
  }
}