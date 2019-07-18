package com.jrestats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jrestats.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.nio.channels.Channel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableCaching
public class App {

    private static Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		ApplicationContext app = SpringApplication.run(App.class, args);
        VideoService videoService = app.getBean(VideoService.class);
        videoService.getAllVideos();
        logger.info("App done booting");
	}

}





