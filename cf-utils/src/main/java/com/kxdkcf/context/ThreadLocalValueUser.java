package com.kxdkcf.context;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.context
 * Author:              wenhao
 * CreateTime:          2024-12-28  22:47
 * Description:         线程本地变量
 * Version:             1.0
 */
public class ThreadLocalValueUser {
    private static final ThreadLocal<Object> threadLocal = new ThreadLocal<>();


    public static <E> E getThreadLocalValue(Class<E> type) {

        @SuppressWarnings("unchecked")
        E e = (E) threadLocal.get();
        return e;
    }

    public static <E> void setThreadLocalValue(E o) {
        threadLocal.set(o);
    }

    public static void removeThreadLocalValue() {
        threadLocal.remove();
    }
}
