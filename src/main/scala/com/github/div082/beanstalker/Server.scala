package com.github.div082.beanstalker

import scala.concurrent.ExecutionContext.Implicits.global
import com.dinstone.beanstalkc.{BeanstalkClientFactory, JobConsumer, JobProducer}


/**
 * @param beansProducer Beanstalkd Job Producer
 * @param beansConsumer Beanstalkd Job Consumer
 * @param processor Content Processor
 */
class Server(beansProducer: JobProducer,
             beansConsumer: JobConsumer,
             processor: Processor) {
  private val _consumer = new Consumer(beansConsumer, _processRequest)
  private val _producer = new Producer(beansProducer)
  new Thread(_consumer).start()

  /**
   * @param request Request Message
   */
  private def _processRequest(request: Message): Unit =
    processor(request.data).map {
      case response: String => _producer.put(Message(request.id, response))
    }

}


/**
 * Beanstalkd Server Builder
 */
object Server {

  /**
   * @param requestTube Tube for requests
   * @param responseTube Tube for responses
   * @param beansFactory Beanstalkd Client Factory
   * @param processor Requests processor
   * @return Beanstalkd Server
   */
  def apply(requestTube: String,
            responseTube: String,
            beansFactory: BeanstalkClientFactory,
            processor: Processor): Server =
    new Server(
      beansFactory.createJobProducer(responseTube),
      beansFactory.createJobConsumer(requestTube), processor)

}
