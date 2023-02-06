package com.concurrency.evolution.myself;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.concurrency.evolution.myself.ConcurrencySupport.*;

@Slf4j
public class C2_Threads {

    @Test
    public void shouldExecuteIterationsConcurrently() throws InterruptedException {
        start();

        List<Thread> threads = new ArrayList<>();
        for (int user = 1; user <= USERS; user++) {
            Thread thread = new Thread(new UserFlow(user));
            thread.start();
            threads.add(thread);
        }

        for (Thread thread: threads) {
            thread.join();
        }

        stop(USERS, SERVICE_A_LATENCY + SERVICE_B_LATENCY + PERSISTENCE_LATENCY * PERSISTENCE_FORK_FACTOR);

    }

    static class UserFlow implements Runnable {
        private final int user;

        UserFlow(int user) {
            this.user = user;
        }

        @Override
        public void run() {
            String serviceA = serviceA(user);
            String serviceB = serviceB(user);
            for (int i = 1; i <= PERSISTENCE_FORK_FACTOR; i++) {
                persistence(i, serviceA, serviceB);
            }
        }
    }
}
