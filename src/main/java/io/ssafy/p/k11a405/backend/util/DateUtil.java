package io.ssafy.p.k11a405.backend.util;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateUtil {

    public String getChatFormatTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        return LocalTime.now().format(dateTimeFormatter)
                .replace("AM", "오전")
                .replace("PM", "오후");
    }
}
