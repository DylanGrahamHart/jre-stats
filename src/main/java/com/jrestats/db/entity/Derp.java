package com.jrestats.db.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Derp {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    public String name;

    public Derp() { }

    public Derp(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{id:" + id + ", name:" + name + "}";
    }
}