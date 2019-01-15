package ru.practice.lyakh.aleksandr;

import ru.practice.lyakh.aleksandr.cache.Cache;
import ru.practice.lyakh.aleksandr.cache.FileSystemCache;
import ru.practice.lyakh.aleksandr.cache.MemoryCache;
import ru.practice.lyakh.aleksandr.exception.OutOfMemoryCache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TwoLevelCache<K, V extends Serializable> implements Cache<K, V> {
    private final MemoryCache<K, V> memoryCache;
    private final FileSystemCache<K, V> fileSystemCache;
    private Map returnMemoryCash;
    private final int maxSizeMemoryCache;
    private final int maxSizeFileSystemCache;
    private int sizeCashe;
    private int freeSpace;

    //Инициализация хэш таблицы и количества потоков,которые смогут обращаться к различным ячейкам
    public TwoLevelCache(int maxSizeMemoryCache, int maxSizeFileSystemCache) {
        this.maxSizeMemoryCache = maxSizeMemoryCache;
        this.maxSizeFileSystemCache = maxSizeFileSystemCache;
        this.memoryCache = new MemoryCache<K, V>(maxSizeMemoryCache);
        this.fileSystemCache = new FileSystemCache<K, V>(maxSizeFileSystemCache);
        this.returnMemoryCash = new HashMap();
    }
    //Инициализация хэш таблицы и количества потоков,которые смогут обращаться к различным ячейкам и путь к TMP файду
    public TwoLevelCache(int maxSizeMemoryCache, int maxSizeFileSystemCache, String path) throws IOException {
        this.maxSizeMemoryCache = maxSizeMemoryCache;
        this.maxSizeFileSystemCache = maxSizeFileSystemCache;
        this.memoryCache = new MemoryCache<K, V>(maxSizeMemoryCache);
        this.fileSystemCache = new FileSystemCache<K, V>(maxSizeFileSystemCache, path);
        this.returnMemoryCash = new HashMap();
    }

    //Размер хэш таблицы
    @Override
    public int size() {
        sizeCashe = memoryCache.size() + fileSystemCache.size();
        return sizeCashe;
    }

    //Свободное пространство в таблице
    @Override
    public int emptySpace() {
        freeSpace = maxSizeMemoryCache + maxSizeFileSystemCache - size();
        return freeSpace;
    }

    //Запись данных в хэш таблицу, при условии, если есть свободное место,
    // определяемое переменной класса maxSizeMemoryCache
    @Override
    public boolean put(K key, V value) throws IOException {
        boolean emptyMemorySpace = memoryCache.emptySpace() > 0;
        boolean emptyFileSystemSpace = fileSystemCache.emptySpace() > 0;
        //Условие при котором данных нет ми в MemoryCache, ни в FileSystemCache,
        // но свободное место в MemoryCache есть.
        if (!(memoryCache.containsKey(key)
                && fileSystemCache.containsKey(key))
                && emptyMemorySpace) {
            return memoryCache.put(key, value);
            //Перезаписываем данные в MemoryCache
        }  else if (memoryCache.containsKey(key)) {
            return memoryCache.put(key, value);
            //Условие при котором данные в FileSystemCache есть,
            // тогда переносим данные в MemoryCache а последний обьект из MemoryCache
            // переносится в FileSystemCache.
        } else if (fileSystemCache.containsKey(key)) {
            K lastKeyMemory = memoryCache.lastKey();
            V lastValueMemory = memoryCache.remove(lastKeyMemory);
            fileSystemCache.remove(key);
            memoryCache.put(key, value);
            return fileSystemCache.put(lastKeyMemory, lastValueMemory);
            //Отсуствуют данные и в MemoryCache и в FileSystemCache, при наличии свободного места
            //переносим последний обьект из MemoryCache в FileSystemCache
            //а в MemoryCache записываем отсуствующий ключ и значение.
        } else if (emptyFileSystemSpace) {
            K lastKeyMemory = memoryCache.lastKey();
            V lastValueMemory = memoryCache.remove(lastKeyMemory);
            fileSystemCache.put(lastKeyMemory, lastValueMemory);
            return memoryCache.put(key, value);
        } else {
            return false;
        }
    }

    //Получение данных с хэш таблицы
    @Override
    public V get(Object key) throws FileNotFoundException {
        if (memoryCache.containsKey(key)) {
            return memoryCache.get(key);
        } else if (fileSystemCache.containsKey(key)) {
            return fileSystemCache.get(key);
        } else {
            throw new OutOfMemoryCache("No data in two level cache");
        }
    }

    //Очистка всю хэш таблицу
    @Override
    public void clear() {
        memoryCache.clear();
        fileSystemCache.clear();
    }

    //Возврат удалённых данных, по ключу
    @Override
    public V remove(Object key) {
        if (memoryCache.containsKey(key)) {
            return memoryCache.remove(key);
        } else if (fileSystemCache.containsKey(key)) {
            return fileSystemCache.remove(key);
        } else return null;
    }

    //Проверяем, имеются данные в хэш таблице, по ключу
    @Override
    public boolean containsKey(Object key) {
        return memoryCache.containsKey(key) ? true : fileSystemCache.containsKey(key) ? true : false;
    }

    //Проверяем, имеются данные в хэш таблице, по Обьекту
    @Override
    public boolean containsValue(Object value) {
        return memoryCache.containsValue(value) ? true : fileSystemCache.containsValue(value) ? true : false;
    }

    //Получение первого ключа
    @Override
    public K firstKey() {
        return fileSystemCache.lastKey();
    }

    //Получение последнего ключа
    @Override
    public K lastKey() {
        return memoryCache.firstKey();
    }

    //Возврат карты для полечения данных в цикле
    public Map<K, V> getReturnMemoryCash() {
        returnMemoryCash.putAll(memoryCache.getMemoryObjects());
        returnMemoryCash.putAll(fileSystemCache.getReturnMemory());
        return returnMemoryCash;
    }
}
