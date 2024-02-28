package core.basesyntax;

import java.util.Map;
import java.util.Objects;

public class MyHashMap<K, V> implements MyMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final int DEFAULT_SIZE_MULTIPLAYER = 2;
    private int loadFactor = 12;
    private Node<K, V>[] storage;
    private int size;

    public MyHashMap() {
        storage = new Node[DEFAULT_CAPACITY];
    }

    @Override
    public void put(K key, V value) {
        if (size == loadFactor) {
            resize();
        }
        int index = getIndex(key);
        Node<K, V> currentNode = storage[index];
        Node<K, V> newNode = new Node<>(key, value, null);
        if (currentNode == null) {
            storage[index] = newNode;
        } else {
            while (true) {
                if (Objects.equals(key, currentNode.key)) {
                    currentNode.value = value;
                    return;
                } else if (!currentNode.hasNext()) {
                    currentNode.next = newNode;
                    break;
                }
                currentNode = getNextNode(currentNode);
            }
        }
        size++;
    }

    @Override
    public V getValue(K key) {
        Node<K, V> node = getNodeByKey(key);
        return node != null ? node.value : null;
    }

    @Override
    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(K key) {
        return getNodeByKey(key) != null;
    }

    public boolean containsValue(V value) {
        for (Node<K, V> node : storage) {
            while (node != null) {
                if (Objects.equals(node.value, value)) {
                    return true;
                }
                node = node.next;
            }
        }
        return false;
    }

    public K remove(K key) {
        Node<K, V> node = getNodeByKey(key);
        if (node != null) {
            int index = getIndex(key);
            if (storage[index].key.equals(key)) {
                storage[index] = node.next;
            } else {
                while (node.hasNext()) {
                    if (node.next.key.equals(key)) {
                        node.next = node.next.next;
                        break;
                    }
                    node = node.next;
                }
            }
            size--;
            return key;
        }
        return null;
    }

    public void putAll(Map<K, V> m) {
        for (Map.Entry<K, V> value : m.entrySet()) {
            put(value.getKey(), value.getValue());
        }
    }

    public void clear() {
        storage = new Node[DEFAULT_CAPACITY];
        size = 0;
    }

    private Node<K, V> getNextNode(Node<K, V> currentNode) {
        return currentNode.next == null ? null : currentNode.next;
    }

    private void resize() {
        loadFactor = loadFactor * DEFAULT_SIZE_MULTIPLAYER;
        size = 0;
        Node<K, V>[] oldStorage = storage;
        storage = new Node[storage.length * DEFAULT_SIZE_MULTIPLAYER];
        for (Node<K, V> node : oldStorage) {
            while (node != null) {
                put(node.key, node.value);
                node = node.next;
            }
        }
    }

    private Node<K, V> getNodeByKey(K key) {
        Node<K, V> currentNode = storage[getIndex(key)];
        while (currentNode != null) {
            if (Objects.equals(currentNode.key, key)) {
                return currentNode;
            }
            currentNode = getNextNode(currentNode);
        }
        return null;
    }

    private int getIndex(K key) {
        return key == null ? 0 : Math.abs(key.hashCode()) % storage.length;
    }

    private static class Node<K, V> {
        private K key;
        private V value;
        private Node<K, V> next;

        private Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        private boolean hasNext() {
            return next != null;
        }
    }
}
