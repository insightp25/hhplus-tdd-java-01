package io.hhplus.tdd.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class LockHandler {

    private final Map<Long, Lock> userMap = new HashMap<>();

    public <T> T executeOnLock(Long userId, Supplier<T> block) {
        Lock lock = userMap.computeIfAbsent(userId, k -> new ReentrantLock(true));

        try {
            if (!lock.tryLock(3000, TimeUnit.MILLISECONDS)) {
                throw new TimeoutException();
            }

            return block.get();
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }
    }
}
