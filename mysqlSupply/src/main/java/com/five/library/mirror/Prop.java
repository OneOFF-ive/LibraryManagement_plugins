package com.five.library.mirror;

public class Prop {
    public Getter getter;
    public Setter setter;

    public Prop() {}
    public Prop(Getter getter, Setter setter) {
        this.getter = getter;
        this.setter = setter;
    }
}
