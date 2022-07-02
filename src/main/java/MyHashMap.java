import java.util.*;
import java.util.function.Consumer;

public class MyHashMap<K, V> implements MyMap<K, V> {
    static final float THE_FULLNESS_OF_THE_ARRAY = 0.75F;
    static final int START_SIZE_OF_ARRAY = 16;


    static class Bucket<K, V> implements Map.Entry<K, V> {
        private int hash;
        private K key;
        private V value;
        private Bucket<K, V> next;

        Bucket(int hash, K key, V value, Bucket<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final String toString() {
            return key + " = " + value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            this.value = value;
            return value;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            return (o instanceof Map.Entry<?, ?>
                    && ((Map.Entry<?, ?>) o).getKey() == this.key
                    && ((Map.Entry<?, ?>) o).getValue() == this.value);
        }

        public int hashCode() {
            return (Objects.hash(next, hash, key, value));
        }

        private void setHash(int hash) {
            this.hash = hash;
        }

        private boolean equalsKey(K key) {
            return this.key.equals(key);
        }
    }

    private Bucket<K, V>[] arrayHash;
    private int size;
    private int capacityFull;
    private int capacity;

    public MyHashMap() {
        arrayHash = new Bucket[START_SIZE_OF_ARRAY];
        this.size = START_SIZE_OF_ARRAY;
        this.capacity = 0;
    }

    public MyHashMap(int size) {
        arrayHash = new Bucket[size];
        this.size = size;
        this.capacity = 0;
    }

    public int getSize() {
        return size;
    }

    private void setSize(int size) {
        this.size = size;
    }

    private void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("{");

        for (Bucket<K, V> bucket : arrayHash) {
            for (; bucket != null; bucket = bucket.next) {
                str.append(bucket.toString());
                str.append(" , ");
            }
        }
        if (str.length() >= 3) {
            str.setLength(str.length() - 3);
        }
        str.append(" }");
        return str.toString();
    }

    private void setCapacityFull(int capacityFull) {
        this.capacityFull = capacityFull;
    }

    public Object cloneFull() {
        MyHashMap<K, V> map = new MyHashMap<>(getSize());
        for (Bucket<K, V> bucket : arrayHash) {
            for (; bucket != null; bucket = bucket.next) {
                map.put(bucket.getKey(), bucket.getValue());
            }
        }
        return map;
    }

    public Set<K> keySet() {
        return new KeySet();
    }

    final class EntryIterator implements Iterator<Map.Entry<K, V>> {
        Bucket<K, V> nextBucket;
        Bucket<K, V> currentBucket;
        int index;

        EntryIterator() {
            currentBucket = null;
            index = 0;
            if (capacityFull > 0) {
                while (index < size && (nextBucket = arrayHash[index++]) == null) ;
            }
        }

        public boolean hasNext() {
            return nextBucket != null;
        }

        public Map.Entry<K, V> next() {
            Bucket<K, V> bucket = nextBucket;
            if (bucket == null) {
                throw new NoSuchElementException();
            }
            if (bucket.next != null) {
                currentBucket = nextBucket;
                nextBucket = bucket.next;
            } else {
                for (; size > index; index++) {
                    if (arrayHash[index] != null) {
                        currentBucket = nextBucket;
                        nextBucket = arrayHash[index++];
                        break;
                    }
                }
                if (size == index) nextBucket = null;
            }
            return bucket;
        }
    }

    final class ValueIterator implements Iterator<V> {
        Bucket<K, V> nextBucket;
        Bucket<K, V> currentBucket;
        int index;

        ValueIterator() {
            currentBucket = null;
            index = 0;
            if (capacityFull > 0) {
                while (size > index && (nextBucket = arrayHash[index++]) == null) ;
            }
        }

        public void remove() {
            removeNode(currentBucket.key);
        }

        public boolean hasNext() {
            return nextBucket != null;
        }

