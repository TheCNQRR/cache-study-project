package by.java.enterprise;

public class CacheEntry<V> {
    private V value;
    public V getValue() {
        return value;
    }
    public void setValue(V value) {
        this.value = value;
    }

    private Long expiredAt;
    public Long getExpiredAt() {
        return expiredAt;
    }
    public void setExpiredAt(Long expiredAt) {
        this.expiredAt = expiredAt;
    }
}
