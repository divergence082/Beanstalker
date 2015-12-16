package com.github.div082.beanstalker

import com.dinstone.beanstalkc.{BeanstalkClientFactory, Configuration}
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Application obj
 */
object Application extends App {

  val beansConfig = new Configuration()
  beansConfig.setServiceHost("127.0.0.1")
  beansConfig.setServicePort(11300)
  beansConfig.setLong(Configuration.OPERATION_TIMEOUT, 1L)
  val beansFactory = new BeanstalkClientFactory(beansConfig)

  val client = Client("request-tube", "response-tube", beansFactory)
  val server = Server("request-tube", "response-tube", beansFactory, onRequest)

  /**
   * @param data Request data
   * @return Response data
   */
  def onRequest(data: String): String = {
    println("REQUEST: " + data)
    data
  }

  val tasks = for (i <- 0 to 10) yield client.send("message:" + i)

  for (task <- tasks) {
    task.onSuccess {
      case data: String => println("RESPONSE: " + data)
    }
  }

}
