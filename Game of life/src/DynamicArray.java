import java.util.Iterator;
import java.util.NoSuchElementException;

public class DynamicArray<T> implements Iterable<T> {

    private static final int INITCAP = 2;
    private T[] storage;
    private int size;

    @SuppressWarnings("unchecked")
    public DynamicArray() {
        this.storage = (T[]) new Object[INITCAP];
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    public DynamicArray(int initCapacity) {
        if (initCapacity < 1) {
            throw new IllegalArgumentException("Capacity cannot be zero or negative.");
        }
        this.storage = (T[]) new Object[initCapacity];
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public int capacity() {
        return this.storage.length;
    }

    public T set(int index, T value) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + this.size);
        }
        T oldValue = this.storage[index];
        this.storage[index] = value;
        return oldValue;
    }

    public T get(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + this.size);
        }
        return this.storage[index];
    }

    @SuppressWarnings("unchecked")
    public boolean add(T value) {
        if (this.size == this.storage.length) {
            T[] newStorage = (T[]) new Object[this.storage.length * 2];
            for (int i = 0; i < this.size; i++) {
                newStorage[i] = this.storage[i];
            }
            this.storage = newStorage;
        }
        this.storage[this.size] = value;
        this.size++;
        return true;
    }

    @SuppressWarnings("unchecked")
    public void add(int index, T value) {
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + this.size);
        }
        if (this.size == this.storage.length) {
            T[] newStorage = (T[]) new Object[this.storage.length * 2];
            for (int i = 0; i < this.size; i++) {
                newStorage[i] = this.storage[i];
            }
            this.storage = newStorage;
        }
        for (int i = this.size; i > index; i--) {
            this.storage[i] = this.storage[i - 1];
        }
        this.storage[index] = value;
        this.size++;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + this.size);
        }
        T removedValue = this.storage[index];
        for (int i = index; i < this.size - 1; i++) {
            this.storage[i] = this.storage[i + 1];
        }
        this.storage[this.size - 1] = null;
        this.size--;

        if (this.capacity() > INITCAP && this.size < this.capacity() / 3) {
            int newCapacity = this.capacity() / 2;
            if (newCapacity < INITCAP) {
                newCapacity = INITCAP;
            }
            T[] newStorage = (T[]) new Object[newCapacity];
            for (int i = 0; i < this.size; i++) {
                newStorage[i] = this.storage[i];
            }
            this.storage = newStorage;
        }
        return removedValue;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentIndex = 0;

            public boolean hasNext() {
                return currentIndex < size;
            }

            public T next() {
                if (hasNext() == false) {
                    throw new NoSuchElementException();
                }
                T value = storage[currentIndex];
                currentIndex++;
                return value;
            }
        };
    }

    public String toString() {
        StringBuilder s = new StringBuilder("Dynamic array with " + size() + " items and a capacity of " + capacity() + ":");
        for (int i = 0; i < size(); i++) {
            s.append("\n  [" + i + "]: " + get(i));
        }
        return s.toString();
    }
}