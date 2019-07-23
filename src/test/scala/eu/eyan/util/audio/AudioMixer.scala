package eu.eyan.util.audio

import javax.swing.JFrame
import eu.eyan.util.swing.JFramePlus.JFramePlusImplicit
import eu.eyan.util.swing.JPanelWithFrameLayout
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import javax.sound.sampled.AudioFormat
import java.io.ByteArrayOutputStream
import eu.eyan.util.java.lang.ThreadPlus
import eu.eyan.util.swing.SwingPlus
import eu.eyan.util.swing.JLabelPlus.JLabelImplicit
import javax.sound.sampled.SourceDataLine

object AudioMixer extends App {
  val SAMPLE_RATE = 8000.0f
  val sampleSizeInBits = 8
  val channels = 1
  val signed = true
  val bigEndian = true
  val AUDIO_FORMAT = new AudioFormat(SAMPLE_RATE, sampleSizeInBits, channels, signed, bigEndian)

  val panel = new JPanelWithFrameLayout
  panel.newColumn.addLabel("Name")
  panel.newColumn.addLabel("Ber")
  panel.newColumn.addLabel("Recording")
  panel.newColumn.addLabel("Play")

  startAudioStreams()

  private val jFrame = new JFrame()
    .name("AudioMixer")
    .title("AudioMixer")
    .iconFromChar('A')
    .onCloseExit
    .withComponent(panel)
    .packAndSetVisible
    .maximize

  def startAudioStreams() = {
    val mixers = AudioSystem.getMixerInfo
    mixers.map(m => AudioSystem getMixer m).foreach(mixer => {

      try {
        mixer.getTargetLineInfo.foreach(info => {
          println(mixer.getMixerInfo)
          println("  TargetLineInfo: " + info + ", LineClass: " + info.getLineClass)
          if (info.getLineClass == classOf[TargetDataLine]) {
            val targetDataLine = mixer.getLine(info)
            val lineInfo = targetDataLine.getLineInfo
            println("    LineInfo: " + lineInfo)
            val microphone = targetDataLine.asInstanceOf[TargetDataLine]
            microphone.getControls.foreach(control => println("    Control: " + control))

            microphone.open(AUDIO_FORMAT)
            microphone.start
            println("    Microphone: " + microphone + " bufferSize: " + microphone.getBufferSize)
            println("    Format: " + microphone.getFormat)

            panel.newRow.addLabel(mixer.getMixerInfo.toString)
            val progressBar = panel.nextColumn.addProgressBar(0, 128, "").valueChanged(0)
            val recordLabel = panel.nextColumn.addLabel("Record")
            val playLabel = panel.nextColumn.addLabel("Play")

            val record = new ByteArrayOutputStream()
            var recording = false
            recordLabel onMouseReleased {
              if (recordLabel.getText == "Record") {
                println("Start to record")
                recording = true
                record.reset
                recordLabel.text("Recording")
              } else if (recordLabel.getText == "Recording") {
                println("End to record")
                recording = false
                recordLabel.text("Record")
              }
            }

            playLabel onMouseReleased {
              val dataLineInfo = new DataLine.Info(classOf[SourceDataLine], AUDIO_FORMAT)
              val speakers = AudioSystem.getLine(dataLineInfo).asInstanceOf[SourceDataLine]
              recording = false
              recordLabel.text("Record")
              val data = record.toByteArray
              println("playing " + data.size)

              speakers.open(AUDIO_FORMAT)
              speakers.start
              speakers.write(data, 0, data.size)
              speakers.drain
              speakers.close
            }

            ThreadPlus.run {
              val start = System.currentTimeMillis
              var ct = 0
              while (true) {
                val data = Array.ofDim[Byte](microphone.getBufferSize / 8)
                val readBytes = microphone.read(data, 0, microphone.getBufferSize / 8)
                if (recording) record.write(data, 0, readBytes)
                ct += readBytes
                val time = System.currentTimeMillis - start
                SwingPlus.invokeLater {
                  if (time != 0) progressBar.setFormat((ct * 8 / time) + "kbps ")
                  progressBar.setValue(data.map(_.toInt).map(_.abs).max)
                }
              }
            }
          }
        })

      } catch {
        case t: Throwable => t.printStackTrace
      }
    })

  }
}