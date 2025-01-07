package userend;

import userend.mixin.TargetClass;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("userend.mixin.InjectorClass");

        TargetClass target = new TargetClass("Super Duper Secret Message");
        target.doStuff();
    }
}
