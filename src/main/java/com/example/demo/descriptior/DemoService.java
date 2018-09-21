package com.example.demo.descriptior;

import com.example.demo.mode.GoodsRecord;
import com.example.demo.stuct.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Component
@EnableAutoConfiguration
@RestController
public interface DemoService {

    @RequestMapping(value = "/submit/json",produces = "application/json;charset=UTF-8",consumes = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo submitJson(@RequestBody LYopRequest lYopRequest);

    @RequestMapping(value = "/search",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public List<GoodsRecord> search(@RequestBody LYopRequest lYopRequest);

    @RequestMapping(value = "/search/count",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public int searchCount(@RequestBody LYopRequest lYopRequest);

    @RequestMapping(value = "/update",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo update(@RequestBody LYopRequest lYopRequest);

    @RequestMapping(value = "/delete",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo delete(@RequestBody LYopRequest lYopRequest);

}
