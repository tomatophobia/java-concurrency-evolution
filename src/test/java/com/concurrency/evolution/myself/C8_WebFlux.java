package com.concurrency.evolution.myself;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static com.concurrency.evolution.myself.ConcurrencySupport.*;

@Slf4j
public class C8_WebFlux {
    @SneakyThrows
    @Test
    public void shouldExecuteIterationsConcurrently() {
        start();

        Flux.range(1, USERS)
                .flatMap(i -> Mono.defer(() -> userFlow(i)).subscribeOn(Schedulers.parallel()))
                .blockLast();

        stop();
    }

    private Mono<String> userFlow(int user) {
        Mono<String> serviceA = Mono.defer(() -> Mono.just(serviceA(user))).subscribeOn(Schedulers.elastic());
        Mono<String> serviceB = Mono.defer(() -> Mono.just(serviceB(user))).subscribeOn(Schedulers.elastic());
        return serviceA.zipWith(serviceB, (sA, sB) -> Flux.range(1, PERSISTENCE_FORK_FACTOR)
                .flatMap(i ->
                        Mono.defer(() -> Mono.just(persistence(i, sA, sB))).subscribeOn(Schedulers.elastic())
                )
                .blockLast()
        );
    }
}
