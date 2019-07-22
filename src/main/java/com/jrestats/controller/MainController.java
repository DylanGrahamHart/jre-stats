package com.jrestats.controller;

import com.jrestats.config.PropConfig;
import com.jrestats.db.entity.ChannelStatEntity;
import com.jrestats.db.repo.ChannelStatRepo;
import com.jrestats.service.ChannelService;
import com.jrestats.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    private static Logger logger = LoggerFactory.getLogger(VideoService.class);

    @Autowired
    ChannelStatRepo channelStatRepo;

    @Autowired
    ChannelService channelService;

    @Autowired
    VideoService videoService;

    @Autowired
    PropConfig propConfig;

    @GetMapping("/")
    public ModelAndView home(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "publishedAt") String sort
    ) {
        ModelAndView mav = new ModelAndView("home");
        mav.addObject("channel", channelService.getChannel());
        mav.addObject("videos", videoService.getVideoListViewModel(page, sort));

        return mav;
    }

    @GetMapping("/get-channel-stats")
    @ResponseBody
    public Iterable<ChannelStatEntity> getChannelStats() {
        return channelStatRepo.findAll();
    }

    @GetMapping("/create-channel-stat")
    @ResponseBody
    public ChannelStatEntity createChannelStat() {
        if (propConfig.isLocal()) return null;
        return channelService.createChannelStat();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    protected String error(Exception error) {
        return "Error";
    }
}
