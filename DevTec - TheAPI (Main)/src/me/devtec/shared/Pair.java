package me.devtec.shared;

public class Pair {
    Object key, value;

    public Pair(Object o, Object o1) {
        key=o;
        value=o1;
    }

    public Object getKey(){
        return key;
    }

    public Object getValue(){
        return value;
    }

    public String toString(){
        return "Pair{"+key+"="+value+"}";
    }
}