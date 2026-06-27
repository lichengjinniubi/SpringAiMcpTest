package com.ai.mcpclient.controller;

import com.ai.mcpclient.service.WeatherClientService;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherClientService weatherClientService;

    public WeatherController(WeatherClientService weatherClientService) {
        this.weatherClientService = weatherClientService;
    }

    @GetMapping("/query")
    public McpSchema.CallToolResult queryWeather(@RequestParam(defaultValue = "北京") String city) {
        return weatherClientService.callWeather(city);
    }

    @GetMapping("/tools")
    public List<McpSchema.Tool> listTools() {
        return weatherClientService.listTools();
    }
}
