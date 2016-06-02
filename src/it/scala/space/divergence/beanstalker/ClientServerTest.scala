package space.divergence.beanstalker

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory
import org.scalatest.{fixture, Outcome}
import org.scalatest.time.SpanSugar._
import org.scalatest.concurrent.Waiters
import space.divergence.beanstalker.codec.Bytes


class ClientServerTest extends fixture.FunSuite with Waiters {
  val logger = LoggerFactory.getLogger(this.getClass)
  Thread.currentThread.setName("client-server-test")

  case class FixtureParam(client: Client, server: Server, load: Int, timeToProcess: Long)

  override def withFixture(test: OneArgTest): Outcome = {
    val requestTube = test.configMap.getRequired[String]("reqTube")
    val responseTube = test.configMap.getRequired[String]("resTube")
    val host = test.configMap.getRequired[String]("host")
    val port = test.configMap.getRequired[String]("port").toInt

    val client = Client(requestTube, responseTube, host, port)
    val server = Server(requestTube, responseTube, host, port, increment)

    try {
      test(
        FixtureParam(client, server,
          test.configMap.getRequired[String]("load").toInt,
          test.configMap.getRequired[String]("ttp").toLong))
    } finally {
      client.close()
      server.close()
    }
  }

  def increment(data: Array[Byte]): Future[Array[Byte]] =
    Future {
      val in = Bytes.encodeString(data).toInt
      logger.info(s"(process) $in")
      Bytes.decodeString((in + 1).toString)
    }



  test("All sent data should be processed correctly") { f =>
    val w = new Waiter
    val passed = 0.until(f.load)

    passed.foreach { i =>
      logger.info(s"send = $i")

      f.client.send(i.toString.getBytes)
        .map { response =>
          val received = Bytes.encodeString(response).toInt
          logger.info(s"sent = $i, received = $received")

          assert(received == i + 1)
          w.dismiss()
        }
    }

    w.await(timeout((f.timeToProcess * f.load).millis), dismissals(f.load))
  }
}
