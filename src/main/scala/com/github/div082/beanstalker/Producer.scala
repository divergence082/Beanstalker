package com.github.div082.beanstalker

import com.dinstone.beanstalkc.JobProducer


/**
 * @param producer Beanstalkd Job Producer
 */
class Producer(producer: JobProducer) {

  /**
   * @param message Sending Message
   */
  def put(message: Message): Unit = {
    try {
      producer.putJob(0, 0, 0, Codec.encodeBase64(message.getBytes))
    } catch {
      case e: Throwable => handleError(e, "com.github.div082.beanstalker.Producer")
    }
  }
}
