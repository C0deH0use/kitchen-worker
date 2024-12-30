package pl.codehouse.restaurant.worker;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.codehouse.restaurant.Command;
import pl.codehouse.restaurant.Context;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
class KitchenWorkerRequestListener {

    private static final Logger logger = LoggerFactory.getLogger(KitchenWorkerRequestListener.class);

    private final List<Command<KitchenWorkerRequestMessage, Boolean>> workerCommands;

    KitchenWorkerRequestListener(List<Command<KitchenWorkerRequestMessage, Boolean>> workerCommands) {
        this.workerCommands = workerCommands;
    }

    @KafkaListener(topics = "${app.kafka.kitchen.topic.topic-name}", groupId = "${spring.kafka.consumer.group-id}")
    public Mono<Boolean> listen(KitchenWorkerRequestMessage message) {
        logger.info("Received KitchenWorker event: {}", message);
        // Add your event processing logic here
        Context<KitchenWorkerRequestMessage> context = new Context<>(message);
        return Flux.fromIterable(workerCommands)
                .flatMap(cmd -> cmd.execute(context)
                        .doOnSuccess(result -> logger.info("Command {} finished with result: {}, being executed with following request:{}",
                                                           cmd.getClass().getSimpleName(),
                                                           result.isSuccess(),
                                                           context.request()
                        )))
                .reduce(true, (result, commandResult) -> result && commandResult.handle());
    }
}
