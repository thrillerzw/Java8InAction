package lambdasinaction.chap7;

import java.util.stream.*;

public class ParallelStreams {

    public static long iterativeSum(long n) {
        long result = 0;
        for (long i = 0; i <= n; i++) {
            result += i;
        }
        return result;
    }
    //iterate生成的是装箱对象，需要拆箱求和
    public static long sequentialSum(long n) {
        return Stream.iterate(1L, i -> i + 1).limit(n).reduce(Long::sum).get();
    }
    //并行后反而慢了，因为iterate很难分割成独立的小块，每次应用这个函数都要依赖前一次应用的结果。
    public static long parallelSum(long n) {
        return Stream.iterate(1L, i -> i + 1).limit(n).parallel().reduce(Long::sum).get();
    }

    public static long rangedSum(long n) {
        return LongStream.rangeClosed(1, n).reduce(Long::sum).getAsLong();
    }
    //LongStream没有拆箱操作，容易分割成独立小块。
    // 正确的数据结构+并行=最佳的性能。数据在内核中工作的时间 > 内核之间传输数据的时间。并行：递归划分为小块，分配给不同的线程，合并结果。避免共享变量
    public static long parallelRangedSum(long n) {
        return LongStream.rangeClosed(1, n).parallel().reduce(Long::sum).getAsLong();
    }

    public static long sideEffectSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).forEach(accumulator::add);
        return accumulator.total;
    }
    //计算结果错误，因为并行共享变量total。
    public static long sideEffectParallelSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
        return accumulator.total;
    }

    public static class Accumulator {
        private long total = 0;

        public void add(long value) {
            total += value;
        }
    }
}
