package com.concurrency.evolution;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static com.concurrency.evolution.ConcurrencySupport.*;

// JDK 19 버전에서 하니까 됨.
@Slf4j
public class C9_Loom {

    public static void main(String[] args) {
        new C9_Loom().startConcurrency();
    }

    @SneakyThrows
    private void startConcurrency() {
        start();

        try (var e = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.rangeClosed(1, USERS).forEach(i -> e.submit(() -> userFlow(i)));
        }

        stop();
    }

    @SneakyThrows
    private void userFlow(int user) {
        List<Future<String>> result;
        try (var e = Executors.newVirtualThreadPerTaskExecutor()) {
            result = e.invokeAll(List.of(
                    () -> serviceA(user),
                    () -> serviceB(user)
            ));
        }

        persist(result.get(0).get(), result.get(1).get());
    }

    private void persist(String serviceA, String serviceB) {
        try (var e = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.rangeClosed(1, PERSISTENCE_FORK_FACTOR)
                    .forEach(i -> e.submit(() -> persistence(i, serviceA, serviceB)));
        }
    }
}
