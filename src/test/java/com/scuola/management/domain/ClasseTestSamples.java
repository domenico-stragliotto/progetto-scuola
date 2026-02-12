package com.scuola.management.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClasseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Classe getClasseSample1() {
        return new Classe().id(1L).numero(1).sezione("sezione1").note("note1");
    }

    public static Classe getClasseSample2() {
        return new Classe().id(2L).numero(2).sezione("sezione2").note("note2");
    }

    public static Classe getClasseRandomSampleGenerator() {
        return new Classe()
            .id(longCount.incrementAndGet())
            .numero(intCount.incrementAndGet())
            .sezione(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString());
    }
}
