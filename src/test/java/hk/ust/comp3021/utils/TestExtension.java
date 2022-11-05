package hk.ust.comp3021.utils;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.util.ExceptionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TestExtension is a JUnit 5 extension that is used to run each tests case in a separate thread.
 * This is to make sure the JUnit 5's test case timeout mechanism always works even when the test case runs into a dead loop.
 */
public class TestExtension implements InvocationInterceptor {
    private final HashMap<Invocation<?>, Thread> testCaseThreads = new HashMap<>();
    private final ReentrantLock mapLock = new ReentrantLock();

    /**
     * Proceed the test case invocation in a separate thread.
     * The thread is stored in a map for later forceful termination.
     *
     * @param invocation The invocation of test case.
     * @param <T>        The return type of the invocation.
     * @return The return value of the invocation.
     */
    public <T> T invokeInThread(Invocation<T> invocation) {
        final var executor = Executors.newSingleThreadExecutor();
        final var future = executor.submit(() -> {
            try {
                mapLock.lock();
                testCaseThreads.put(invocation, Thread.currentThread());
                mapLock.unlock();
                return invocation.proceed();
            } catch (Throwable t) {
                throw ExceptionUtils.throwAsUncheckedException(t);
            }
        });

        try {
            return future.get();
        } catch (ExecutionException ex) {
            throw ExceptionUtils.throwAsUncheckedException(ex.getCause());
        } catch (Throwable ex) {
            throw ExceptionUtils.throwAsUncheckedException(ex);
        }
    }

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) {
        this.invokeInThread(invocation);
    }

    @Override
    public <T> T interceptTestFactoryMethod(Invocation<T> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        return this.invokeInThread(invocation);
    }

    @Override
    public void interceptTestTemplateMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        this.invokeInThread(invocation);
    }

    @Override
    public void interceptDynamicTest(Invocation<Void> invocation, DynamicTestInvocationContext invocationContext, ExtensionContext extensionContext) throws Throwable {
        this.invokeInThread(invocation);
    }

    @Override
    public void interceptAfterEachMethod(Invocation<Void> invocation,
                                         ReflectiveInvocationContext<Method> invocationContext,
                                         ExtensionContext extensionContext
    ) throws Throwable {
        mapLock.lock();
        Thread thread = testCaseThreads.get(invocation);
        mapLock.unlock();
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            try {
                // force skill the test case thread, ugly but works
                //noinspection removal
                thread.stop();
            } catch (IllegalMonitorStateException ignored) {
            }
        }
        invocation.proceed();
    }
}
