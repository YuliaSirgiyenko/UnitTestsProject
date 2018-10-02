package com.sirgiyenko;

public class IdGenerator {

    private static long id = 0;

    long nextId(){
        return id+1;
    }

}
