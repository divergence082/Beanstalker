package com.github.div082.beanstalker

import java.util.Base64
import scala.util.Random
import net.liftweb.json


/**
 * Encoders and Decoders
 */
object Codec {

  implicit val formats = json.DefaultFormats
  private val _idLength = 10


  /**
   * @param data Base64 Array[Byte] Message
   * @return Message
   */
  def decodeMessage(data: Array[Byte]): Message = decodeMessage(new String(data))

  /**
   * @param data Base64 string Message
   * @return Message
   */
  def decodeMessage(data: String): Message =
    try {
      json.parse(data).extract[Message]
    } catch {
      case e: Exception => Message(Random.alphanumeric.take(_idLength).mkString, data)
    }

  /**
   * @param message Message
   * @return String Message
   */
  def messageToString(message: Message): String =
    json.compact(json.render(json.Extraction.decompose(message)))

  /**
   * @param message Message
   * @return Array[Byte] Message
   */
  def messageToBytes(message: Message): Array[Byte] = messageToString(message).getBytes

  /**
   * @param bytes Base64 bytes
   * @return Decoded Base64 bytes
   */
  def decodeBase64(bytes: Array[Byte]): Array[Byte] = Base64.getDecoder.decode(bytes)

  /**
   * @param bytes Raw bytes
   * @return Encoded Base64 bytes
   */
  def encodeBase64(bytes: Array[Byte]): Array[Byte] = Base64.getEncoder.encode(bytes)

}
