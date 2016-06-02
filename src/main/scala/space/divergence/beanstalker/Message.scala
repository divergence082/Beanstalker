package space.divergence.beanstalker

import scala.util.Random
import space.divergence.beanstalker.codec.{Bytes, GZip, Base64}


case class Message(id: String, data: Array[Byte]) {
  def getBytes: Array[Byte] = Bytes.decodeString(id) ++ data
  def toJob: Array[Byte] = GZip.compress(Base64.encode(getBytes))
}


object Message {
  private val _idLength = 10

  private def _generateId: String =
    Random.alphanumeric.take(_idLength).mkString

  def apply(data: Array[Byte]): Message =
    Message(_generateId, data)

  def fromJob(job: Array[Byte]): Message = {
    val (id, data) = Base64.decode(GZip.decompress(job)).splitAt(_idLength)
    Message(Bytes.encodeString(id), data)
  }
}
