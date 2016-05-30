package com.divergence.beanstalker

import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.{Future, Promise}
import org.slf4j.LoggerFactory
import com.dinstone.beanstalkc.{BeanstalkClientFactory, Configuration, JobConsumer, JobProducer}


class Client(consumer: JobConsumer, producer: JobProducer) {
  type Handler = (String) => Unit

  private val _logger = LoggerFactory.getLogger(this.getClass)
  private val _registry = new ConcurrentHashMap[Array[Byte], Promise[Message]]()
  private val _consumer = new Consumer(consumer, _processResponse)
  private val _producer = new Producer(producer)
  private val _consumerThread = new Thread(_consumer)
  _consumerThread.start()

  private def _processResponse(response: Message): Unit =
    try {
      Option(_registry.remove(response.id)) match {
        case Some(p: Promise[Message]) => p.success(response)
        case None => ()
      }
    } catch {
      case e: Throwable => _logger.error(e.getMessage)
    }

  def send(data: Array[Byte]): Future[Message] = {
    val p = Promise[Message]()
    val request = Message.fromBytes(data)
    _producer.put(request)
    _registry.put(request.id, p)
    p.future
  }

  def close(): Unit = {
    _producer.close()
    _consumer.close()
    _consumerThread.interrupt()
  }
}


object Client {
  def apply(requestTube: String,
            responseTube: String,
            host: String,
            port: Int): Client = {
    val beansConfig = new Configuration()
    beansConfig.setServiceHost(host)
    beansConfig.setServicePort(port)
    val beansFactory = new BeanstalkClientFactory(beansConfig)

    new Client(
      beansFactory.createJobConsumer(responseTube),
      beansFactory.createJobProducer(requestTube))
  }
}