package com.divergence.beanstalker

import com.dinstone.beanstalkc.{BeanstalkClientFactory, Configuration, JobConsumer, JobProducer}


class Server(consumer: JobConsumer,
             producer: JobProducer,
             processor: ByteArrayProcessor) {

  private val _producer = new Producer(producer)
  private val _consumer = new Consumer(consumer, _processRequest)
  private val _consumerThread = new Thread(_consumer)
  _consumerThread.start()

  private def _processRequest(request: Message): Unit =
    processor(request.data).map {
      case response: Array[Byte] => _producer.put(Message(request.id, response))
    }

  def close(): Unit = {
    _consumer.close()
    _producer.close()
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