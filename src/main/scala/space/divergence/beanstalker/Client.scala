package space.divergence.beanstalker

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.{Future, Promise}
import org.slf4j.LoggerFactory
import com.dinstone.beanstalkc.{BeanstalkClientFactory, Configuration, JobConsumer, JobProducer}


class Client(consumer: JobConsumer, producer: JobProducer) {
  private val _logger = LoggerFactory.getLogger(this.getClass)
  private val _registry = new ConcurrentHashMap[String, Promise[Array[Byte]]]()
  private val _consumer = new Consumer(consumer, _processResponse)
  private val _producer = new Producer(producer)
  private val _consumerThreadName = s"client-consumer-${UUID.randomUUID().toString}"
  private val _consumerThread = new Thread(_consumer, _consumerThreadName)
  _consumerThread.start()

  private def _processResponse(response: Message): Unit =
    try {
      _logger.trace(s"response (${response.id}) received")

      Option(_registry.remove(response.id)) match {
        case Some(p) => p.success(response.data)
        case None =>
          _logger.warn(s"no requests for response (${response.id})")
          ()
      }
    } catch {
      case e: Throwable => _logger.error(e.getMessage)
    }

  def send(data: Array[Byte]): Future[Array[Byte]] = {
    val p = Promise[Array[Byte]]()
    val request = Message(data)
    _producer.put(request)
    _registry.put(request.id, p)

    _logger.trace(s"request (${request.id}) sent")
    p.future
  }

  def close(): Unit = {
    _logger.info("(close)")
    _producer.close()
    _consumer.close()
    _logger.info("(interrupt)")
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
