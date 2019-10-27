package com.netcracker.report.vivo.model.report.wonopportunities.bysegment;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WonOpportunitiesBySegment {
    private String 	month;
    private BigDecimal empresas;
    private BigDecimal 	massivo;
    private BigDecimal 	top;
    private BigDecimal 	grandTotal;
    private BigDecimal 	monthlyIncreaseDecrease;
}

