package com.mornd.system.controller;

import com.mornd.system.entity.po.Server;
import com.mornd.system.entity.result.JsonResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController
{
    @PreAuthorize("hasAnyAuthority('systemMonitor:server')")
    @GetMapping()
    public JsonResult getInfo() throws Exception
    {
        Server server = new Server();
        server.copyTo();
        return JsonResult.successData(server);
    }
}
