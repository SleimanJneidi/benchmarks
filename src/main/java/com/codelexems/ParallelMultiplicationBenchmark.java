
package com.codelexems;

import com.codelexems.code.ArrayReducer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
public class ParallelMultiplicationBenchmark {

    private BigDecimal[]input;
    private static final int SIZE = 75_000;

    @Setup
    public void setup(){
        input = new Random().longs().limit(SIZE).mapToObj(BigDecimal::valueOf).toArray(BigDecimal[]::new);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void parallelMutliply(Blackhole blackhole) {
        ArrayReducer<BigDecimal> reducer = new ArrayReducer<>(input,BigDecimal.ONE,BigDecimal::multiply);
        BigDecimal result = reducer.compute();
        blackhole.consume(result);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void sequentialMultiply(Blackhole blackhole){
        BigDecimal acc = BigDecimal.ONE;
        for (BigDecimal bigDecimal : input) {
            acc = acc.multiply(bigDecimal);
        }
        blackhole.consume(acc);
    }


    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ParallelMultiplicationBenchmark.class.getSimpleName())
                .warmupIterations(3)
                .measurementIterations(10)
                .forks(1)
                .build();

        Runner runner = new Runner(options);
        runner.run();
    }

}
