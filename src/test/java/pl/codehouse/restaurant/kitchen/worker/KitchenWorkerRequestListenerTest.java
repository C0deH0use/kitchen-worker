package pl.codehouse.restaurant.kitchen.worker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.codehouse.restaurant.kitchen.Command;
import pl.codehouse.restaurant.kitchen.Context;
import pl.codehouse.restaurant.kitchen.ExecutionResult;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class KitchenWorkerRequestListenerTest {

    @Mock
    private CookMenuItemCommand cookCommand;

    private KitchenWorkerRequestListener sut;

    @BeforeEach
    void setUp() {
        sut = new KitchenWorkerRequestListener(List.of(cookCommand));
    }

    @Test
    @DisplayName("should execute cook command and handle its result when request received")
    void should_ExecuteCookCommandAndHandleItsResult_When_RequestReceived() {
        // given
        KitchenWorkerRequestMessage message = new KitchenWorkerRequestMessage(1000, 1);
        Context<KitchenWorkerRequestMessage> expectedCtx = new Context<>(message);

        given(cookCommand.execute(expectedCtx)).willReturn(Mono.just(ExecutionResult.success(true)));

        // when
        Mono<Boolean> monoResult = sut.listen(message);

        // then
        StepVerifier.create(monoResult)
                .assertNext(result -> assertThat(result).isTrue())
                .verifyComplete();

        then(cookCommand).should(times(1)).execute(expectedCtx);
    }

    @Test
    @DisplayName("should return true when no commands are applicable for received message")
    void should_ReturnTrue_When_NoCommandsAreApplicable() {
        // given
        KitchenWorkerRequestMessage message = new KitchenWorkerRequestMessage(1000, 1);
        sut = new KitchenWorkerRequestListener(List.of());

        // when
        Mono<Boolean> monoResult = sut.listen(message);

        // then
        StepVerifier.create(monoResult)
                .assertNext(result -> assertThat(result).isTrue())
                .verifyComplete();

        then(cookCommand).should(never()).execute(any());
    }

    @MethodSource("mockCommandMethodSource")
    @ParameterizedTest(name = "{index} - should handle all responses from command results ({argumentsWithNames}) when received kitchen request message and having multiple commands that are applicable")
    void should_HandleAllResponsesFromCommandResults_When_HandlingKitchenRequestMessagesAndHavingMultipleCommands(
            boolean mockCommandResult, boolean expectedResult
    ) {
        // given
        KitchenWorkerRequestMessage message = new KitchenWorkerRequestMessage(1000, 1);
        Context<KitchenWorkerRequestMessage> expectedCtx = new Context<>(message);

        Command<KitchenWorkerRequestMessage, Boolean> mockCommand = Mockito.mock(Command.class);

        sut = new KitchenWorkerRequestListener(List.of(cookCommand, mockCommand));

        given(cookCommand.execute(expectedCtx)).willReturn(Mono.just(ExecutionResult.success(true)));
        given(mockCommand.execute(expectedCtx)).willReturn(Mono.just(ExecutionResult.success(mockCommandResult)));

        // when
        Mono<Boolean> monoResult = sut.listen(message);

        // then
        StepVerifier.create(monoResult)
                .assertNext(result -> assertThat(result).isEqualTo(expectedResult))
                .verifyComplete();

        then(cookCommand).should(times(1)).execute(expectedCtx);
        then(mockCommand).should(times(1)).execute(expectedCtx);
    }

    private static Stream<Arguments> mockCommandMethodSource() {
        return Stream.of(
                Arguments.of(true, true),
                Arguments.of(false, false)
        );
    }
}