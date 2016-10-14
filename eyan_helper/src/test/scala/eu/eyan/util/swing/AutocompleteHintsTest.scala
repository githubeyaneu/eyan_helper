package eu.eyan.util.swing

import org.fest.assertions.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import eu.eyan.testutil.ScalaEclipseJunitRunner
import org.fest.assertions.Assertions._
import eu.eyan.util.random.RandomPlus

@RunWith(classOf[ScalaEclipseJunitRunner])
class AutocompleteHintsTest {

  val autocompleteHints = new AutocompleteHints
  val bigList = List("", "(Kondor Katalin)", "Ádám György", "Ady", "Ambrus András", "Ambrus Zoltán", "András László", "Antal László", "Apáczai Csere János", "Apor", "Apor Péter", "Arany", "Asturias, Miguel Ángel", "Babits", "Bajkay Éva", "Bakay Kornél", "Bakó Zsuzsanna", "Baktay Ervin (feld.)", "Balás Gábor", "Balás Gábor (szerk.)", "Bánkúti Imre", "Baranyi Ferenc", "Bárczy János", "Barna István (Összeáll.)", "Barta Gábor", "Barta János", "Bartha János", "Bartha Lajos (szerk.)", "Beke Kata", "Bellow, Saul", "Benedek Elek", "Benedek Elek (mesélte)", "Benedek Elek (szerk.)", "Bényei Miklós", "Bernáth Aurél", "Berzsenyi", "Bethlen", "Bitskey István", "Bizzer István", "Björnson, Björnstjerne", "Boll, Franz - Bezold, Carl", "Bolyai", "Boreczky Beratrix", "Boronkay Iván (feld.)", "Böll, Heinrich", "Börne", "Bözödi György", "Bródy Sándor", "Cela, Canmilo José", "Cholnoky Viktor", "Churchill, Winston Sir", "Czakó Gábor", "Czeizel", "Csapody Csaba", "Csapodyné", "Csiffáry Gabriella (vál.)", "Csomor Lajos", "Csurka István", "Darvas Gábor", "Deledda, Grazia", "Déry Tibor", "Devecseri Gábor", "Dornyay Béla, Vigyázó János", "Dömötör Tekla (feld.)", "Dt", "Dümmerth Dezső", "Eötvös Károly", "Erdélyi István", "Fa", "Falus Róbert", "Farkas Árpád", "Farkas Zsuzsa", "Fekete", "Fekete István", "Ferenczes István (vál. és szerk.)", "Ferenczi", "Fodor István", "France, Anatole", "Freud, Sigmund", "Fülep", "Gaál Gábor", "Galsworthy, John", "Geréb György  - Popper Péter", "Gergely András", "Gide, André", "Glasenapp, Helmuth von", "Glatz Ferenc (összeáll., szerk.)", "Gopcsa Katalin", "Gordimer, Nadine", "Granasztói György", "Granasztói Pál", "gróf Nádasdy Borbála", "Gundel Károly, Gundel Imre", "Györffy György (sajtó alá rend.)", "H. Tóth I", "Haller", "Hámori József", "Hankiss Ágnes", "Hankiss Elemér", "Harsányi Zsolt", "Hársing Lajos", "Hegedüs Géza", "Hegedűs Géza", "Hegyi Klára", "Heidenstam, Verner von", "Herman Ottó", "Hessky Orsolya", "Hevesi Lajos", "Hirschhaut, Jaclyn - Floodgate, Lauren", "Hitler, Adolf", "Honti", "Horváth Béla", "Hughes, Spike", "Huizinga, Johan", "Ignácz Rózsa", "II. Rákóczi Ferenc", "Illyés Gyula", "Jászi Oszkár", "Jókai Mór", "Jones, Ernest", "József Attila", "Kaffka Margit", "Kányádi Sándor", "Karádi Ilona (szerk.)", "Karinthy Frigyes", "Katona", "Kemény", "Kende János", "Kertész Iván (szerk.)", "Kipling, Rudyard", "Kisch, Egon Ervin", "Kisfaludy Katalin", "Kiss Gábor", "Kiss Gábor (szerk.)", "Kolozsvári Grandpierre Emil", "KomoróczyKomlós", "Konrád György", "Kósa L", "Kosáry Dopmokos", "Kosztolányi", "Kovács Ágnes", "Kovalovszky", "Köpeczi", "Kövér György", "Kratochwill Mimi", "Kristó György", "Kristó Gyula", "Kriza János", "Kriza János (szerk.)", "Kroó György", "Krúdy Gyula", "Kulcsár Péter", "Kulcsár Zsuzsanna", "Kun Erzsébet", "Kunszabó Ferenc", "Kunszt E", "Kurth, Hanns", "Lábos", "Lackó Mihály", "Lagerlöf, Selma", "László Gy", "Lázár Béla", "Le Goff, Jacques", "Lengyel Anna  (felv.)", "Lewis, Sinclair", "Litván Gy", "Litván György", "Löpöczi Rózsa", "Ludassy", "Lukács", "Lükő Gábor", "Major Iván", "Marosvölgyi Gábor", "Marx", "Matthews, D. (szerk.)", "Mauriac, Francois", "Mentovich", "Merényi László", "Merezskovszkij, Dmitrij", "Miele, Philip és Silva, José", "Mikes Kelemen", "Miskolczi Miklós", "Mochár Szilvia", "Moldova György", "Molnár Ernő (az eredeti Talmud szöveg alapján", "Molnár Imre", "Molnos Péter", "Nádori Attila (főszerk.)", "Nagy L", "Nagy Lajos", "Óe Kenzaburó", "Oldal Gábor", "Ormay Imre", "Ormós Zsolt", "P. Ábrahám Ernő", "Paget, John", "Palisca, Claude V.", "Paneth Gábor", "Pataki Gábor", "Perjés", "Péter Katalin", "Pilinszky János", "Pinter, Harold", "Pirandello, Luigi", "Pleszviny Edit", "Popper Péter", "Radnóti", "Ránki György", "Ransanus, Petrua", "Rappl, Erich", "Rásonyi László", "Repiszky Tamás, Szörényi Levente (vál., szerk.)", "Révész Emese", "Reymont, Wladyslaw", "Romhányi József", "Romsics Ignác", "Rózsa Gyula", "Rum Attila", "Saint-Exupéry, Consuelo de", "Sármány-Parsons Ilona", "Schlett István", "Seibert, Jutta (szerk.)", "Sienkiewicz, Henryk", "Singer, Isaac Bashevis", "Sinkovits Péter", "Solohov, Mihail", "Solténszky Tibor (szerk.)", "Somogyi Éva", "Spira György", "Steinert Ágota (vál., szerk., sajtó alá rend., jegyzetek", "Surányi Miklós", "Szabad György (összeáll.)", "Szabó László", "Szabolcsi Bence", "Szakály Ferenc", "Száraz György", "Szász Béla", "Szász Zoltán", "Széchenyi István", "Székely János", "Székely Mózes (Daday Lóránd)", "Szent-Györgyi Albert", "Szepesi Attila", "Szigethy Gábor (vál., szerk., előszó és jegyzetek)", "Szilágyi Ákos", "Szlávik Tamás (szerk.)", "Szombathy Viktor (szerk.)", "Szondi Lipót", "Szőnyi Gy.e.", "Szvoboda Dománszky Gabriella", "Tamási Áron", "Teke Zsuzsa", "Terts I", "Till Géza", "Tolnai", "Tormay Cécile", "Tóth Béla", "Tóth István", "Török Sándor", "Trencsényi-Waldapfel Imre", "Újváry Zsuzsanna", "Vajda György Mihály (szerk.)", "Vajda János", "Várady Géza", "Varga", "Vekerdi József (vál. és ford.)", "Venesz József", "Vígh Károly", "Wesselényi", "Yeats, William Butler", "Zolnay László", "Zsögödi Nagy Imre")

