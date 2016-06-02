package space.divergence.beanstalker.codec

import java.util.zip.{GZIPInputStream, GZIPOutputStream}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}


object GZip {
  private val _bufferSize = 8192

  def compress(data: Array[Byte]): Array[Byte] = {
    val baos = new ByteArrayOutputStream
    val gzos = new GZIPOutputStream(baos)
    gzos.write(data)
    gzos.finish()
    gzos.close()
    baos.close()
    baos.toByteArray
  }

  def decompress(data: Array[Byte]): Array[Byte] = {
    val bais   = new ByteArrayInputStream(data)
    val gzis   = new GZIPInputStream(bais)
    val baos   = new ByteArrayOutputStream

    val buf    = new Array[Byte](_bufferSize)
    var read   = gzis.read(buf)

    while (read > 0) {
      baos.write(buf, 0, read)
      read = gzis.read(buf)
    }

    gzis.close()
    baos.close()
    baos.toByteArray
  }
}
