package eu.eyan.util.string

import org.fest.assertions.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

import StringsSearchTree.newTree
import eu.eyan.testutil.ScalaEclipseJunitRunner
import eu.eyan.util.random.RandomPlus
import eu.eyan.util.string.StringPlus.StringPlusImplicit
import eu.eyan.testutil.TestPlus

@RunWith(classOf[ScalaEclipseJunitRunner])
class StringPlusImplicitTest extends TestPlus{

  @Test def toCamel = {
    "KOMBI_PHONE_CALL_ACTIVE_2".toCamelCase ==> "KombiPhoneCallActive2"
    "activeHMIContext3ChoiceB4".toUnderScoreCase ==> "active_H_M_I_Context3_Choice_B4"
      
    "a".toCamelCase ==> "a"
    "A".toCamelCase ==> "A"
    "2".toCamelCase ==> "2"
    "_".toCamelCase ==> ""

    "aa".toCamelCase ==> "aa"
    "aA".toCamelCase ==> "aa"
    "a2".toCamelCase ==> "a2"
    "a_".toCamelCase ==> "a"

    "Aa".toCamelCase ==> "Aa"
    "AA".toCamelCase ==> "Aa"
    "A2".toCamelCase ==> "A2"
    "A_".toCamelCase ==> "A"
    
    "2a".toCamelCase ==> "2a"
    "2A".toCamelCase ==> "2a"
    "22".toCamelCase ==> "22"
    "2_".toCamelCase ==> "2"
    
    "_a".toCamelCase ==> "A"
    "_A".toCamelCase ==> "A"
    "_2".toCamelCase ==> "2"
    "__".toCamelCase ==> ""
  }
  
  
  @Test def toUnderScore = "KombiPhoneCallActive2".toUnderScoreCase ==> "Kombi_Phone_Call_Active2"
    
    
    
}