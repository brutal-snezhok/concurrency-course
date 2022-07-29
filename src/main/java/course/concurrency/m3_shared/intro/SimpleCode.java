package course.concurrency.m3_shared.intro;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class SimpleCode {

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        final int size = 50_000_000;
        Object[] objects = new Object[size];
        for (int i = 0; i < size; ++i) {
            objects[i] = new Object();
        }

        System.out.println("Time: " + ((double) (System.currentTimeMillis() - time)/1000) + " s");
    }

    public static <R> Map<String, R> execute(List<String> tables, Function<String, R> query, R defaultValue) {
        Map<String, R> tableToResultMap = new ConcurrentHashMap<>();

        CompletableFuture<?>[] futures = tables.stream()
                .map(table -> CompletableFuture
                        .supplyAsync(() -> tableToResultMap.put(table, query.apply(table)))
                        .handle((res, ex) -> {
                            if(ex != null) {
                                tableToResultMap.put(table, defaultValue);
                            }
                            return res;
                        }))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();

        return tableToResultMap;
    }
}
