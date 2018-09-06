package eu.eyan.testutil.video

import java.awt.Component

import org.junit.internal.runners.statements.InvokeMethod
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod

import VideoRunner.componentToRecord
import VideoRunner.{ componentToRecord_= => componentToRecord_= }
import VideoRunner.testVideoCounter
import VideoRunner.{ testVideoCounter_= => testVideoCounter_= }
import VideoRunner.fullScreenToRecord

object VideoRunner {
  private var testVideoCounter = 1
  private var componentToRecord: Component = null
  private var fullScreenToRecord = false
  def setComponentToRecord(componentToRecord: Component) = VideoRunner.componentToRecord = componentToRecord
  def setFullScreenToRecord = fullScreenToRecord = true
}

class VideoRunner(klass: Class[_]) extends BlockJUnit4ClassRunner(klass) {

  override def methodInvoker(method: FrameworkMethod, test: Object) = {
    val testName = test.getClass().getSimpleName() + '.' + method.getName()

    new InvokeMethod(method, test) {
      override def evaluate() = {
        if (componentToRecord == null && !fullScreenToRecord) {
          throw new Exception(
            "Component to record not found. Please set it in the @Before method for the test: "
              + testName
              + "\r\nFor Example: " + classOf[VideoRunner].getSimpleName()
              + ".setComponentToRecord(component);");
        }
        val videoRecorder = new VideoRecorder
        try {
          videoRecorder.start(fullScreenToRecord, componentToRecord, "FailedTestVideos", testVideoCounter + "_" + testName)
          super.evaluate
          videoRecorder.stopAndDeleteVideo
        } catch {
          case t: Throwable => {
            videoRecorder.stopAndSaveVideo
            testVideoCounter += 1
            throw t
          }
        } finally {
          componentToRecord = null
          fullScreenToRecord = false
        }
      }
    }
  }

  override def validateTestMethods(errors: java.util.List[Throwable]) = {}
  override def validateInstanceMethods(errors: java.util.List[Throwable]) = {}
  override def collectInitializationErrors(errors: java.util.List[Throwable]) = {}
}