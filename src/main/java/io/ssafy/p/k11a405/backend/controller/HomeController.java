package io.ssafy.p.k11a405.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "index.html";  // static 폴더에 위치한 index.html로 라우팅
    }
}

