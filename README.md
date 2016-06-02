# Beanstalker
Scala Beanstalkd client-server library 
  
Sbt:
----
```
libraryDependencies += "space.divergence" % "beanstalker_2.11" % "0.0.1"
```
or
```
libraryDependencies += "space.divergence" %% "beanstalker" % "0.0.1"
```

Usage:
------
- Client
```
val client = Client(requestTube = "request-tube", 
                    responseTube = "response-tube", 
                    host = "127.0.0.1", 
                    port = 11300)                                       // Create client

client.send("message".getBytes)                                         // Send request
  .onSuccess {                                                          // Handle response
    case data: Array[Byte] => println("Response: " + new String(data))   
  }
```

- Server
```
def onRequest(data: Array[Byte]): Future[Array[Byte]] =                 // Request handler
  Future {
    println("Request: " + new String(data))
    data
  }

val server = Server(requestTube = "request-tube", 
                    responseTube = "response-tube", 
                    host = "127.0.0.1",
                    port = 11300, 
                    processor = onRequest)                              // Create Server
```

Tests:
------
- Units
```
sbt test
```

-Integration Tests
```
sbt "it:test-only space.divergence.beanstalker.ClientServerTest -- -DreqTube=req -DresTube=res -Dhost=127.0.0.1 -Dport=11300 -Dload=1000 -Dttp=1"
```



