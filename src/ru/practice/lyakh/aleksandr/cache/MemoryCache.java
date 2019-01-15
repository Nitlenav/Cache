package ru.practice.lyakh.aleksandr.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCache<K, V> implements Cache<K, V> {
    private final Map<K, V> memoryObjects;
    private List keyMemoryObjects;
    private final int sizeMemoryObjects;

    //Инициализация хэш таблицы и количества потоков,которые смогут обращаться к различным ячейкам
    public MemoryCache(int sizeMemoryObjects) {
        this.sizeMemoryObjects = sizeMemoryObjects;
        this.keyMemoryObjects = new ArrayList();
        this.memoryObjects = new ConcurrentHashMap<>(sizeMemoryObjects);

    }

    //Размер хэш таблицы
    @Override
    public int size() {
        return this.memoryObjects.size();
    }

    //Свободное пространство в таблице
    @Override
    public int emptySpace() {
        int coutFreeSpace = sizeMemoryObjects - size();
        return coutFreeSpace;
    }

    //Запись данных в хэш таблицу, при условии, если есть свободное место,
    // определяемое переменной класса sizeMemoryObjects
    @Override
    public boolean put(K key, V value) {
        if (emptySpace() > 0) {
            memoryObjects.put(key, value);
            keyMemoryObjects.add(key);
            return true;
        } else {
            return false;
        }
    }

    //Получение данных с хэш таблицы
    @Override
    public V get(Object key) {
        return memoryObjects.get(key);
    }

    //Очистка всю хэш таблицу
    @Override
    public void clear() {
        keyMemoryObjects.clear();
        memoryObjects.clear();
    }

    //Возврат удалённых данных, по ключу
    @Override
    public V remove(Object key) {
        keyMemoryObjects.remove(key);
        return memoryObjects.remove(key);
    }


    //Проверяем, имеются данные в хэш таблице, по ключу
    @Override
    public boolean containsKey(Object key) {
        return memoryObjects.containsKey(key);
    }

    //Проверяем, имеются данные в хэш таблице, по Обьекту
    @Override
    public boolean containsValue(Object value) {
        return memoryObjects.containsValue(value);
    }

    //Получение первого ключа
    @Override
    public K firstKey() {
        return (K) keyMemoryObjects.get(keyMemoryObjects.size() - 1 );
    }

    //Получение последнего ключа
    @Override
    public K lastKey() {
        return (K) keyMemoryObjects.get(0);
    }

    //Возврат карты для полечения данных в цикле
    public Map<K, V> getMemoryObjects() {
        return memoryObjects;
    }

}
