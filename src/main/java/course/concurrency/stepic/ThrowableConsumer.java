package course.concurrency.stepic;

/**
 * Represents an action that may potentially fail with an exception
 */
@FunctionalInterface
interface ThrowableConsumer<T, E extends Throwable> {
    void accept(T t) throws E;
}
