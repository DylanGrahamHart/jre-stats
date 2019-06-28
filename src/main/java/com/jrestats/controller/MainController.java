package com.jrestats.controller;

import com.jrestats.service.ChannelService;
import com.jrestats.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    ChannelService channelService;

    @Autowired
    VideoService videoService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/channel")
    @ResponseBody
    public Map<String, Object> channel() {
        return channelService.getChannel();
    }

    @GetMapping("/videos")
    @ResponseBody
    public List<Map<String, String>> videos() {
        return videoService.getAllVideos();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    protected String error() {
        return "Error";
    }
}
