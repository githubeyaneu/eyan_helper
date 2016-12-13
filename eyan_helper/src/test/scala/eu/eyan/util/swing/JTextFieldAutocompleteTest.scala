package eu.eyan.util.swing

import java.awt.Dimension
import java.awt.Point
import java.awt.event.KeyEvent

import org.fest.assertions.Assertions.assertThat
import org.fest.swing.core.KeyPressInfo
import org.fest.swing.fixture.FrameFixture
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.jgoodies.forms.factories.CC
import com.jgoodies.forms.layout.FormLayout

import eu.eyan.testutil.AbstractUiTest
import eu.eyan.testutil.swing.fixture.AutocompleteFixture
import eu.eyan.testutil.video.VideoRunner
import eu.eyan.util.random.RandomPlus
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.WindowConstants

class JTextFieldAutocompleteTest extends AbstractUiTest {

  var autocomplete: AutocompleteFixture = null

  @Before def setUp = {
    JTextFieldAutocompleteTest.main(Array())
    frame = new FrameFixture(JTextFieldAutocompleteTest.NAME_FRAME)
    frame.target.toFront()
    VideoRunner.setComponentToRecord(frame.target)

    autocomplete = new AutocompleteFixture(frame, JTextFieldAutocompleteTest.NAME_AUTOCOMPLETE)
    componentBefore = frame.textBox(JTextFieldAutocompleteTest.NAME_BEFORE)
    componentAfter = frame.textBox(JTextFieldAutocompleteTest.NAME_AFTER)
  }

  @Test
  def create_Autocomplete = {
    assertThat(autocomplete.target).isInstanceOf(classOf[JTextField])
  }

  @Test
  def hint_text_if_empty = {
    val ac = new JTextFieldAutocomplete
    val ac2 = ac.setHintText("default")
    assertThat(ac.getHintText).isEqualTo("default")
    assertThat(ac2).isSameAs(ac)
    // TODO Has to be tested manually if it is correct displayed, but can be tested with screenshots.
    assertThat(ac.getText).isEqualTo("")
  }

  @Test
  def focus = {
    componentBefore.target.requestFocus
    componentBefore.requireFocused
    componentBefore.pressKey(KeyEvent.VK_TAB)
    autocomplete.requireFocused
    autocomplete.pressKey(KeyEvent.VK_TAB)
    componentAfter.requireFocused
  }

  @Test
  def popup_comes_with_relevant_elements = {
    autocomplete.setAutocompleteList("a", "b", "aa", "bb")
    autocomplete.requirePopupNotVisible
    autocomplete.enterText("a")
    autocomplete.popup.requireVisible
    autocomplete.list.requireItemCount(2)
    autocomplete.requireItems("a", "aa")
  }

