import java.util.function.Consumer;

public interface MyMap<K, V> {
    V put(K key, V value);

    V get(K key);

    V remove(Object key);

    void clear();

    boolean containsValue(Object value);

    boolean containsKey(Object key);

    void print();

    boolean isEmpty();

    void forEach(Consumer action);

    interface MyEntry<K, V> {
        K getKey();

        V getValue();

        void setValue(V value);

        boolean equals(MyHashMap.Bucket<K, V> bucket);

        int hashCode();
    }
}
