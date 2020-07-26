# Log Access Analytics - Code evaluation

## Pain points and TODO List
There're few itens that I'd do in a different way:

1. Log access persistence - Logs being ingested are being persisted only on Kafka topic. If we loose the instance it won't be possible to recover this records. A kafka sinc to deliver the input log topic contents to a nosql/sql database or even a low cost cloud storage mechanism, such glacier should be enough.

2. As this solution works in a cluster that relies on TCP/IP multicast to work, it won't be possible to deploy multiple cluster around the globe. I'm preety sure that in this application latency wouldn't be an issue because of the size of logs being transported. This wouldn't be a problem.

3. Security - Adding an application security layer would be great to avoid any problems using OAuth2. Also deploying this service in a VPC with white list app also would contribute for security. A third layer would be exposing the log ingestion API via an API gateway with throtling woudld garantee that we won't run into any attacks even if one of NPS servers had been taken.

4. Persistence of metrics - In the streams application we also are consuming the output topics to send the aggregation results to Redis. There's a lot of responsibility for a single microservice and it'd be a better option to let kafka sinc to persit output topics into the destination.

5. Microservice breakdow - In a single API we are getting the ingested logs, sending it to the destination, processing the aggregation logic and serving the metrics. IMHO it's not consice and I'd split this microservice into 2. One with the REST endpoints and another one with the streaming logic.

## Team work

The job of attracting, motivating and persuades developers is more related to personal skills and environment features, but there's a few things that is worthy to mention:

1. Good stack - There's a few benchmarks comparing the components of this stack with other frameworks in the market, and we can say that Vert.x is one of the best in terms of performance and it also has a great way to code. Also Kafka Streams is a very interesting component that can be applied to several problems. 
2. Listen - It's good to have all the cerimonies that the agile process demands, but also we can't stick only with them. Estimulating one-to-one meetings, coding DOJOs or simply be open to others opinion about every piece of the project keeps people motivated to contribute, search for better ways to accomplish the tasks and also increases the ownership of the team.
3. Training - Not all developers should be performing at the best level on the first 1-2 weeks while working with a new technology or framewoek. Provide good trainning and mentoring is not good only to the project but to the company and people.
4. Recognition - Managers need to work with employees to understand what they want for their future. Many engineers would prefer to stay technical rather than go down a management path, but they want to be sure they can continue to grow in the organization. Providing opportunities for them to excel and become technical leaders within the organization will go a long way toward motivating your software engineers to continue producing quality work and maintaining their loyalty to the business
5. Opportunities to innovate - Estimulate people to share new ideas, talk about their pet projects, improove the knowledge transfer, interact with other teams working in different projects.
6. Flexible work hours - The stereotypical software engineer is burning the midnight oil, energized by solving the problem they're working on. However, if they're going to be scolded for coming into the office late, they're more likely to spend those evening hours on pet projects that have nothing to do with work. Though an organization has to enforce policies regarding when employees should be in the office, the more flexible they can be, the more likely they'll be to have loyal employees. An employee who is trusted to get their work done is more likely to work on their own time than one who is mandated to be in the office at times that are difficult for them.
7. Infrastructure - Good infrastructure on the office can transform the environment. Not only computers and fast network, good chairs, coffee and ammenities motivate people to stay and produce more.

## Team distribution
It's a small project with well defined features and few rules to develop. I'd focus the team in coding, data and devops, probabbly with 50% devs experienced devs (java), 25% with devops and cloud knowlede (build and monitoring tools) and 25% of devops with more focus on data. 

