package ru.practice.lyakh.aleksandr.cache;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileSystemCache<K, V extends Serializable> implements Cache<K, V>{
    private final Map<K, File> memoryObjects;
    private Map<Object, V> returnMemory;
    private List keyFileSystemCache;
    private final int sizeMemoryObjects;
    private String tmpPath;

    //Инициирования размера сохраняемых данных и инициирования TMP директории.
    public FileSystemCache(int sizeMemoryObjects) {
        this.memoryObjects = new ConcurrentHashMap(sizeMemoryObjects);
        this.returnMemory = new HashMap<>();
        this.keyFileSystemCache = new ArrayList();
        this.sizeMemoryObjects = sizeMemoryObjects;
        this.tmpPath = System.getProperty("java.io.tmpdir");
    }

    //Инициирования размера сохраняемых данных и TMP директорию.
    public FileSystemCache(int sizeMemoryObjects, String tmpPath) throws IOException {
        this.sizeMemoryObjects = sizeMemoryObjects;
        this.memoryObjects = new ConcurrentHashMap<>(sizeMemoryObjects);
        this.returnMemory = new HashMap<>();
        this.keyFileSystemCache = new ArrayList();
        if(!(new File(tmpPath).exists())){
            Files.createDirectories(Paths.get(tmpPath));
        }
        this.tmpPath = tmpPath;
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
            keyFileSystemCache.add(key);
            File tempFilePath;
            try (FileOutputStream fileOutputStream = new FileOutputStream(
                    tempFilePath = Files.createTempFile(Paths.get(tmpPath), "TMP", "CASHE").toFile());
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(value);
                objectOutputStream.flush();
                memoryObjects.put(key, tempFilePath);
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    //Получение данных с хэш таблицы
    @Override
    public V get(Object key) {
        if (containsKey(key)) {
            File tempFilePath = memoryObjects.get(key);
            try (FileInputStream fileInputStream = new FileInputStream(tempFilePath);
                 ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                return (V) objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    //Очистка всей хэш таблицы и TMP директории
    @Override
    public void clear() {
        memoryObjects.forEach((K, File) -> {
            File.delete();
        });
        keyFileSystemCache.clear();
        memoryObjects.clear();
    }

    //Возврат удалённых данных, по ключу
    @Override
    public V remove(Object key) {
        keyFileSystemCache.remove(key);
        File remoteFile = memoryObjects.remove(key);
        remoteFile.delete();
        return (V) remoteFile;
    }

    //Проверяем, имеются данные в хэш таблице, по Обьекту и месту расположения
    @Override
    public boolean containsValue(Object value) {
        if (memoryObjects.containsValue(value)) {
            return true;
        } else {
            return false;
        }
    }

    //Проверяем, имеются данные в хэш таблице, по ключу и месту расположения
    @Override
    public boolean containsKey(Object key) {
        //File tempFilePath = get(key);
        if (memoryObjects.containsKey(key) ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public K firstKey() {
        return (K) keyFileSystemCache.get(keyFileSystemCache.size() - 1);
    }

    @Override
    public K lastKey() {
        return (K) keyFileSystemCache.get(0);
    }

    public Map<Object, V> getReturnMemory() {
        keyFileSystemCache.forEach(keyFileSystem -> returnMemory.put( keyFileSystem, get(keyFileSystem)));

        return returnMemory;
    }
}
