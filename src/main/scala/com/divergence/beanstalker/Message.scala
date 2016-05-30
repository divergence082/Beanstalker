package com.divergence.beanstalker

import scala.util.Random
import com.divergence.beanstalker.codec.{GZip, Base64}


case class Message(id: Array[Byte], data: Array[Byte]) {
  def getBytes: Array[Byte] = id ++ data
  def toJob: Array[Byte] = GZip.compress(Base64.encode(getBytes))
}


object Message {
  private val _idLength = 10

  private def _generateId: Array[Byte] =
    Random.alphanumeric.take(_idLength).mkString.getBytes

  def fromBytes(data: Array[Byte]): Message =
    Message(_generateId, data)

  def fromJob(job: Array[Byte]): Message =
    Message.apply _ tupled Base64.decode(GZip.decompress(job)).splitAt(_idLength)
}
