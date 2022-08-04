package course.concurrency.stepic;

/**
 * Represents a Function that accepts T and returns R and may fail with an exception
 */
@FunctionalInterface
interface ThrowableFunction<T, R> {
    R apply(T t) throws Throwable;
}
