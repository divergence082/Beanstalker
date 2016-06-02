package space.divergence.beanstalker

import java.util.UUID
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext.Implicits.global
import com.dinstone.beanstalkc.{BeanstalkClientFactory, Configuration, JobConsumer, JobProducer}


class Server(consumer: JobConsumer, producer: JobProducer, processor: ByteArrayProcessor) {
  private val _logger = LoggerFactory.getLogger(this.getClass)
  private val _producer = new Producer(producer)
  private val _consumer = new Consumer(consumer, _processRequest)
  private val _consumerThreadName = s"server-consumer-${UUID.randomUUID().toString}"
  private val _consumerThread = new Thread(_consumer, _consumerThreadName)
  _consumerThread.start()

  private def _processRequest(request: Message): Unit = {
    _logger.trace(s"request (${request.id}) received")
    processor(request.data).map {
      case res: Array[Byte] =>
        val response = Message(request.id, res)
        _logger.trace(s"response (${response.id}) sent")
        _producer.put(response)
    }
  }

  def close(): Unit = {
    _logger.info("(close)")
    _consumer.close()
    _producer.close()
    _logger.info("(interrupt)")
    _consumerThread.interrupt()
  }
}


object Server{
  def apply(requestTube: String,
            responseTube: String,
            host: String,
            port: Int,
            processor: ByteArrayProcessor): Server = {
    val beansConfig = new Configuration()
    beansConfig.setServiceHost(host)
    beansConfig.setServicePort(port)
    val beansFactory = new BeanstalkClientFactory(beansConfig)

    new Server(
      beansFactory.createJobConsumer(requestTube),
      beansFactory.createJobProducer(responseTube),
      processor)
  }
}
