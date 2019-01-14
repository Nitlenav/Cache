package ru.practice.lyakh.aleksandr;

import java.io.IOException;
import java.util.Date;

public class MainProject {
    public static void main(String[] args) throws IOException {
        TwoLevelCache twoLevelCache = new TwoLevelCache(2,3,"D:\\TMP_PRACTICE");
        Integer a = new Integer(1);
        Integer b = new Integer(2);
        Integer c = new Integer(3);
        Integer i = new Integer(4);

        twoLevelCache.put(a, "asdfasdfasd");
        twoLevelCache.put(b, new StringBuffer("askdjfhkjasd"));
        twoLevelCache.put(c, new Date());
        twoLevelCache.put(i, new StringBuffer());

        System.out.println(twoLevelCache.get(a));
        System.out.println(twoLevelCache.get(b));
        System.out.println(twoLevelCache.get(c));
        System.out.println(twoLevelCache.get(4));
    }
}
