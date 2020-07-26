# LAA Rest API

The code was develop using [IntelliJ IDEA](https://www.jetbrains.com/idea/), using [maven](https://maven.apache.org/) as build and depencency management tool and the following libraries and its dependencies:
* [Eclipse Vert.x](https://vertx.io/) - Non blocking event based toolkit;
* [vertx-health-chek](https://vertx.io/docs/vertx-health-check/java/) - Component that provides a simple way to expose healh checks on vert.x applications;
* [vertx-redis-clitn](https://vertx.io/docs/vertx-redis-client/java/) - Vert.x module that allows data to be saved, retrieved, searched for, and deleted in Redis;
* [logback](http://logback.qos.ch/) Popular logging library for java projects
* [Kafka Streams](https://kafka.apache.org/documentation/streams/) - Client library for build applications and microservices, where the input and output data are stored in kafka clusters;
* [Zandero Rest Vert.x](https://github.com/zandero/rest.vertx) - Lightweigth JAX-RS (RestEasy) like annotation processor for Vert.x verticles
* [Intapp Vertx Guice](https://github.com/intappx/vertx-guice) - Enable verticle dependency injection in Vert.x using [Guice](https://github.com/google/guice)
* [jersey-server](https://eclipse-ee4j.github.io/jersey/) - JAX-RS implementation
* [Guice](https://github.com/google/guice) - Dependency Injection framework
* [vertx-hazelcast](https://vertx.io/docs/vertx-hazelcast/java/) - Cluster manager 









## Eclipse Vert.x 

Eclipse Vert.x, which we will just call vertx) is an opersource project at the Eclipse foundation. 

Vert.x is not a framework but a toolkit: the core library defines the fundamental APIs for writing asynchronous networked applications, and then you can pick the useful modules for your application (e.g., database connection, monitoring, authentication, logging, service discovery, clustering support, etc). Vert.x is based on the Netty project, a high-performance asynchronous networking library for the JVM. Vert.x will let you access the Netty internals if need be, but in general you will better benefit from the higher-level APIs that Vert.x provides while not sacrificing performance compared to raw Netty.

Vert.x does not impose any packaging or build environment. Since Vert.x core itself is just a regular Jar library it can be embedded inside applications packaged as a set of Jars, a single Jar with all dependencies, or it can even be deployed inside popular component and application containers.

Because Vert.x was designed for asynchronous communications it can deal with more concurrent network connections with less threads than synchronous APIs such
as Java servlets or java.net socket classes. Vert.x is useful for a large range of applications: high volume message / event processing, micro-services, API gateways,
HTTP APIs for mobile applications, etc. Vert.x and its ecosystem provide all sorts of technical tools for building end-to-end reactive applications.

While it may sound like Vert.x is only useful for demanding applications, the present guide also states that Vert.x works very well for more traditional web
applications. As we will see, the code will remain relatively easy to comprehend, but if the application needs to face a sudden peak in traffic then the code is already written with the essential ingredient for scaling up: asynchronous processing of events.
Finally, it is worth mentioning that Vert.x is polyglot as it supports a wide range of popular JVM languages: Java, Groovy, Scala, Kotlin, JavaScript, Ruby and
Ceylon. The goal when supporting a language in Vert.x is not just to provide access to the APIs, but also to make sure that the language-specific APIs are idiomatic
in each target language (e.g., using Scala futures in place of Vert.x futures). It is well-possible to develop different technical parts of a Vert.x application using
different JVM languages.


