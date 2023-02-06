package com.concurrency.evolution.myself;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static com.concurrency.evolution.myself.ConcurrencySupport.*;

@Slf4j
public class C1_No_Concurrency {
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

        stop(1, USERS * (SERVICE_A_LATENCY + SERVICE_B_LATENCY + PERSISTENCE_LATENCY * PERSISTENCE_FORK_FACTOR));
    }

}
