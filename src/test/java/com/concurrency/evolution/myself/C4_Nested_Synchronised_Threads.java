package com.concurrency.evolution.myself;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.concurrency.evolution.myself.ConcurrencySupport.*;

@Slf4j
public class C4_Nested_Synchronised_Threads {

    @Test
    public void shouldExecuteIterationsConcurrently() throws InterruptedException {
        start();

        List<Thread> threads = new ArrayList<>();
        for (int user = 1; user <= USERS; user++) {
            Thread thread = new Thread(new UserFlow(user));
            thread.start();
            threads.add(thread);
        }

        // Stop Condition - Not the most optimal but gets the work done
        for (Thread thread : threads) {
            thread.join();
        }

        stop();
    }

    static class UserFlow implements Runnable {
        private final int user;
        private final List<String> serviceResult = new ArrayList<>();

        public UserFlow(int user) {
            this.user = user;
        }

        @SneakyThrows
        @Override
        public void run() {
            Thread threadA = new Thread(new Service(this, "A", SERVICE_A_LATENCY, user));
            Thread threadB = new Thread(new Service(this, "B", SERVICE_B_LATENCY, user));
            threadA.start();
            threadB.start();
            threadA.join();
            threadB.join();

            List<Thread> threads = new ArrayList<>();
            for (int i = 1; i <= PERSISTENCE_FORK_FACTOR; i++) {
                Thread thread = new Thread(new Persistence(i, serviceResult.get(0), serviceResult.get(1)));
                thread.start();
                threads.add(thread);
            }

            // Not the most optimal but gets the work done
            for (Thread thread : threads) {
                thread.join();
            }
        }

        public synchronized void addToResult(String result) {
            serviceResult.add(result);
        }
    }

    static class Service implements Runnable {
        private final UserFlow callback;
        private final String name;
        private final long latency;
        private final int iteration;

        public Service(UserFlow callback, String name, long latency, int iteration) {
            this.callback = callback;
            this.name = name;
            this.latency = latency;
            this.iteration = iteration;
        }

        @Override
        public void run() {
            callback.addToResult(service(name, latency, iteration));
        }
    }

    static class Persistence implements Runnable {
        private final int fork;
        private final String serviceA;
        private final String serviceB;

        public Persistence(int fork, String serviceA, String serviceB) {
            this.fork = fork;
            this.serviceA = serviceA;
            this.serviceB = serviceB;
        }

        @Override
        public void run() {
            persistence(fork, serviceA, serviceB);
        }
    }
}
