package com.divergence.beanstalker

import org.slf4j.LoggerFactory
import com.dinstone.beanstalkc.JobConsumer


class Consumer(consumer: JobConsumer, process: MessageProcessor) extends Runnable {

  private val _logger = LoggerFactory.getLogger(this.getClass)

  private def _next(): Unit =
    try {
      Option(consumer.reserveJob(0)) match {
        case Some(job) =>
          consumer.deleteJob(job.getId)
          process(Message.fromJob(job.getData))
        case None => ()
      }
    } catch {
      case e: Throwable => _logger.error(e.getMessage)
    }

  def run(): Unit = {
    _next()
    run()
  }

  def close(): Unit =
    consumer.close()
}
