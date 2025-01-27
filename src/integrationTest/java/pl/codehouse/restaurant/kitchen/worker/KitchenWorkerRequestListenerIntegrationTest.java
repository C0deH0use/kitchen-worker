package pl.codehouse.restaurant.kitchen.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import pl.codehouse.commons.Context;
import pl.codehouse.commons.ExecutionResult;
import pl.codehouse.restaurant.kitchen.TestcontainersConfiguration;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.kafka.kitchen.topic.topic-name=test_kitchen_topic"
        }
)
@ExtendWith(SpringExtension.class)
@Import(TestcontainersConfiguration.class)
class KitchenWorkerRequestListenerIntegrationTest {
    private static final String TOPIC_NAME = "test_kitchen_topic";
    private static final LocalDateTime UPDATED_AT = LocalDateTime.parse("2024-10-03T10:15:30");

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CookMenuItemCommand command;

    @Captor
    private ArgumentCaptor<Context<KitchenWorkerRequestMessage>> contextCaptor;

    private Producer<Integer, String> producerServiceTest;

    @BeforeEach
    void setUp(
            @Autowired KafkaContainer kafkaContainer
    ) {
        Map<String, Object> testProducerProps = KafkaTestUtils.producerProps(kafkaContainer.getBootstrapServers());
        producerServiceTest = new DefaultKafkaProducerFactory<Integer, String>(testProducerProps).createProducer();
    }

    @Test
    @DisplayName("Should execute KitchenWorker when RequestMessage is received")
    void shouldExecutePackingCommandWhenShelfEventDtoIsReceived() {
        // given
        int requestId = 123;
        Map<String, Object> message = Map.of(
                "menuItemId", 1000,
                "quantity", 2
        );
        String recordPayload = createPayload(message);
        RecordHeaders recordHeaders = new RecordHeaders();
        recordHeaders.add("__TypeId__", "pl.codehouse.restaurant.kitchen.worker.KitchenWorkerRequestMessage".getBytes(StandardCharsets.UTF_8));
        var producerRecord = new ProducerRecord<Integer, String>(TOPIC_NAME, null, null, requestId, recordPayload, recordHeaders);

        given(command.execute(any())).willReturn(Mono.just(ExecutionResult.success(Boolean.TRUE)));

        // when
        producerServiceTest.send(producerRecord);

        // then
        var expectedRequestMessage = new KitchenWorkerRequestMessage(1000, 2);

        Awaitility.given()
                .atMost(Duration.ofSeconds(2))
                .then()
                .pollDelay(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    then(command).should(times(1)).execute(contextCaptor.capture());
                    Context<KitchenWorkerRequestMessage> capturedContext = contextCaptor.getValue();
                    assertThat(capturedContext.request()).isEqualTo(expectedRequestMessage);
                });
    }

    private String createPayload(Map<String, Object> shelfEventDto) {
        try {
            return objectMapper.writeValueAsString(shelfEventDto);
        } catch (JsonProcessingException e) {
            return Assertions.fail("Failed to create payload", e);
        }
    }
}
