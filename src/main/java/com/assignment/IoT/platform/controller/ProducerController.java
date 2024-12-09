package com.assignment.IoT.platform.controller;

import com.assignment.IoT.platform.producer.ProducerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iot/producer")
public class ProducerController {

    private final ProducerService producerService;

    public ProducerController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/message")
    public String produceMessage(@RequestParam String message) {
        producerService.produceSensorData(message);
        return "Message: " + message;
    }
}
