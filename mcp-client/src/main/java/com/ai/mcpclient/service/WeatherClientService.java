package com.ai.mcpclient.service;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherClientService {

    private static final Logger log = LoggerFactory.getLogger(WeatherClientService.class);

    private final List<McpSyncClient> mcpSyncClients;

    public WeatherClientService(List<McpSyncClient> mcpSyncClients) {
        this.mcpSyncClients = mcpSyncClients;
    }

    public McpSchema.CallToolResult callWeather(String city) {
        Map<String, Object> param = new HashMap<>();
        param.put("city", city);

        for (McpSyncClient client : mcpSyncClients) {
            McpSchema.Implementation clientInfo = client.getClientInfo();
            McpSchema.Implementation serverInfo = client.getServerInfo();
            log.info("clientInfo: {}", clientInfo);
            log.info("serverInfo: {}", serverInfo);
            try {
                McpSchema.CallToolRequest request = McpSchema.CallToolRequest.builder()
                        .name("getWeather")
                        .arguments(param)
                        .build();
                McpSchema.CallToolResult result = client.callTool(request);
                log.info("callTool result: {}", result);
                return result;
            } catch (Exception ex) {
                log.error("调用 MCP Server 失败: {}", ex.getMessage(), ex);
            }
        }
        return null;
    }

    public List<McpSchema.Tool> listTools() {
        for (McpSyncClient client : mcpSyncClients) {
            try {
                McpSchema.ListToolsResult result = client.listTools();
                log.info("tools: {}", result.tools());
                return result.tools();
            } catch (Exception ex) {
                log.error("获取工具列表失败: {}", ex.getMessage(), ex);
            }
        }
        return List.of();
    }
}
