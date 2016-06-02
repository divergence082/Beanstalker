package space.divergence.beanstalker

import java.util.concurrent.atomic.AtomicBoolean
import org.slf4j.LoggerFactory
import com.dinstone.beanstalkc.JobProducer


class Producer(producer: JobProducer) {
  private val _logger = LoggerFactory.getLogger(this.getClass)
  private val _isActive = new AtomicBoolean(true)

  def put(message: Message): Unit =
    try {
      if (_isActive.get) {
        producer.putJob(0, 0, 0, message.toJob)
      } else {
        _logger.warn("producer connection is closed")
      }
    } catch {
      case e: Throwable => _logger.error(e.getMessage)
    }

  def close(): Unit = {
    _logger.info("(close)")
    _isActive.set(false)
    producer.close()
  }
}
