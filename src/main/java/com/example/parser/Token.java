package com.example.parser;

public class Token {

    private String x;
    private int id;
    private String type;

    public Token(int id , String x, String type) {
        this.id = id;
        this.x = x;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return x + "   " + id  + "   " + type;
    }

}
