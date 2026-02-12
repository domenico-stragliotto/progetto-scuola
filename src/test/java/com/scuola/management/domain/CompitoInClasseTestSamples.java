package com.scuola.management.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CompitoInClasseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CompitoInClasse getCompitoInClasseSample1() {
        return new CompitoInClasse().id(1L);
    }

    public static CompitoInClasse getCompitoInClasseSample2() {
        return new CompitoInClasse().id(2L);
    }

    public static CompitoInClasse getCompitoInClasseRandomSampleGenerator() {
        return new CompitoInClasse().id(longCount.incrementAndGet());
    }
}
