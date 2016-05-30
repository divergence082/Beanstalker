package com.divergence

import scala.concurrent.Future

package object beanstalker {
  type ByteArray = Array[Byte]
  type MessageProcessor = (Message) => Unit
  type ByteArrayProcessor = (Array[Byte]) => Future[Array[Byte]]



}
