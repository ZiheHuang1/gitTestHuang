package com.za;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import jdk.nashorn.internal.ir.LexicalContextNode;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.*;

/**
 * @author huangzihe
 * @date 2020/8/19 3:44 下午
 */
public class Haha {
    public static <T> void demo(Supplier<T> supplier,
                                Function<T, Integer> function,
                                BiConsumer<T, Integer> biConsumer,
                                Consumer<T> consumer
                                ) {
        //生成一个数组
        T t = supplier.get();
        //获取长度
        int length = function.apply(t);
        List<Thread> list = new ArrayList<>();
        for (int i = 0;i < 100;i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0;j < 500;j++) {
                    biConsumer.accept(t, j % length);
                }
            });
            list.add(thread);
        }
        list.forEach(Thread::start);
        list.forEach(one -> {
            try {
                one.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        consumer.accept(t);
    }

    public static void main(String[] args) {
        demo(() -> new int[10],
                i -> i.length,
                (i, j) -> i[j]++,
                i -> System.out.println(Arrays.toString(i)));
//        demo(() -> new AtomicIntegerArray(10),
//                array -> array.length(),
//                (array, index) -> array.getAndIncrement(index),
//                array -> System.out.println(array));
    }
}




