package com.thoughtworks.jwt.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {

    @GetMapping("/welcome")
    public String welcome(){
        String str = "This is private page";
        str += "This page is not allowed for unauthenticated users";
        System.out.println("PROTECTED ROUTE : : "+str);
        return str;
    }
    @GetMapping("/noAuth")
    public String noAuth(){
        String str = "This is public page";
        System.out.println("UNPROTECTED ROUTE : : "+str);
        return str;
    }
}
