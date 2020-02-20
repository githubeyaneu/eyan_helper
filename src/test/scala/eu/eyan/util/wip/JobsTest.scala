package eu.eyan.util.wip

import eu.eyan.testutil.TestPlus
import org.junit.runner.RunWith
import org.junit.Test
import eu.eyan.testutil.ScalaEclipseJunitRunner
import rx.Observable
import eu.eyan.log.Log
import org.junit.BeforeClass
import eu.eyan.util.java.lang.ThreadPlus

@RunWith(classOf[ScalaEclipseJunitRunner])
class JobsTest extends TestPlus {

  Log.activateInfoLevel
  val jobs = Jobs()
  def workInProgressTest = jobs.workInProgress.test
  def assertWorkInProgress = workInProgressTest.assertValue(true)
  def assertNoWorkInProgress = workInProgressTest.assertValue(false)

  @Test def noWorkAtBeginning = workInProgressTest.assertValue(false).assertNotCompleted()

  @Test def workInProgressAtFirstJob = jobs.run { assertWorkInProgress }

  @Test def idleAtFirstCompleted = { jobs.run {}; assertNoWorkInProgress }

  @Test def inOtherThread = {
    val workInProgress = workInProgressTest
    ThreadPlus.run {
      assertNoWorkInProgress
      jobs.run {
        assertWorkInProgress
        Thread.sleep(100)
        assertWorkInProgress
      }
      assertNoWorkInProgress
    }

    Thread.sleep(200)
    workInProgress.assertValues(false, true, false)
  }

  @Test def moreJobsParallel = {
    val workInProgress = workInProgressTest
    assertNoWorkInProgress

    val start = System.currentTimeMillis
    
    ThreadPlus.run { jobs.run {Thread.sleep(400)} }
    
    Thread.sleep(100)
    assertWorkInProgress
    ThreadPlus.run { jobs.run {Thread.sleep(100)} }
    
    Thread.sleep(100)
    assertWorkInProgress

    Thread.sleep(100)
    ThreadPlus.run { jobs.run {Thread.sleep(200)} }
    assertWorkInProgress
    
    Thread.sleep(100)
    assertWorkInProgress
    
    Thread.sleep(200)
    assertNoWorkInProgress
    
    workInProgress.assertValues(false, true, false)
  }
}