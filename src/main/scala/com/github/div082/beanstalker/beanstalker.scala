package com.github.div082


/**
 * com.github.div082.beanstalker helpers, constants and types
 */
package object beanstalker {
  type Processor = (String) => String
  type MessageProcessor = (Message) => Unit

  /**
   * @param e Error
   * @param where Method thrown error
   */
  def handleError(e: Throwable, where: String = ""): Unit =
    System.err.println("ERROR: " + where + ": " + e.getMessage)
}
