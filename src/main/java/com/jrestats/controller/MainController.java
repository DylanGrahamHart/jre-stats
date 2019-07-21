package com.jrestats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jrestats.db.entity.Derp;
import com.jrestats.db.repo.DerpRepo;
import com.jrestats.service.ChannelService;
import com.jrestats.service.VideoService;
import com.jrestats.viewmodel.Channel;
import com.jrestats.viewmodel.Video;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    private static Logger logger = LoggerFactory.getLogger(VideoService.class);

    @Autowired
    DerpRepo derpRepo;

    @Autowired
    ChannelService channelService;

    @Autowired
    VideoService videoService;

    @GetMapping("/")
    public ModelAndView home(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "publishedAt") String sort
    ) {
        ModelAndView mav = new ModelAndView("home");
        mav.addObject("channel", channelService.getChannel());

        List<Video> allVideos = videoService.getAllVideos();
        mav.addObject("videos", videoService.getSortedSubList(allVideos, page, sort));
        mav.addObject("controls", videoService.getControls(allVideos.size(), page, sort));

        return mav;
    }

    @GetMapping("/bitch")
    @ResponseBody
    public String home(
        @RequestParam(defaultValue = "Ass Fucker") String name
    ) throws Exception {
        derpRepo.save(new Derp(name));

        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(derpRepo.findAll());
        } catch (Exception e) {
            logger.error("Some shit went wrong: " + e.getMessage());
            return "Error";
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    protected String error(Exception error) {
        return "Error";
    }
}
