package com.github.div082.beanstalker

import com.dinstone.beanstalkc.JobConsumer


/**
 * @param consumer Beanstalkd Consumer
 * @param processor Message processor
 */
class Consumer(consumer: JobConsumer, processor: MessageProcessor) extends Runnable {

  /**
   * Beanstalkd queue item handler
   */
  private def _next(): Unit = {
    try {
      val job = consumer.reserveJob(0)

      if (job != null) {
        val message = Codec.decodeMessage(Codec.decodeBase64(job.getData))
        consumer.deleteJob(job.getId)
        processor(message)
      }
    } catch {
      case e: Throwable => handleError(e, "com.github.div082.beanstalker.Consumer")
    }

    _next()
  }

  /**
   * Thread starting method
   */
  def run(): Unit = {
    _next()
  }
}