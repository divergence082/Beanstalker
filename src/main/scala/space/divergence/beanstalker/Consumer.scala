package space.divergence.beanstalker

import java.util.concurrent.atomic.AtomicBoolean
import org.slf4j.LoggerFactory
import com.dinstone.beanstalkc.JobConsumer


class Consumer(consumer: JobConsumer, process: MessageProcessor) extends Runnable {
  private val _logger = LoggerFactory.getLogger(this.getClass)
  private val _isActive = new AtomicBoolean(true)

  def run(): Unit =
    try {
      while (_isActive.get) {
        Option(consumer.reserveJob(0)) match {
          case Some(job) =>
            consumer.deleteJob(job.getId)
            process(Message.fromJob(job.getData))
          case None => ()
        }
      }
    } catch {
      case e: Throwable => _logger.error(e.getMessage)
    }

  def close(): Unit = {
    _logger.info("(close)")
    _isActive.set(false)
    consumer.close()
  }
}
