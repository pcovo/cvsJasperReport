package com.netcracker.report.vivo.model.report.wonopportunities.bysegment;

import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WonOpportunitiesBySegmentToReport {

    public static final Map<String,String> headers = new HashMap<String,String>(){{
        put("month","Month");
        put("empresas","Empresas");
        put("massivo","Massivo");
        put("top","TOP");
        put("grandTotal","Grand Total");
        put("monthlyIncreaseDecrease","Monthly         Increase/Decrease, %");
    }};

    private List<WonOpportunitiesBySegment> wonOpportunitiesBySegmentList;
    private BigDecimal empresas;
    private BigDecimal 	massivo;
    private BigDecimal 	top;
    private BigDecimal 	grandTotal;

}
