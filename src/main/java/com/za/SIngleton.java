package com.za;

/**
 * @author huangzihe
 * @date 2020/8/18 9:34 下午
 */
public class SIngleton {
    public static void test() {
        System.out.println("hello");
    }
    private static class LazyHolder {
        static {
            System.out.println("init");
        }
        private static final SIngleton s = new SIngleton();
    }
    public static SIngleton getInstance() {
        return LazyHolder.s;
    }

}
class Test {
    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> aClass = Class.forName("java.lang.String");
        System.out.println(aClass.getClassLoader());

    }
}