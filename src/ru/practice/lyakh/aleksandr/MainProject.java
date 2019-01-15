package ru.practice.lyakh.aleksandr;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public class MainProject {
    public static void main(String[] args) throws IOException {
        TwoLevelCache twoLevelCache = new TwoLevelCache(2, 6, "D:\\TMP_PRACTICE");
        Integer a = new Integer(1);
        String b = new String("Ключь - ЗОЛОТОЙ");
        Object c = new Object();
        int d = 100;
        String i = "Шутки ради";
// Добавляем данные
        twoLevelCache.put(a, "asdfasdfasd");
        twoLevelCache.put(b, new StringBuffer("askdjfhkjasd"));
        twoLevelCache.put(c, " Какой то обьект");
        twoLevelCache.put(d, new Date());
        twoLevelCache.put(i, new Date());
//Проверяем метод get
        System.out.println(twoLevelCache.get(a));
        System.out.println(twoLevelCache.get(b));
        System.out.println(twoLevelCache.get(c));
        System.out.println(twoLevelCache.get(i));
        System.out.println(twoLevelCache.get(d));
// Выбрасывает исключение  OutOfMemoryCache: No data in two level cache
        //System.out.println(twoLevelCache.get("А здесь ничего не должно быть"));
//Просто реализуем цикл по всему масиву данных
        twoLevelCache.getReturnMemoryCash().forEach((k, v) -> System.out.println(k + " " + v + "\n"));
//Проверяем метод size для возвращаемого масива Map.
        System.out.println(twoLevelCache.getReturnMemoryCash().size());
//Проверяем метод size для всего TwoLevelCache
        System.out.println(twoLevelCache.size());
//Проверяем метод firstKey и lastKey
        System.out.println(twoLevelCache.firstKey() + " " + twoLevelCache.lastKey());
//Проверяем свободное место в TwoLevelCache
        System.out.println("Size empty" + twoLevelCache.emptySpace());
//Проверяем метод containsKey и containsValue
        System.out.println(twoLevelCache.containsKey(d) + " " + twoLevelCache.containsValue("Для того что бы вывел FALSE"));
        System.out.println(twoLevelCache.containsKey("Для того что бы вывел FALSE") + " " + twoLevelCache.containsValue(twoLevelCache.get(d)));
//Проверяем удаления данных как через remove так м через clear
//        twoLevelCache.getReturnMemoryCash().forEach((k, v) -> twoLevelCache.remove(k));
        twoLevelCache.clear();
    }
}
