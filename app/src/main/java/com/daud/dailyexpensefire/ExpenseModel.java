package com.daud.dailyexpensefire;

public class ExpenseModel {
    private String key;
    private String type;
    private int amount;
    private String date;
    private String time;
    private String doc;

    public ExpenseModel() {
    }

    public ExpenseModel(String key, String type, int amount, String date, String time, String doc) {
        this.key = key;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.doc = doc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }
}
