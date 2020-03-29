package me.Straiker123;

import java.io.Serializable;

public abstract class SerializableRunnable  implements Serializable, Runnable{

    private static final long serialVersionUID = 6217172014399079306L;

    @Override
    public abstract void run();

}