package com.coresaken.jobportal.controller;

import com.coresaken.jobportal.service.FollowService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class FollowController {
    final FollowService followService;

    @PostMapping("/company/follow/{id}")
    public ResponseEntity<String> follow(@PathVariable("id") Long id) {
        return followService.follow(id);
    }

    @DeleteMapping("/company/follow/{id}")
    public ResponseEntity<String> unfollowCompany(@PathVariable("id") Long id) {
        return followService.unfollow(id);
    }
}