  @Before def setUp = {}

  @After def tearDown() = {}

  @Test
  def sortAlgo = {
    assertThat(new AutocompleteHints().sortAlgo("dé").apply("Déry", "Déry")).isFalse
    assertThat(new AutocompleteHints().sortAlgo("dé").apply("Déry", "Dery")).isTrue
    assertThat(new AutocompleteHints().sortAlgo("dé").apply("Dery", "Déry")).isFalse
    assertThat(new AutocompleteHints().sortAlgo("dé").apply("Dery", "Dery")).isFalse

    assertThat(new AutocompleteHints().sortAlgo("de").apply("Déry", "Déry")).isFalse
    assertThat(new AutocompleteHints().sortAlgo("de").apply("Déry", "eDery")).isTrue
    assertThat(new AutocompleteHints().sortAlgo("de").apply("eDery", "Déry")).isFalse
    assertThat(new AutocompleteHints().sortAlgo("de").apply("eDery", "eDery")).isFalse

    assertThat(new AutocompleteHints().sortAlgo("de").apply("ederi", "ederi")).isFalse
    assertThat(new AutocompleteHints().sortAlgo("de").apply("ederi", "edri")).isTrue
    assertThat(new AutocompleteHints().sortAlgo("de").apply("edri", "ederi")).isFalse
    assertThat(new AutocompleteHints().sortAlgo("de").apply("edri", "edri")).isFalse
  }
}