package io.ssafy.p.k11a405.backend.controller;

import io.ssafy.p.k11a405.backend.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;
}
