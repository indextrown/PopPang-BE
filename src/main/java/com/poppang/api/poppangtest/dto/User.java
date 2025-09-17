package com.poppang.api.poppangtest.dto;

public class User {
    private String name;
    private int age;

    // 생성자
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // getter (json 변환을 위함)
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}