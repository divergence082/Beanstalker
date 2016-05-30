package com.divergence.beanstalker

import com.dinstone.beanstalkc.JobProducer
import org.slf4j.LoggerFactory


class Producer(producer: JobProducer) {

  private val _logger = LoggerFactory.getLogger(this.getClass)

  def put(message: Message): Unit =
    try {
      producer.putJob(0, 0, 0, message.toJob)
    } catch {
      case e: Throwable => _logger.error(e.getMessage)
    }

  def close(): Unit =
    producer.close()
}
