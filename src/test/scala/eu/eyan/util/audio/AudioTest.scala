package eu.eyan.util.audio

import java.io.ByteArrayOutputStream

import org.fourthline.cling.UpnpService
import org.fourthline.cling.UpnpServiceImpl
import org.fourthline.cling.model.message.header.STAllHeader
import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.registry.Registry
import org.fourthline.cling.registry.RegistryListener
import org.junit.Test

import eu.eyan.testutil.TestPlus
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.Mixer
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine
import eu.eyan.log.Log

class AudioTest extends TestPlus {
  var ct = 0

  def printMixer(info: Mixer.Info) = {
    println(ct + ". ")
    println("  " + info.getName)
    println("  " + info.getDescription)
    if (info.getVendor != "Unknown Vendor") println("  " + info.getVendor)
    if ("Unknown Version" != info.getVersion) println("  " + info.getVersion)
    println
    ct = ct + 1
  }

  //@Test
  def audio = {
    println(AudioSystem.getMixerInfo.groupBy(i => i.getDescription).keySet)
    AudioSystem.getMixerInfo.foreach(printMixer)

    println("AudioFileTypes:")
    AudioSystem.getAudioFileTypes.foreach(println)
    println

  }

  //@Test
  def mic = {
    val format: AudioFormat = new AudioFormat(8000.0f, 16, 1, true, true)
    try {
      val mixer = AudioSystem.getMixerInfo
      val mix4 = AudioSystem.getMixer(mixer(4))
      val info: DataLine.Info = new DataLine.Info(classOf[TargetDataLine], format)

      val microphone = AudioSystem.getLine(info).asInstanceOf[TargetDataLine]
      //      val microphone = mix4.getLine(info).asInstanceOf[TargetDataLine]

      println(microphone.getLineInfo)
      microphone.open(format)
      val out: ByteArrayOutputStream = new ByteArrayOutputStream()
      val CHUNK_SIZE: Int = 1024
      val data: Array[Byte] = Array.ofDim[Byte](microphone.getBufferSize / 5)
      microphone.start

      val dataLineInfo: DataLine.Info = new DataLine.Info(classOf[SourceDataLine], format)
      val speakers = AudioSystem.getLine(dataLineInfo).asInstanceOf[SourceDataLine]
      //      val speakers = AudioSystem.getMixer( mixer(1)).getLine(dataLineInfo).asInstanceOf[SourceDataLine]
      speakers.open(format)
      speakers.start

      var bytesRead: Int = 0
      var numBytesRead: Int = 0
      val start = System.currentTimeMillis
      while (bytesRead < 100000) {
        numBytesRead = microphone.read(data, 0, CHUNK_SIZE)
        bytesRead += numBytesRead

        print(".")
        //        println(numBytesRead)
        //        println(data.mkString(","))

        // write the mic data to a stream for use later
        out.write(data, 0, numBytesRead)
        // write mic data to stream for immediate playback
        speakers.write(data, 0, numBytesRead)
      }
      println

      val time = System.currentTimeMillis - start
      println(bytesRead + " bytes in " + time + " ms: " + (bytesRead * 1000 / time / 1024) + "kB/s")

      speakers.drain
      speakers.close
      microphone.close
    } catch {
      case e: LineUnavailableException => e.printStackTrace

    }
  }

  @Test
  def upnp() = {
    Log.activateAllLevel
    // UPnP discovery is asynchronous, we need a callback
    val listener: RegistryListener = new RegistryListener() {
      def remoteDeviceDiscoveryStarted(registry: Registry, device: RemoteDevice) = println("Discovery started: " + device.getDisplayString)
      def remoteDeviceDiscoveryFailed(registry: Registry, device: RemoteDevice, ex: Exception) = println("Discovery failed: " + device.getDisplayString + " => " + ex)
      def remoteDeviceAdded(registry: Registry, device: RemoteDevice) = println("Remote device available: " + device.getDisplayString)
      def remoteDeviceUpdated(registry: Registry, device: RemoteDevice) = println("    Remote device updated: " + device.getDisplayString)
      def remoteDeviceRemoved(registry: Registry, device: RemoteDevice) = println("Remote device removed: " + device.getDisplayString)
      def localDeviceAdded(registry: Registry, device: LocalDevice) = println("Local device added: " + device.getDisplayString)
      def localDeviceRemoved(registry: Registry, device: LocalDevice) = println("Local device removed: " + device.getDisplayString)
      def beforeShutdown(registry: Registry) = println("Before shutdown, the registry has devices: " + registry.getDevices.size)
      def afterShutdown() = println("Shutdown of registry complete!")
    }
    // This will create necessary network resources for UPnP right away
    println("Starting Cling...")
    val upnpService: UpnpService = new UpnpServiceImpl(listener)
    // Send a search message to all devices and services, they should respond soon
    upnpService.getControlPoint.search(new STAllHeader())
    // Let's wait 10 seconds for them to respond
    println("Waiting 10 seconds before shutting down...")
    Thread.sleep(10000)
    // Release all resources and advertise BYEBYE to other UPnP devices
    println("Stopping Cling...")
    upnpService.shutdown()
  }

}