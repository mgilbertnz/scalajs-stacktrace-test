package com.stacktrace.test.client

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("ApplicationMain")
object ApplicationMain {

  def main(args: Array[String]): Unit =
    startApp

  private def startApp(): Unit =
    try {
      funcThatGoesBoom
    } catch {
      case th: Throwable =>
        th.printStackTrace()
    }

  private def funcThatGoesBoom() =
    throw new RuntimeException("Test Exception 123")

}
