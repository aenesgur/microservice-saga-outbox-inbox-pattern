# Microservice Application with Saga, Outbox, Inbox, Distributed Locking Design Patterns
Microservice boilerplate application developed with Outbox/Inbox pattern and Distributed Caching
### Technologies
* Spring Boot
* Spring Data
* Apache Kafka
* Redis
* Docker
* Postgresql

#### What is Inbox/Outbox pattern?
The Inbox-Outbox pattern is a messaging pattern used in distributed systems to improve the performance and reliability of communication between components.

In this pattern, each component has two message queues: an inbox and an outbox. The inbox receives messages from other components, while the outbox holds messages that the component needs to send to other components.

The component processes messages from its inbox in a separate thread, and once it completes processing a message, it places the response message in its outbox. The outbox messages are then sent by a separate thread that continuously polls the outbox queue for new messages and sends them to their destinations.

This pattern can help improve performance and reliability by decoupling the sending and receiving of messages, reducing contention and preventing message loss. It also enables components to operate asynchronously, allowing them to continue processing messages from their inbox while messages from their outbox are being sent.

#### What is Distributed Caching?
Distributed caching is a technique used in distributed computing to improve performance and scalability by caching frequently accessed data in memory across a network of distributed nodes.

In a distributed caching system, a cache layer is placed between the application and the database or data source. When the application needs to access data, it first checks the cache layer to see if the requested data is available. If the data is found in the cache, the application can retrieve it directly from the cache, which can be much faster than accessing the original data source. If the data is not found in the cache, the application retrieves it from the original data source and stores it in the cache for future use.

### System Diagram
![microservice-saga-outbox-inbox-pattern](https://user-images.githubusercontent.com/47754791/229523982-685a5dba-78ee-41b3-b64a-6cc8bf9210eb.jpg)

### Distributed Lock Diagram
![distributed-lock](https://user-images.githubusercontent.com/47754791/224034101-bc4be8ff-735d-4f81-910d-a789ed64b5d2.jpg)

### Run
First: 
``
mvn clean install
``

Then:
``
docker compose up -d
``

### Test
Test for payment-fail-topic:

``
curl --location --request POST 'localhost:4041/api/orders' \
--header 'Content-Type: application/json' \
--data-raw '{
"totalPrice": 12,
"cardNo":"27411111222233334444",
"userId": 45623,
"orderNote":"lorem ipsum"
}'``


Test for payment-complete-topic:

``
curl --location --request POST 'localhost:4041/api/orders' \
--header 'Content-Type: application/json' \
--data-raw '{
"totalPrice": 12,
"cardNo":"77411111222233334444",
"userId": 45623,
"orderNote":"lorem ipsum"
}'``

