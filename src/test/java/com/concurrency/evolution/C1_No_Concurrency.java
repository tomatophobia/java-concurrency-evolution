package com.concurrency.evolution;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static com.concurrency.evolution.ConcurrencySupport.PERSISTENCE_FORK_FACTOR;
import static com.concurrency.evolution.ConcurrencySupport.PERSISTENCE_LATENCY;
import static com.concurrency.evolution.ConcurrencySupport.USERS;
import static com.concurrency.evolution.ConcurrencySupport.SERVICE_A_LATENCY;
import static com.concurrency.evolution.ConcurrencySupport.SERVICE_B_LATENCY;
import static com.concurrency.evolution.ConcurrencySupport.persistence;
import static com.concurrency.evolution.ConcurrencySupport.serviceA;
import static com.concurrency.evolution.ConcurrencySupport.serviceB;
import static com.concurrency.evolution.ConcurrencySupport.start;
import static com.concurrency.evolution.ConcurrencySupport.stop;

@Slf4j
public class C1_No_Concurrency {

    /**
     * Without any concurrency everything is linear all latency is accumulated
     */
    @Test
    public void shouldBeNotConcurrent() {
        start();

        for (int user = 1; user <= USERS; user++) {
            String serviceA = serviceA(user);
            String serviceB = serviceB(user);
            for (int i = 1; i <= PERSISTENCE_FORK_FACTOR; i++) {
                persistence(i, serviceA, serviceB);
            }
        }

        stop(
                1,
                USERS * (SERVICE_A_LATENCY + SERVICE_B_LATENCY + PERSISTENCE_LATENCY * PERSISTENCE_FORK_FACTOR)
        );
    }
}
