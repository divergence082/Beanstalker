package com.divergence.beanstalker.codec

import java.util.{Base64 => base64}


object Base64 {
  private val _encoder = base64.getEncoder
  private val _decoder = base64.getDecoder

  def encode(data: Array[Byte]): Array[Byte] = _encoder.encode(data)
  def decode(data: Array[Byte]): Array[Byte] = _decoder.decode(data)
}
