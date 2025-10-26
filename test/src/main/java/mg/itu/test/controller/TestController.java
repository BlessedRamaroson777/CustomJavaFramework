package mg.itu.test.controller;

import itu.framework.annotations.MyURL;

public class TestController {
    
    public String index() {
        return "Hello from Test Controller!";
    }
    
    @MyURL("/test")
    public String test() {
        return "Test method called!";
    }
}
