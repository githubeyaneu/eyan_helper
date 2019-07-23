package eu.eyan.karnaugh

import org.junit.Test
import org.junit.runner.RunWith
import eu.eyan.testutil.ScalaEclipseJunitRunner
import eu.eyan.testutil.assertt.AssertPlus._
import eu.eyan.testutil.TestPlus

@RunWith(classOf[ScalaEclipseJunitRunner])
class KarnaughTest extends TestPlus {
  def a = Set(Var("A"))
  def na = Set(VarNot("A"))

  def a_b = Set(Var("A"), Var("B"))
  def a_nb = Set(Var("A"), VarNot("B"))
  def na_b = Set(VarNot("A"), Var("B"))
  def na_nb = Set(VarNot("A"), VarNot("B"))
  
  def A = new Karnaugh(Set("A"))
  def karnaugh_Atrue = A.addCell(a, true)
  def karnaugh_Atrue_nAtrue = karnaugh_Atrue.addCell(na, true)

  def karnaughAB = new Karnaugh(Set("A", "B"))
  
  @Test def varsEqual = Var("A") ==> Var("A") 
  @Test def varsNotEqual = Var("A") !== VarNot("A")
  
	@Test def createKarnaugh = A
	@Test def noCells =  A.solve ==> KarnaughCellsMissing()
	@Test def addCells = karnaugh_Atrue
	@Test def notEnoughCells =  karnaugh_Atrue.solve ==> KarnaughCellsMissing()
	@Test def enoughCells = karnaugh_Atrue_nAtrue.solve
  
  @Test def notEnoughCells_ab =  karnaughAB.addCell(a_b, true).solve ==> KarnaughCellsMissing()
  @Test def enoughCells_ab = karnaughAB.addCell(a_b, true).addCell(a_nb, true).addCell(na_b, true).addCell(na_nb, true).solve
	  
  @Test def a_true = A.addCell(a, true).addCell(na, true).solve ==> "true"
  @Test def a_false = A.addCell(a, false).addCell(na, false).solve ==> "false"
  @Test def a_a = A.addCell(a, true).addCell(na, false).solve ==> "A"
  @Test def a_nota = A.addCell(a, false).addCell(na, true).solve ==> "!A"

  
}