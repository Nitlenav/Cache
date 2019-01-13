package ru.practice.lyakh.aleksandr.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public interface Cache<K, V> {

    //Получение размера КЭШа
    int size();

    //Получаем размер пустого пространства КЭШа
    int emptySpace();

    //Запись данных в КЭШ
    boolean put(K key, V value) throws IOException;

    //Получаем данные из КЭШа по ключу
    V get(Object key) throws FileNotFoundException;

    //Очищаем КЭШ
    void clear();

    //Удаляем данные по ключу
    V remove(Object key);

    //Проверяем наличие данных
    boolean containsValue(Object value);

    //Проверяем наличие данных по ключу
    boolean containsKey(Object key);

    //Получаем первый загруженный ключ
    K firstKey();

    //Получаем последний загруженный ключ
    K lastKey();

}
