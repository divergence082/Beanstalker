package com.github.div082.beanstalker

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import com.dinstone.beanstalkc.{BeanstalkClientFactory, JobConsumer, JobProducer}


/**
 * @param beansProducer Beanstalkd JobProducer
 * @param beansConsumer Beanstalkd JobConsumer
 */
class Client(beansProducer: JobProducer, beansConsumer: JobConsumer) {
  type Handler = (String) => Unit

  private val _registry = mutable.Map[String, Promise[String]]()
  private val _consumer = new Consumer(beansConsumer, _processResponse)
  private val _producer = new Producer(beansProducer)
  new Thread(_consumer).start()

  /**
   * @param response Response message
   */
  private def _processResponse(response: Message): Unit =
    _registry.remove(response.id) match {
      case Some(p: Promise[String]) => p.success(response.data)
      case None =>
        new Throwable("ERROR: com.github.div082.beanstalker.Client: Request is not registered")
    }

  /**
   * @param data Sending data
   * @return Receiving data
   */
  def send(data: String): Future[String] = {
    val p = Promise[String]()
    val request = Codec.decodeMessage(data)
    _producer.put(request)
    _registry.put(request.id, p)
    p.future
  }

}


/**
 * Beanstalkd Client Builder
 */
object Client {

  /**
   * @param requestTube Tube for requests
   * @param responseTube Tube for responses
   * @param beansFactory Beanstalkd Client Factory
   * @return Beanstalkd Client
   */
  def apply(requestTube: String,
            responseTube: String,
            beansFactory: BeanstalkClientFactory): Client =
    new Client(
      beansFactory.createJobProducer(requestTube),
      beansFactory.createJobConsumer(responseTube))

}