  @Test
  def popup_comes_with_all_elements = { 
    autocomplete.setAutocompleteList("a", "b", "aa", "bb")
    autocomplete.enterText("a")
    autocomplete.popup.requireVisible
    autocomplete.pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_BACK_SPACE))
    autocomplete.list.requireItemCount(4)
  }

  @Test
  def popup_max_items = {
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("a")
    autocomplete.list.requireItemCount(4)
    autocomplete.requireItems("a", "ba", "ca", "da")
    componentBefore.pressKey(KeyEvent.VK_BACK_SPACE)

    autocomplete.setMaxElementsVisible(3)

    autocomplete.deleteText
    autocomplete.enterText("a")
    autocomplete.list.requireItemCount(3)
    autocomplete.requireItems("a", "ba", "ca")
  }

  @Test
  def popup_no_items = {
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("x")
    autocomplete.list.requireItemCount(1)
    autocomplete.requireItems(autocomplete.noItemsFoundText)
    autocomplete.list.requireDisabled
  }

  @Test
  def hide_popup = {
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("a")
    autocomplete.popup.requireVisible
    autocomplete.pressEscape
    autocomplete.requirePopupNotVisible
  }

  @Test
  def popup_up_down_keys = {
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("a")
    autocomplete.list.requireSelection(0)
    autocomplete.pressUp
    autocomplete.list.requireSelection(0)
    autocomplete.pressDown
    autocomplete.list.requireSelection(1)
    autocomplete.pressDown
    autocomplete.pressDown
    autocomplete.pressDown
    autocomplete.pressDown
    autocomplete.list.requireSelection(3)
    autocomplete.pressUp
    autocomplete.pressUp
    autocomplete.pressUp
    autocomplete.pressUp
    autocomplete.pressUp
    autocomplete.pressUp
    autocomplete.list.requireSelection(0)
  }

  @Test
  def popup_no_items_not_selectable = {
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("x")
    autocomplete.pressDown
    autocomplete.list.requireNoSelection
  }

  @Test
  def popup_selecting_item = {
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("a")
    autocomplete.pressDown
    autocomplete.pressEnter
    autocomplete.requireText("ba")
  }

  @Test
  def popup_changing_autocomplete_values = {
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("a")
    autocomplete.requireItems("a", "ba", "ca", "da")
    autocomplete.setAutocompleteList("ba", "ca", "da", "ea")
    autocomplete.requireItems("ba", "ca", "da", "ea")
  }

  @Test
  def popup_changing_max_elements_visible = {
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("a")
    autocomplete.requireItems("a", "ba", "ca", "da")
    autocomplete.setMaxElementsVisible(2)
    autocomplete.requireItems("a", "ba")
  }

  @Test
  def popup_changing_noItemsFoundText = {
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("x")
    autocomplete.requireItems(autocomplete.noItemsFoundText)
    autocomplete.setNoItemsFoundText("xdc")
    autocomplete.requireItems("xdc")
    autocomplete.list.requireDisabled
  }

  @Test
  def closing_parent_closes_popup = {
    frame.targetCastedTo(classOf[JFrame]).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    autocomplete.enterText("a")
    frame.close
    autocomplete.requirePopupNotVisible
  }

  @Test
  def closing_list_with_null = {
    autocomplete.setAutocompleteList("a", null, "aa")
    autocomplete.enterText("a")
    autocomplete.pressDown
    autocomplete.pressDown
    autocomplete.pressEnter
    autocomplete.requireText("aa")
  }

  @Test
  def closing_list_with_empty = {
    autocomplete.setAutocompleteList("a", "", "aa")
    autocomplete.pressDown
    autocomplete.pressDown
    autocomplete.pressEnter
    autocomplete.requireText("aa")
  }

  @Test
  def popup_no_items_more = { ///
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("b")
    autocomplete.requireItems("ba")
    autocomplete.list.requireEnabled
    autocomplete.pressBackspace
    autocomplete.requireItems("a", "ba", "ca", "da")
    autocomplete.list.requireEnabled
    autocomplete.enterText("x")
    autocomplete.requireItems(autocomplete.noItemsFoundText)
    autocomplete.list.requireDisabled
    autocomplete.pressBackspace
    autocomplete.requireItems("a", "ba", "ca", "da")
    autocomplete.list.requireEnabled
    autocomplete.enterText("y")
    autocomplete.requireItems(autocomplete.noItemsFoundText)
    autocomplete.list.requireDisabled
    autocomplete.pressBackspace
    autocomplete.enterText("z")
    autocomplete.requireItems(autocomplete.noItemsFoundText)
    autocomplete.list.requireDisabled
  }

  @Test
  def actions_after_escape = { ///
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("b")
    autocomplete.requireItems("ba")
    autocomplete.pressEscape
    autocomplete.requirePopupNotVisible
    autocomplete.pressBackspace
    autocomplete.enterText("b")
    autocomplete.requireItems("ba")
    autocomplete.pressEscape
    autocomplete.requirePopupNotVisible
    autocomplete.pressBackspace

    autocomplete.enterText("x")
    autocomplete.requireItems(autocomplete.noItemsFoundText)
    autocomplete.pressEscape
    autocomplete.requirePopupNotVisible
    autocomplete.enterText("x")
    autocomplete.requireItems(autocomplete.noItemsFoundText)
    autocomplete.pressEscape
    autocomplete.requirePopupNotVisible
    autocomplete.pressBackspace
    autocomplete.requireItems(autocomplete.noItemsFoundText)
    autocomplete.pressEscape
    autocomplete.requirePopupNotVisible
  }

  @Test
  def no_reaction_in_background = {
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("a")
    autocomplete.pressDown
    autocomplete.pressDown
    autocomplete.pressDown
    autocomplete.pressEscape
    autocomplete.pressEnter
    autocomplete.requireText("a")
  }

  @Test
  def double_click_selection = {
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("a")
    autocomplete.list.item(3 - 1).doubleClick
    autocomplete.requireText("ca")
    autocomplete.requirePopupNotVisible
  }

  @Test
  def hiding_resets_selection = {
    autocomplete.setAutocompleteList("a", "ba", "ca", "da", "ea")
    autocomplete.enterText("a")
    autocomplete.pressDown
    autocomplete.pressDown
    autocomplete.list.requireSelection(2)
    autocomplete.pressEscape
    autocomplete.pressDown
    autocomplete.list.requireSelection(0)
  }

  @Test
  def search_accent_and_case_insensitive = {
    autocomplete.setAutocompleteList("ÁRVÍZTŰRŐ TÜKÖRFÚRÓGÉP", "árvíztűrő tükörfúrógép", "öüäß", "ÖÜÄß")

    autocomplete.enterText("arvizturo tukorfurogep")
    autocomplete.requireItems("ÁRVÍZTŰRŐ TÜKÖRFÚRÓGÉP", "árvíztűrő tükörfúrógép")
    autocomplete.deleteText

    autocomplete.enterText("ouas")
    autocomplete.requireItems("öüäß", "ÖÜÄß")
    autocomplete.deleteText

    autocomplete.setAutocompleteList("arvizturo tukorfurogep", "ARVIZTURO TUKORFUROGEP", "OUAS", "ouas")

    autocomplete.enterText("ÁRVÍZTŰRŐ TÜKÖRFÚRÓGÉP")
    autocomplete.requireItems("arvizturo tukorfurogep", "ARVIZTURO TUKORFUROGEP")
    autocomplete.deleteText
  }

  @Test
  def items_sorted_beginsWith_then_Contains = {
    autocomplete.setAutocompleteList("aba", "ba", "ab", "bac", "dba")
    autocomplete.setMaxElementsVisible(6)

    autocomplete.enterText("ba")

    autocomplete.requireItems("ba", "bac", "aba", "dba")
  }

  @Test
  def items_distinct = {
    autocomplete.setAutocompleteList("aba", "ba", "ab", "aba", "aba")
    autocomplete.setMaxElementsVisible(6)

    autocomplete.enterText("ba")

    autocomplete.requireItems("ba", "aba")
  }

  @Test
  def items_sorted_correct_biglist = {
    autocomplete.setMaxElementsVisible(3)
    autocomplete.enterText("k")
    autocomplete.requireItems("Kaffka Margit", "Kányádi Sándor", "Karádi Ilona (szerk.)")
  }

  @Test
  def close_popup_on_focus_lost = {
    autocomplete.enterText("a")
    autocomplete.pressTab
    componentAfter.requireFocused
    autocomplete.requirePopupNotVisible
  }

  @Test
  def popup_speed = {
    val r50000 = new RandomPlus(3).nextReadableStrings(50000, 5, 15).toList

    val start = System.currentTimeMillis
    autocomplete.setAutocompleteList(r50000)
    println(System.currentTimeMillis - start)
    assertThat(System.currentTimeMillis - start).isLessThan(300)

    val start2 = System.currentTimeMillis
    autocomplete.enterText("a")
    println(System.currentTimeMillis - start2)
    assertThat(System.currentTimeMillis - start2).isLessThan(3000)
  }

  @Test
  def moving = {
    val acScreenLocation = autocomplete.target.getLocationOnScreen
    val acHeight = autocomplete.target.getHeight
    autocomplete.enterText("a")
    autocomplete.popup.requireLocation(acScreenLocation.x, acScreenLocation.y + acHeight)

    val pos = frame.target.getLocationOnScreen
    frame.moveTo(new Point(pos.x + 100, pos.y + 200))

    autocomplete.popup.requireLocation(acScreenLocation.x + 100, acScreenLocation.y + acHeight + 200)
  }

  @Test
  def resizing = {
    val acScreenLocation = autocomplete.target.getLocationOnScreen
    val acWidth = autocomplete.target.getWidth
    autocomplete.enterText("a")
    autocomplete.popup.requireWidth(acWidth)

    val size = frame.target.getSize
    frame.resizeTo(new Dimension(size.width + 130, size.height + 222))

    autocomplete.popup.requireWidth(acWidth + 130)
  }

  //nem ártana ha nagyítható lenne az egész
  //  Ha lehet akkor decorátorral megoldani
  //lista konfigurálható
  // ...scrollbarral
}

