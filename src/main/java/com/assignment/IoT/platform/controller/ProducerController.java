package com.assignment.IoT.platform.controller;

import com.assignment.IoT.platform.producer.SensorDataProducerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iot/producer")
public class ProducerController {

    private final SensorDataProducerService producerService;

    public ProducerController(SensorDataProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/message")
    public String produceMessage() {
        return "Message: ";
    }
}
