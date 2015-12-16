# Beanstalker
Scala Beanstalkd client-server library

- BeanstalkClientFactory
```
val beansConfig = new Configuration()                       // Configure factory
beansConfig.setServiceHost("127.0.0.1")
beansConfig.setServicePort(11300)
beansConfig.setLong(Configuration.OPERATION_TIMEOUT, 1L)

val beansFactory = new BeanstalkClientFactory(beansConfig)  // Create factory
  
```

- Client
```
val client = Client(requestTube = "request-tube", 
                    responseTube = "response-tube", 
                    beansFactory = beansFactory)            // Create client

client.send("message")                                      // Send request
  .onSuccess {                                              // Handle response
    case data: String => println("Response: " + data)   
  }
```

- Server
```
/**
 * @param data Request data
 * @return Response data
 */
def onRequest(data: String): Future[String] = {                     // Request handler
  println("Request: " + data)
  Future {"Response: " + data}
}

val server = Server(requestTube = "request-tube", 
                    responseTube = "response-tube", 
                    beansFactory = beansFactory, 
                    processor = onRequest)                  // Create Server
```
