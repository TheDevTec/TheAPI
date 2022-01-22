package me.devtec.shared;

public class Pair {
    private Object key;
    private Object value;

    public Pair(Object key, Object value) {
        this.key=key;
        this. value=value;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        return "Pair{"+key+"="+value+"}";
    }
}