object JTextFieldAutocompleteTest {
  val NAME_FRAME = "AutocompleteTest"
  val NAME_AUTOCOMPLETE = "AutocompleteComponent"
  val NAME_BEFORE = "AutocompleteTestComponentBefore"
  val NAME_AFTER = "AutocompleteTestComponentAfter"
  val bigList = List("", "(Kondor Katalin)", "Ádám György", "Ady", "Ambrus András", "Ambrus Zoltán", "András László", "Antal László", "Apáczai Csere János", "Apor", "Apor Péter", "Arany", "Asturias, Miguel Ángel", "Babits", "Bajkay Éva", "Bakay Kornél", "Bakó Zsuzsanna", "Baktay Ervin (feld.)", "Balás Gábor", "Balás Gábor (szerk.)", "Bánkúti Imre", "Baranyi Ferenc", "Bárczy János", "Barna István (Összeáll.)", "Barta Gábor", "Barta János", "Bartha János", "Bartha Lajos (szerk.)", "Beke Kata", "Bellow, Saul", "Benedek Elek", "Benedek Elek (mesélte)", "Benedek Elek (szerk.)", "Bényei Miklós", "Bernáth Aurél", "Berzsenyi", "Bethlen", "Bitskey István", "Bizzer István", "Björnson, Björnstjerne", "Boll, Franz - Bezold, Carl", "Bolyai", "Boreczky Beratrix", "Boronkay Iván (feld.)", "Böll, Heinrich", "Börne", "Bözödi György", "Bródy Sándor", "Cela, Canmilo José", "Cholnoky Viktor", "Churchill, Winston Sir", "Czakó Gábor", "Czeizel", "Csapody Csaba", "Csapodyné", "Csiffáry Gabriella (vál.)", "Csomor Lajos", "Csurka István", "Darvas Gábor", "Deledda, Grazia", "Déry Tibor", "Devecseri Gábor", "Dornyay Béla, Vigyázó János", "Dömötör Tekla (feld.)", "Dt", "Dümmerth Dezső", "Eötvös Károly", "Erdélyi István", "Fa", "Falus Róbert", "Farkas Árpád", "Farkas Zsuzsa", "Fekete", "Fekete István", "Ferenczes István (vál. és szerk.)", "Ferenczi", "Fodor István", "France, Anatole", "Freud, Sigmund", "Fülep", "Gaál Gábor", "Galsworthy, John", "Geréb György  - Popper Péter", "Gergely András", "Gide, André", "Glasenapp, Helmuth von", "Glatz Ferenc (összeáll., szerk.)", "Gopcsa Katalin", "Gordimer, Nadine", "Granasztói György", "Granasztói Pál", "gróf Nádasdy Borbála", "Gundel Károly, Gundel Imre", "Györffy György (sajtó alá rend.)", "H. Tóth I", "Haller", "Hámori József", "Hankiss Ágnes", "Hankiss Elemér", "Harsányi Zsolt", "Hársing Lajos", "Hegedüs Géza", "Hegedűs Géza", "Hegyi Klára", "Heidenstam, Verner von", "Herman Ottó", "Hessky Orsolya", "Hevesi Lajos", "Hirschhaut, Jaclyn - Floodgate, Lauren", "Hitler, Adolf", "Honti", "Horváth Béla", "Hughes, Spike", "Huizinga, Johan", "Ignácz Rózsa", "II. Rákóczi Ferenc", "Illyés Gyula", "Jászi Oszkár", "Jókai Mór", "Jones, Ernest", "József Attila", "Kaffka Margit", "Kányádi Sándor", "Karádi Ilona (szerk.)", "Karinthy Frigyes", "Katona", "Kemény", "Kende János", "Kertész Iván (szerk.)", "Kipling, Rudyard", "Kisch, Egon Ervin", "Kisfaludy Katalin", "Kiss Gábor", "Kiss Gábor (szerk.)", "Kolozsvári Grandpierre Emil", "KomoróczyKomlós", "Konrád György", "Kósa L", "Kosáry Dopmokos", "Kosztolányi", "Kovács Ágnes", "Kovalovszky", "Köpeczi", "Kövér György", "Kratochwill Mimi", "Kristó György", "Kristó Gyula", "Kriza János", "Kriza János (szerk.)", "Kroó György", "Krúdy Gyula", "Kulcsár Péter", "Kulcsár Zsuzsanna", "Kun Erzsébet", "Kunszabó Ferenc", "Kunszt E", "Kurth, Hanns", "Lábos", "Lackó Mihály", "Lagerlöf, Selma", "László Gy", "Lázár Béla", "Le Goff, Jacques", "Lengyel Anna  (felv.)", "Lewis, Sinclair", "Litván Gy", "Litván György", "Löpöczi Rózsa", "Ludassy", "Lukács", "Lükő Gábor", "Major Iván", "Marosvölgyi Gábor", "Marx", "Matthews, D. (szerk.)", "Mauriac, Francois", "Mentovich", "Merényi László", "Merezskovszkij, Dmitrij", "Miele, Philip és Silva, José", "Mikes Kelemen", "Miskolczi Miklós", "Mochár Szilvia", "Moldova György", "Molnár Ernő (az eredeti Talmud szöveg alapján", "Molnár Imre", "Molnos Péter", "Nádori Attila (főszerk.)", "Nagy L", "Nagy Lajos", "Óe Kenzaburó", "Oldal Gábor", "Ormay Imre", "Ormós Zsolt", "P. Ábrahám Ernő", "Paget, John", "Palisca, Claude V.", "Paneth Gábor", "Pataki Gábor", "Perjés", "Péter Katalin", "Pilinszky János", "Pinter, Harold", "Pirandello, Luigi", "Pleszviny Edit", "Popper Péter", "Radnóti", "Ránki György", "Ransanus, Petrua", "Rappl, Erich", "Rásonyi László", "Repiszky Tamás, Szörényi Levente (vál., szerk.)", "Révész Emese", "Reymont, Wladyslaw", "Romhányi József", "Romsics Ignác", "Rózsa Gyula", "Rum Attila", "Saint-Exupéry, Consuelo de", "Sármány-Parsons Ilona", "Schlett István", "Seibert, Jutta (szerk.)", "Sienkiewicz, Henryk", "Singer, Isaac Bashevis", "Sinkovits Péter", "Solohov, Mihail", "Solténszky Tibor (szerk.)", "Somogyi Éva", "Spira György", "Steinert Ágota (vál., szerk., sajtó alá rend., jegyzetek", "Surányi Miklós", "Szabad György (összeáll.)", "Szabó László", "Szabolcsi Bence", "Szakály Ferenc", "Száraz György", "Szász Béla", "Szász Zoltán", "Széchenyi István", "Székely János", "Székely Mózes (Daday Lóránd)", "Szent-Györgyi Albert", "Szepesi Attila", "Szigethy Gábor (vál., szerk., előszó és jegyzetek)", "Szilágyi Ákos", "Szlávik Tamás (szerk.)", "Szombathy Viktor (szerk.)", "Szondi Lipót", "Szőnyi Gy.e.", "Szvoboda Dománszky Gabriella", "Tamási Áron", "Teke Zsuzsa", "Terts I", "Till Géza", "Tolnai", "Tormay Cécile", "Tóth Béla", "Tóth István", "Török Sándor", "Trencsényi-Waldapfel Imre", "Újváry Zsuzsanna", "Vajda György Mihály (szerk.)", "Vajda János", "Várady Géza", "Varga", "Vekerdi József (vál. és ford.)", "Venesz József", "Vígh Károly", "Wesselényi", "Yeats, William Butler", "Zolnay László", "Zsögödi Nagy Imre")

  def main(args: Array[String]): Unit = {

    //    Log.activate
    val frame = new JFrame
    val panel = new JPanel(new FormLayout("p:g", "p,p,p"))

    val before = new JTextField("before")
    panel.add(before, CC.xy(1, 1))
    before.setName(NAME_BEFORE)

    val ac = new JTextFieldAutocomplete
    ac.setHintText("default")
    ac.setName(NAME_AUTOCOMPLETE)
    //    ac.setValues(List("a", "ba", "", "ca", "da", null, "ea", "", "aa"))
    ac.setValues(bigList)
    panel.add(ac, CC.xy(1, 2))

    val after = new JTextField("after")
    panel.add(after, CC.xy(1, 3))
    after.setName(NAME_AFTER)

    frame.add(panel)
    frame.pack
    frame.setVisible(true)
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    frame.setTitle(NAME_FRAME)
    frame.setName(NAME_FRAME)

    ac.requestFocus
  }
}