package com.example.demo.descriptior;

import com.example.demo.stuct.LYopRequest;
import com.example.demo.stuct.ResponseDemo;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Component
@EnableAutoConfiguration
@RestController
public interface UserService {


    @RequestMapping(value = "/",produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
    public ModelAndView sign();

    @RequestMapping(value = "/registry",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo registry(@RequestBody LYopRequest lYopRequest);

    @RequestMapping(value = "/check/user",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo checkUser(@RequestBody LYopRequest lYopRequest);

    @RequestMapping(value = "/check/status",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo checkStatus(@RequestBody LYopRequest lYopRequest);

    @RequestMapping(value = "/loginout",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo loginout(@RequestBody LYopRequest lYopRequest);

    @RequestMapping(value = "/send/code",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo sendEmailCode(@RequestBody LYopRequest lYopRequest);
}
