package main.java.mg.itu.framework.sample;

import itu.framework.annotations.MyController;

@MyController("Sample")
public class SampleController {

    public String hello() {
        return "Hello from SampleController";
    }

}
