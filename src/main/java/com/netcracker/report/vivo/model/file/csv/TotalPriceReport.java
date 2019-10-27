package com.netcracker.report.vivo.model.file.csv;

import lombok.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TotalPriceReport {

    public enum STATUS {
        DELIVERED,
        SUBMITTED_TO_LEGACY
    }

    private String quoteid; //String
    private String status; //String
    private String segment; //String
    private String oppid; //String
    private String productofferingidStr; //Long --
    private String productofferingname; //String
    private String subfamilyidStr; //Long --
    private String subfamilyname; //String
    private String family; //String
    private String quotecreateddateStr; //Date
    private String quoteupdateddateStr; //Date
    private String contracttermStr; //Long --
    private String totalrc_brl_taxinclStr; //BigDecimal
    private String totalnrc_brl_taxinclStr; //BigDecimal
    private String totalpriceStr; //BigDecimal

    static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public Date getQuotecreateddate() throws Exception{
        return !this.quotecreateddateStr.isEmpty() ? format.parse(this.quotecreateddateStr.trim()) : null;
    }

    public Date getQuoteupdateddate() throws Exception{
        return !this.quoteupdateddateStr.isEmpty() ? format.parse(this.quoteupdateddateStr.trim()) : null;
    }

    public Long getProductofferingid() throws Exception{
        return !this.productofferingidStr.isEmpty() ? Long.parseLong(this.productofferingidStr.trim()) : null;
    }

    public Long getContractterm() throws Exception{
        return !this.contracttermStr.isEmpty() ? Long.parseLong(this.contracttermStr.trim()) : null;
    }

    public BigDecimal getTotalrc_brl_taxincl() throws Exception{
        return !this.totalrc_brl_taxinclStr.isEmpty() ? new BigDecimal(NumberFormat.getNumberInstance(Locale.FRANCE).parse(totalrc_brl_taxinclStr).doubleValue()) :null;
    }

    public BigDecimal getTotalnrc_brl_taxincl() throws Exception{
        return !this.totalnrc_brl_taxinclStr.isEmpty() ? new BigDecimal(NumberFormat.getNumberInstance(Locale.FRANCE).parse(totalnrc_brl_taxinclStr).doubleValue()) :null;
    }

    public BigDecimal getTotalprice() throws Exception{
        return !this.totalpriceStr.isEmpty() ? new BigDecimal(NumberFormat.getNumberInstance(Locale.FRANCE).parse(totalpriceStr).doubleValue()) :null;
    }
}
