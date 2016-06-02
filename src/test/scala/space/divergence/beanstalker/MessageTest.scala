package space.divergence.beanstalker

import org.scalatest.FunSuite
import space.divergence.beanstalker.codec.Bytes


class MessageTest extends FunSuite {

  test("Message.getBytes") {
    val id = "1234567890"
    val data = "mydata"
    val message = Message(id, Bytes.decodeString(data))
    val result = message.getBytes
    val expected = Bytes.decodeString(id ++ data)

    assert(result.sameElements(expected))
  }

  test("Message.toJob -> Message.fromJob") {
    val id = "1234567890"
    val data = "mydata"
    val message = Message(id, Bytes.decodeString(data))
    val job = message.toJob

    val result = Message.fromJob(job)

    assert(message.id == result.id, "message.id")
    assert(message.data.sameElements(result.data), "message.data")
  }

  test("Message.apply") {
    val data = Bytes.decodeString("mydata")
    val message = Message(data)

    assert(message.data == data)
  }
}
