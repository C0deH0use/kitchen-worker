package pl.codehouse.restaurant.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class KitchenWorkerRequestListener {

    private static final Logger logger = LoggerFactory.getLogger(KitchenWorkerRequestListener.class);

    @KafkaListener(topics = "${app.kafka.kitchen.topic.topic-name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(KitchenWorkerRequestMessage message) {
        logger.info("Received KitchenWorker event: {}", message);
        // Add your event processing logic here
//        packingCommand.execute(new Context<Integer>(message.requestId()))
//                .map(ExecutionResult::handle)
//                .doOnSuccess(result -> logger.info("Packing command for the following request:{} finished with the following:{}", event.requestId(), result))
//                .doOnError(error -> logger.error("Error while processing packing command for request: {}. Error:{}",
//                        event.requestId(),
//                        error.getMessage(),
//                        error)
//                )
//                .subscribe();
    }
}
