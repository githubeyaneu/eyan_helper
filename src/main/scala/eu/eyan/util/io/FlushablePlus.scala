package eu.eyan.util.io

import java.io.Flushable
import java.util.TimerTask
import java.util.Timer
import java.io.OutputStream
import java.io.Closeable

object FlushablePlus {
  implicit class OutputStreamImplicit[FLUSHABLE <: Flushable](stream: FLUSHABLE) {
  }
}