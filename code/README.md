# LAA Rest API

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


