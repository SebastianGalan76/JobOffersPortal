package com.coresaken.jobportal.controller;

import com.coresaken.jobportal.database.model.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LinkController {

    @ResponseBody
    @PostMapping("/link/types")
    public Link.LinkTypeEnum[] getLinkTypes(){
        return Link.LinkTypeEnum.values();
    }
}