        public V next() {
            Bucket<K, V> bucket = nextBucket;
            if (bucket == null) {
                throw new NoSuchElementException();
            }
            if (bucket.next != null) {
                currentBucket = nextBucket;
                nextBucket = bucket.next;
            } else {
                for (; size > index; index++) {
                    if (arrayHash[index] != null) {
                        currentBucket = nextBucket;
                        nextBucket = arrayHash[index++];
                        break;
                    }
                }
                if (size == index) nextBucket = null;
            }
            return bucket.value;
        }
    }

    final class KeyIterator implements Iterator<K> {
        Bucket<K, V> nextBucket;
        Bucket<K, V> currentBucket;
        int index;

        KeyIterator() {
            currentBucket = null;
            index = 0;
            if (capacityFull > 0) {
                while (size > index && (nextBucket = arrayHash[index++]) == null) ;
            }
        }

        public void remove() {
            removeNode(currentBucket.key);
        }

        public boolean hasNext() {
            return nextBucket != null;
        }

        public K next() {
            Bucket<K, V> bucket = nextBucket;
            if (bucket == null) {
                throw new NoSuchElementException();
            }
            if (bucket.next != null) {
                currentBucket = nextBucket;
                nextBucket = bucket.next;
            } else {
                for (; size > index; index++) {
                    if (arrayHash[index] != null) {
                        currentBucket = nextBucket;
                        nextBucket = arrayHash[index++];
                        break;
                    }
                }
                if (size == index) nextBucket = null;
            }
            return bucket.key;
        }
    }

    public Set<Map.Entry<K, V>> setEntry() {
        return new SetEntry();
    }


    final class KeySet extends AbstractSet<K> {

        public int size() {
            return capacityFull;
        }

        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        public void clear() {
            MyHashMap.this.clear();
        }

        public boolean contains(Object key) {
            return get(key) != null;
        }

        public boolean remove(Object key) {
            return (MyHashMap.this.remove(key) != null);
        }

        public Object[] toArray() {
            int index = 0;
            Object[] arr = new Object[capacityFull];
            if (capacityFull > 0) {
                for (Bucket<K, V> bucket : arrayHash) {
                    for (; bucket != null; bucket = bucket.next, index++) {
                        arr[index] = bucket.key;
                    }
                }
            }
            return arr;
        }

        public void forEach(Consumer<? super K> action) {
            if (action == null)
                throw new NullPointerException();
            else {
                for (Bucket<K, V> bucket : arrayHash) {
                    for (; bucket != null; bucket = bucket.next)
                        action.accept(bucket.key);
                }
            }
        }
    }

    public Collection<V> values() {
        return new Values();
    }

    class Values extends AbstractCollection<V> {
        public final int size() {
            return capacityFull;
        }

        public final void clear() {
            MyHashMap.this.clear();
        }

        public final Iterator<V> iterator() {
            return new ValueIterator();
        }

        public final boolean contains(Object o) {
            return MyHashMap.this.containsValue(o);
        }

        public final void forEach(Consumer<? super V> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            if (capacityFull > 0) {
                for (Bucket<K, V> bucket : arrayHash) {
                    for (; bucket != null; bucket = bucket.next) {
                        action.accept(bucket.value);
                    }
                }
            }
        }
    }

    class SetEntry extends AbstractSet<Map.Entry<K, V>> {

        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        public int size() {
            return (capacityFull);
        }

        public void clear() {
            MyHashMap.this.clear();
        }

        public boolean contains(Object o) {
            if (o instanceof Map.Entry<?, ?>) {
                Map.Entry<?, ?> o1 = (Map.Entry<?, ?>) o;
                return getNode(o1.getKey()).equals(o1);
            }
            return false;
        }

        public boolean remove(Object o) {
            if (o instanceof Map.Entry<?, ?>) {
                return (removeNode(((Map.Entry<?, ?>) o).getKey()) != null);
            }
            return false;
        }

