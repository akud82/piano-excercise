package com.akudsoft.piano.controllers;

import com.akudsoft.piano.gateway.StackOverflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ExecutorService;

@RestController
public class MainController {
    @Autowired
    ExecutorService executor;

    @Autowired
    StackOverflowService stackOverflowService;

    @RequestMapping("/search/{searchText}")
    public DeferredResult<String> ping(@PathVariable String searchText) {
        final DeferredResult<String> dr = new DeferredResult<>();

        executor.submit((Runnable) () -> {
            final String search = stackOverflowService.search(searchText);
            dr.setResult(search);
        });

        return dr;
    }
}
