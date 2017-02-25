package com.codelexems;

import com.codelexems.code.Node;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.HotspotMemoryProfiler;
import org.openjdk.jmh.profile.Profiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
public class ListsBenchmark {

    private LinkedList<Integer> linkedList;
    private ArrayList<Integer> arrayList;
    private int []array;
    private Node head;

    @Setup
    public void setup(){
        array = new Random().ints().limit(10_000_000).toArray();
        head = Node.fromArray(array);
        linkedList = new Random().ints().limit(10_000_000).boxed()
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void iterateLinkedList(Blackhole blackhole){
        Node current = head;
        while (current!=null){
            blackhole.consume(current.val);
            current = current.next;
        }

    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void iterateArray(Blackhole blackhole){
        for (int i : array) {
            blackhole.consume(i);
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void iterateArrayList(Blackhole blackhole){
        for (Integer i : arrayList) {
            blackhole.consume(i);
        }
    }




    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ListsBenchmark.class.getSimpleName())
                .warmupIterations(3)
                .measurementIterations(10)
                .forks(1)
                .build();

        Runner runner = new Runner(options);
        runner.run();
    }
}
