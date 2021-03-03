package org.atlanmod.warmup;

public class DummyWarmup {
    public static void warmup() {
        for (int i = 0; i < 100000; i++) {
            Dummy dummy = new Dummy();
            dummy.m();
        }
    }
}