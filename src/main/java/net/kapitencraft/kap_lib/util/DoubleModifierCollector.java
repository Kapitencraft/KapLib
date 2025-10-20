package net.kapitencraft.kap_lib.util;

import java.util.ArrayList;
import java.util.List;

public class DoubleModifierCollector {

    private final List<Double> addition = new ArrayList<>(),
            multiplyBase = new ArrayList<>(),
            multiplyTotal = new ArrayList<>();


    public void addAddition(double d) {
        addition.add(d);
    }

    public void addBaseMultiplication(double d) {
        multiplyBase.add(d);
    }

    public void addTotalMultiplication(double d) {
        multiplyTotal.add(d);
    }

    public double calculate(double base) {
        for (double d : addition) base += d;
        for (double d : multiplyBase) base *= d;
        for (double d : multiplyTotal) base *= d;
        return base;
    }
}