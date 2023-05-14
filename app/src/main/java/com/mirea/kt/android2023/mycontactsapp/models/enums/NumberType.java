package com.mirea.kt.android2023.mycontactsapp.models.enums;

public enum NumberType {

    NONE("NONE"), HOME("HOME"), WORKER("WORKER"), CELLULAR("CELLULAR");

    private String name;

    NumberType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
