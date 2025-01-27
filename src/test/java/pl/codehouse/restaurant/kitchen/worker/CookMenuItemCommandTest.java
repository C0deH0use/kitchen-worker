package pl.codehouse.restaurant.kitchen.worker;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.codehouse.commons.Context;
import pl.codehouse.commons.ExecutionResult;
import pl.codehouse.restaurant.kitchen.exceptions.ConfigurationNotFoundException;
import pl.codehouse.restaurant.kitchen.shelf.ShelfService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CookMenuItemCommandTest {

    private static final int MENU_ITEM1_COOKING_TIME = 160;
    private final MenuItemsCookingTimes menuItemsCookingTimes = MenuItemsCookingTimes.from(Map.entry(1000, MENU_ITEM1_COOKING_TIME));

    @Mock
    private ShelfService shelfService;

    private CookMenuItemCommand sut;

    @BeforeEach
    void setUp() {
        sut = new CookMenuItemCommand(shelfService, menuItemsCookingTimes);
    }

    @Test
    @DisplayName("should prepare menuItem1 in a given amount of time when requesting known menu item")
    void should_prepareMenuItem1InGivenAmountOfTime_When_RequestingKnownMenuItem() {
        // given
        var message = new KitchenWorkerRequestMessage(1000, 2);
        Context<KitchenWorkerRequestMessage> ctx = new Context<>(message);
        given(shelfService.updateMenuItem(1000, 2)).willReturn(Mono.empty());

        // when
        Mono<ExecutionResult<Boolean>> resultMono = sut.execute(ctx);

        // then
        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertThat(result.isSuccess()).isTrue();
                    assertThat(result.handle()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("should not prepare menuItem if when unknown menu item")
    void should_NotPrepareMenuItemIf_When_UnknownMenuItem() {
        // given
        var message = new KitchenWorkerRequestMessage(100, 2);
        Context<KitchenWorkerRequestMessage> ctx = new Context<>(message);

        //when
        Mono<ExecutionResult<Boolean>> resultMono = sut.execute(ctx);

        //then
        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertThat(result.isFailure()).isTrue();
                    assertThat(result.exception())
                            .isInstanceOf(ConfigurationNotFoundException.class)
                            .hasMessage("Missing configuration for the following Menu Item: " + 100)
                    ;
                });
    }

}