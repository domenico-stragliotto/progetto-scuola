package com.scuola.management.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AlunnoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Alunno getAlunnoSample1() {
        return new Alunno().id(1L).nome("nome1").cognome("cognome1");
    }

    public static Alunno getAlunnoSample2() {
        return new Alunno().id(2L).nome("nome2").cognome("cognome2");
    }

    public static Alunno getAlunnoRandomSampleGenerator() {
        return new Alunno().id(longCount.incrementAndGet()).nome(UUID.randomUUID().toString()).cognome(UUID.randomUUID().toString());
    }
}
