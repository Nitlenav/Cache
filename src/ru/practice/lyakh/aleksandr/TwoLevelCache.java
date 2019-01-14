package ru.practice.lyakh.aleksandr;

import ru.practice.lyakh.aleksandr.cache.Cache;
import ru.practice.lyakh.aleksandr.cache.FileSystemCache;
import ru.practice.lyakh.aleksandr.cache.MemoryCache;
import ru.practice.lyakh.aleksandr.exception.OutOfMemoryCache;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public class TwoLevelCache<K, V extends Serializable> implements Cache<K, V> {
    private final MemoryCache<K, V> memoryCache;
    private final FileSystemCache<K, V> fileSystemCache;
    private Map <K, V> returnMemoryCash;
    private final int maxSizeMemoryCache;
    private final int maxSizeFileSystemCache;
    private int sizeCashe;
    private int freeSpace;
    private long timerKey;


    public TwoLevelCache(int maxSizeMemoryCache, int maxSizeFileSystemCache) {
        this.maxSizeMemoryCache = maxSizeMemoryCache;
        this.maxSizeFileSystemCache = maxSizeFileSystemCache;
        this.memoryCache = new MemoryCache<K, V>(maxSizeMemoryCache);
        this.fileSystemCache = new FileSystemCache<K, V>(maxSizeFileSystemCache);
    }

    public TwoLevelCache(int maxSizeMemoryCache, int maxSizeFileSystemCache, String path) {
        this.maxSizeMemoryCache = maxSizeMemoryCache;
        this.maxSizeFileSystemCache = maxSizeFileSystemCache;
        this.memoryCache = new MemoryCache<K, V>(maxSizeMemoryCache);
        this.fileSystemCache = new FileSystemCache<K, V>(maxSizeFileSystemCache, path);
    }

    @Override
    public int size() {
        sizeCashe = memoryCache.size() + fileSystemCache.size();
        return sizeCashe;
    }

    @Override
    public int emptySpace() {
        freeSpace = maxSizeMemoryCache + maxSizeFileSystemCache - size();
        return freeSpace;
    }

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
        } else if (memoryCache.containsKey(key)) {
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

    @Override
    public void clear() {
        memoryCache.clear();
        fileSystemCache.clear();
    }

    @Override
    public V remove(Object key) {
        if (memoryCache.containsKey(key)) {
            return memoryCache.remove(key);
        } else if (fileSystemCache.containsKey(key)) {
            return fileSystemCache.remove(key);
        } else return null;
    }

    @Override
    public boolean containsKey(Object key) {
        return memoryCache.containsKey(key) ? true : fileSystemCache.containsKey(key) ? true : false;
    }

    @Override
    public boolean containsValue(Object value) {
        return memoryCache.containsValue(value) ? true : fileSystemCache.containsValue(value) ? true : false;
    }

    @Override
    public K firstKey() {
        return memoryCache.firstKey();
    }

    @Override
    public K lastKey() {
        return fileSystemCache.lastKey();
    }

    public Map<K, V> getReturnMemoryCash() {
        returnMemoryCash.putAll( memoryCache.getMemoryObjects());
        returnMemoryCash.putAll(fileSystemCache.getReturnMemory());
        return returnMemoryCash;
    }
}
