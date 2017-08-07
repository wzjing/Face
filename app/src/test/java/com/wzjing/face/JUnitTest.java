package com.wzjing.face;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.concurrent.locks.Lock;

public class JUnitTest {

    private Number number = new Number();

    @Test
    public void fooThread() throws InterruptedException {
        Thread A = newCumpute("A");
        Thread B = newCumpute("B");
        A.start();
        B.start();
        A.join();
        B.join();
    }

    private Thread newCumpute(String name) {
        return new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                number.addSelf();

                Thread.yield();
            }
        }, name);
    }

    class Number {
        private final IntNumber number = new IntNumber(0);

        void addSelf() {
            synchronized (number) {
                for (int i = 0; i < 10000000; i++)
                    number.set(number.value()+1);
//                number.notify();
                System.out.println(String.format("Thread:%s, %d", Thread.currentThread().getName(), number.value()));
            }
        }
    }

    class IntNumber {
        private int number = 0;
        public IntNumber(int value) {
            number = value;
        }
        private void set(int value) {
            number = value;
        }
        private int value(){
            return number;
        }
    }

}