        public void forEach(Consumer<? super Map.Entry<K, V>> action) {
            if (action == null)
                throw new NullPointerException();
            if (capacityFull > 0) {
                for (Bucket<K, V> bucket : arrayHash) {
                    for (; bucket != null; bucket = bucket.next) {
                        action.accept(bucket);
                    }
                }
            }
        }
    }

    public V replace(K key, V value) {
        Bucket<K, V> bucket = getNode(key);
        V returnValue;
        if (bucket != null) {
            returnValue = bucket.value;
            bucket.value = value;
            return returnValue;
        }
        return null;
    }

    public V put(K key, V value) {
        V oldValue = null;
        if (key == null) {
            Bucket<K, V> bucket = arrayHash[0];
            if (bucket == null) {
                arrayHash[0] = new Bucket<>(0, null, value, null);
                setCapacity(++capacity);
                setCapacityFull(++capacityFull);
                return null;
            } else {
                oldValue = bucket.getValue();
                arrayHash[0] = new Bucket<>(0, null, value, null);
                return oldValue;
            }
        }
        int hashCode = myHashCode(key);
        int index = indexOf(hashCode, this.size);
        // если в индексе массива нет элементов
        if (this.arrayHash[index] == null) {
            arrayHash[index] = new Bucket<>(hashCode, key, value, null);
            setCapacity(++capacity);
            setCapacityFull(++capacityFull);
            if (capacity > (THE_FULLNESS_OF_THE_ARRAY * size))
                resizeArray();
            return null;
        } else {
            // если в индексе массива есть элемент пробегаемся по списку, ищем элемент с таким же хэшем
            Bucket<K, V> bucket = searchBucketWithHashcode(arrayHash[index], hashCode);
            // если елемента с таким хэшем нет, дописываем в конец списка
            if (bucket == null) {
                bucket = getLastBuket(arrayHash[index]);
                bucket.next = new Bucket<>(hashCode, key, value, null);
                setCapacityFull(++capacityFull);
                return null;
            } else {
                // если элемент с таким хэшем есть, сравниваем ключи
                // если ключи совпадают, то перезаписываем Value
                if (bucket.equalsKey(key)) {
                    bucket.setValue(value);
                    // если ключи не совпадают, тогда возникает коллизия, добавляем в список
                } else {
                    bucket = getLastBuket(arrayHash[index]);
                    bucket.next = new Bucket<>(hashCode, key, value, null);
                    setCapacityFull(++capacityFull);
                    return null;
                }
                oldValue = arrayHash[index].getValue();
                arrayHash[index].setValue(value);
                setCapacityFull(++capacityFull);
                return oldValue;
            }
        }
    }

    @Override
    public V get(Object key) {
        return (getNode(key).getValue());
    }

    @Override
    public V remove(Object key) {
        return removeNode(key).getValue();
    }

    @Override
    public void clear() {
        for (Bucket<K, V> bucket : arrayHash) {
            bucket = null;
        }
    }

