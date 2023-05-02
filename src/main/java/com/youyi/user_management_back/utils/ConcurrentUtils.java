package com.youyi.user_management_back.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ConcurrentUtils {

    public static <T> Future<T> doJob(ExecutorService executorService, Callable<T> callable) {
        return executorService.submit(callable);
    }


}
