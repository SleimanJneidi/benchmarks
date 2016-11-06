package com.codelexems.code;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.BinaryOperator;

public class ArrayReducer<T> {

    static class ReductionTask<T> extends RecursiveTask<T> {
        private final T[]a;
        private final int low;
        private final int high;
        private final BinaryOperator<T> operator;
        private static final int THRESHOLD = 8192;

        public ReductionTask(T[]a, BinaryOperator<T>operator){
            this(a,0,a.length-1,operator);
        }

        private ReductionTask(T[]a,int low,int high,BinaryOperator<T>operator) {
            this.a = a;
            this.low = low;
            this.high = high;
            this.operator = operator;
        }

        @Override
        protected T compute() {
            if((high-low)<=THRESHOLD){
                T result = a[low];
                for (int i = low + 1;i<= high;i++) {
                    result = operator.apply(result,a[i]);
                }
                return result;
            }

            int mid = (low + high) >>> 1;
            ReductionTask<T> leftTask = new ReductionTask<>(a,low,mid,operator);
            ReductionTask<T> rightTask = new ReductionTask<>(a,mid+1,high,operator);
            ForkJoinTask<T> fork1 = leftTask.fork();
            ForkJoinTask<T>fork2 = rightTask.fork();
            T result = operator.apply(fork1.join(),fork2.join());
            return result;
        }
    }

    private final T[]a;
    private final T identity;
    private final BinaryOperator<T> operator;

    public ArrayReducer(T[]a,T identity, BinaryOperator<T> operator){
        this.a = a;
        this.identity = identity;
        this.operator = operator;
    }

    public T compute(){
        if(a.length==0){
            return this.identity;
        }
        ReductionTask<T> reductionTask = new ReductionTask<>(a,operator);
        ForkJoinTask<T> task = ForkJoinPool.commonPool().submit(reductionTask);
        T result = task.invoke();
        result = operator.apply(identity, result);
        return result;
    }

}
