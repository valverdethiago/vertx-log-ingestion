# LAA - Log Access Analytics

## Problem statement
Nabuco is a word class CEO. He need to know which pages are having more access on his website. Nabuco has the biggest pet shop in the world called NPS(Nabucco's Pet Service). Mr Nabuco has lots of access around the world. His site is running in 3 AWS(Amazon Web Service) regions being: us-east-1, us-west-2 and ap-south-1. Lucky-ly Nabuco developers wrote a log line every time a user access a webpage. Nabucos uses the best solution around web ui rendering web-assembly(WASM) faster than the speed of light. Here is a example of logs you might find:

---
/pets/exotic/cats/10 1037825323957 5b019db5-b3d0-46d2-9963-437860af707f 1
/pets/guaipeca/dogs/1 1037825323957 5b019db5-b3d0-46d2-9963-437860af707g 2
/tiggers/bid/now 1037825323957 5b019db5-b3d0-46d2-9963-437860af707e 3
---

First information is the URL of the site, second we have a timestamp with the user visited that URL. Them right after the timestamp we have the UUID of the user and finally region code being: us-east-1(1), us-west-2(2) and ap-south-1(3).

You need to build a solution that receives(ingest) logs via a REST endpoint and is able to calculate in near-real time all the top metrics like:

* Metric 1 - Top 3 URL accessed all around the world
* Metric 2 - Top 3 URL accessed PER region
* Metric 3 - The URL with less access in all world
* Metric 4 - Top 3 Access per DAY, WEEK, YEAR (you recive the DAY/WEEK/YEAR by parameter)
* Metric 5 - The minute with more access in all URLs

## Architecture bic picture

The main concern about this application could be deducted by the statement above:
* Lots of access
* Response-time needs to be really fast in metrics endpoint

It's a basic description of a streaming application that should handle tons of data and process than to create summarized view of it. The design should be able to provide:
* Strong and highly available rest API to handle the log ingestion
* A streaming mechanism to produce the summarized data in an asynchronous way and make it available just after the calculations, that should be done incrementally. 


![Architecture Overview](./diagram/architecutre_big_picture.png)

So, I'll split this description in two big pictures, each one of them to address one specific feature as described above.

### REST API

Our REST API should be developed in a highly scalable fashion that should be deployed on various instances to handle any amount of data that came from the main website. Each API call should not be blocking and even if our streaming component is offline we should be able to act and consume the messages that couldn't be consumed at the arrival.

#### Vert.x

Vert.x is an event-driven non-blocking application platform that enables us to write concurrent code without having to think too much about concurrency itsel, so we can focus on the application business logic instead of threads and synchornization. A key abstraction is Verticle, which works similarly to actors in the actor model, and besides all this features, it is polyglot, so we can write our components in different languages. 

Another key component is the event bus, which works similarly as a message broker shared with all verticles on all instances of the application cluster. It's very simple to run Vert.x in cluster mode, turning the event bus into a distributed event based broker accross multiple nodes without changing any code. Actually there are four libraries that can be used to run Vert.x in cluster mode, two of them in stable versions Apache Ignite and Hazelcast. The latest one was the choice for this POC basically because of it's really simple to work with.