    @Override
    public boolean containsValue(Object value) {
        for (Bucket<K, V> bucket : arrayHash) {
            for (; bucket.next != null; bucket = bucket.next) {
                if (bucket.value == value || (value != null && value.equals(bucket))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        Bucket<K, V> bucket = arrayHash[indexOf(myHashCode(key), size)];
        for (; bucket != null; bucket = bucket.next) {
            if (bucket.key == key || (key != null && key.equals(bucket))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void print() {
        for (Bucket<K, V> hash : arrayHash) {
            if (hash != null)
                System.out.println(hash);
        }
    }

    @Override
    public boolean isEmpty() {
        return capacity == 0;
    }

    @Override
    public void forEach(Consumer action) {
        for (Bucket<K, V> bucket : arrayHash) {
            if (action == null) {
                throw new NullPointerException();
            }
            for (; bucket != null; bucket = bucket.next) {
                action.accept(bucket);
            }
        }
    }

    private Bucket<K, V> searchBucketWithHashcode(Bucket<K, V> first, int hash) {
        for (; first != null; first = first.next) {
            if (first.hash == hash) {
                return first;
            }
        }
        ;
        return null;
    }

    private Bucket<K, V> getLastBuket(Bucket<K, V> first) {
        while (first.next != null)
            first = first.next;
        return first;
    }

    private Bucket<K, V> searchByKey(Bucket<K, V> first, K key) {
        Bucket<K, V> temp = first;
        while (temp.key != key) {
            if (temp.next == null)
                return null;
            temp = temp.next;
        }
        return null;
    }

    private void resizeArray() {
        Bucket<K, V>[] arrayHashCopy = arrayHash;
        setSize(size * 2);
        capacity = 0;
        int index;
        int hash;
        Bucket<K, V> bucketInSmallArray;
        Bucket<K, V> bucketInBigArray;
        arrayHash = new Bucket[size];
        // пробегаемся по малому массиву
        for (Bucket<K, V> bucket : arrayHashCopy) {
            //копируем ссылку на первый элемент связного списка он же i-й элемент массива
            bucketInSmallArray = bucket;
            //если элемент не пустой
            if (bucketInSmallArray != null) {
                //вычисляем его позицию в большом масиве
                hash = myHashCode((K) bucketInSmallArray.getKey());
                index = indexOf(hash, size);
                bucketInBigArray = arrayHash[index];
                //если i-й элемент массива пустой, вставляем его в новый массив переписывая все хэши в списке
                if (bucketInBigArray == null) {
                    setHashForListBuckets(bucketInBigArray, hash);
                    arrayHash[index] = bucketInSmallArray;
                    setCapacity(++capacity);
                } else {
                    bucketInBigArray = getLastBuket(bucketInBigArray);
                    setHashForListBuckets(bucketInSmallArray, hash);
                    bucketInBigArray.next = bucketInSmallArray;
                }
            }
        }
    }

    private void setHashForListBuckets(Bucket<K, V> bucket, int hash) {
        while (bucket != null) {
            bucket.setHash(hash);
            bucket = bucket.next;
        }
    }

    private int indexOf(int hash, int size) {
        return hash % size;
    }

    private int myHashCode(Object key) {
        return key.hashCode();
    }

    private Bucket<K, V> getNode(Object key) {
        if (key == null){
            return arrayHash[0];
        }
        int hash = myHashCode(key);
        int index = indexOf(hash, size);
        Bucket<K, V> bucketCurent = arrayHash[index];
        if (bucketCurent == null) {
            return null;
        } else {
            for (; bucketCurent != null; bucketCurent = bucketCurent.next) {
                if (key.equals(bucketCurent.key)) {
                    return bucketCurent;
                }
            }
            return null;
        }
    }

    private Bucket<K, V> removeNode(Object key) {
        int hash = myHashCode(key);
        int index = indexOf(hash, size);
        Bucket<K, V> bucketCurent = arrayHash[index];
        Bucket<K, V> bucketPrevious = null;
        Bucket<K, V> returnBucket;
        int counter = 0;
        // прбегаемся по элементам"списка"
        for (; bucketCurent != null; bucketPrevious = bucketCurent, bucketCurent = bucketCurent.next, counter++) {
            if (key == bucketCurent.key || (key != null && key.equals(bucketCurent))) {
                //если элемент первый в списке
                if (counter == 0) {
                    returnBucket = arrayHash[index];
                    arrayHash[index] = bucketCurent.next;
                    return returnBucket;
                } else {
                    //если элемент последний в списке
                    if (bucketCurent.next == null) {
                        returnBucket = bucketCurent;
                        bucketPrevious = null;
                        return returnBucket;
                    } else {
                        //если элемент в середине списка
                        returnBucket = bucketCurent;
                        bucketPrevious.next = bucketCurent.next;
                        return null;
                    }
                }
            }
        }
        return null;
    }
}