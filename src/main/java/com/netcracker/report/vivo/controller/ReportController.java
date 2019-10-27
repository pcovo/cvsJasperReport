package com.netcracker.report.vivo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.netcracker.report.vivo.service.ReportServiceImpl;

import reactor.core.publisher.Mono;

/*import com.netcracker.cloud.gateway.route.RouteType;
import com.netcracker.cloud.gateway.route.annotation.GatewayRequestMapping;
import com.netcracker.cloud.gateway.route.annotation.Route;*/

@RestController
//@GatewayRequestMapping("/vivo-report/api/v1")
@RequestMapping("/api/v1/report")
//@Route(RouteType.PUBLIC)
@Slf4j
public class ReportController {

    @Autowired
    private ReportServiceImpl service;

    @GetMapping
    public Mono<ResponseEntity<Mono<byte[]>>> getBusinessWeeklyReport() {
        Mono<byte[]> bytes = null;
        try {
            bytes= service.getBusinessWeeklyReport();
        }catch (Exception ex){
            System.out.println(ex);
        }
        String nomeRelatorio= "BusinessWeeklyReport" + ".pdf";
        return Mono.just(ResponseEntity.ok()
                // Specify content type as PDF
                .header("Content-Type", "application/pdf; charset=UTF-8")
                // Tell browser to display PDF if it can
                .header("Content-Disposition", "inline; filename=\"" + nomeRelatorio)
                .body(bytes));
    }
}
