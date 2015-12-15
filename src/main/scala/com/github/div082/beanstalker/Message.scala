package com.github.div082.beanstalker


/**
 * @param id Message identifier
 * @param data Sending data
 */
case class Message(id: String, data: String) {

  /**
   * @return Message as Array[Byte]
   */
  def getBytes: Array[Byte] = Codec.messageToBytes(this)

  /**
   * @return Message as String
   */
  override def toString: String = Codec.messageToString(this)
}
