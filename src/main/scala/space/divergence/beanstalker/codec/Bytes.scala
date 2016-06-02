package space.divergence.beanstalker.codec


object Bytes {

  def encodeString(bytes: Array[Byte], encoding: String = "UTF-8"): String =
    new String(bytes, encoding)

  def decodeString(string: String, encoding: String = "UTF-8"): Array[Byte] =
    string.getBytes(encoding)
}
