package com.netcracker.report.vivo.util;

import com.netcracker.report.vivo.model.file.csv.TotalPriceReport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    //private static final String SAMPLE_CSV_FILE_PATH = "C:/Users/paco0619/Documents/NC/REPORT/report.vivo/src/main/resources/reports/weekly/dataToLoadTest.csv";//"./users.csv";
    private static final String SAMPLE_CSV_FILE_PATH = "C:/Users/paco0619/Documents/NC/REPORT/report.vivo/src/main/resources/reports/weekly/total_price_report_2019-10-20.csv";//"./users.csv";

    //@Value("classpath:reports/weekly/Blank_A4_Landscape.jrxml")
    //private Resource resourceBusinessWeeklyReport;

    public Mono<List<TotalPriceReport>> getValues()throws IOException, Exception{
    //public static void main(String[] args) throws IOException, Exception {
        final URL url = new File(SAMPLE_CSV_FILE_PATH).toURI().toURL();
        List<TotalPriceReport> beanList = new ArrayList<>();
        try (
                //String path = resourceBusinessWeeklyReport.getURL().getPath()
                //Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH), Charset.forName("UTF-8"));
                Reader reader = new InputStreamReader(new BOMInputStream(url.openStream()), "ISO-8859-1");
                CSVParser csvParser = new CSVParser(reader, CSVFormat.newFormat(';')
                                                                        .withFirstRecordAsHeader()
                                                                        .withIgnoreHeaderCase()
                                                                        .withTrim());
        ) {
            TotalPriceReport bean = null;
            for (CSVRecord csvRecord : csvParser) {
                beanList.add(new TotalPriceReport(csvRecord.get(0),csvRecord.get(1),csvRecord.get(2),csvRecord.get(3),csvRecord.get(4),csvRecord.get(5),csvRecord.get(6),csvRecord.get(7),csvRecord.get(8),csvRecord.get(9),csvRecord.get(10),csvRecord.get(11),csvRecord.get(12),csvRecord.get(13),csvRecord.get(14)));
                /*bean = new TotalPriceReport(csvRecord.get(0),csvRecord.get(1),csvRecord.get(2),csvRecord.get(3),csvRecord.get(4),csvRecord.get(5),csvRecord.get(6),csvRecord.get(7),csvRecord.get(8),csvRecord.get(9),csvRecord.get(10),csvRecord.get(11),csvRecord.get(12),csvRecord.get(13),csvRecord.get(14));
                System.out.println("getSubfamilyname (special characters)- " + bean.getSubfamilyname());
                System.out.println("---------------");
                System.out.println(" --- BigDecimal conversions --- ");
                System.out.println("getTotalrc_brl_taxincl : " + bean.getTotalrc_brl_taxincl());
                System.out.println("getTotalnrc_brl_taxincl : " + bean.getTotalnrc_brl_taxincl());
                System.out.println("getTotalprice: " + bean.getTotalprice());
                System.out.println("---------------");
                System.out.println(" --- Date conversions --- ");
                System.out.println("getQuotecreateddate: " + bean.getQuotecreateddate());
                System.out.println("getQuoteupdateddate: " + bean.getQuoteupdateddate());
                System.out.println("---------------");
                System.out.println(" --- Long conversions --- ");
                System.out.println("getProductofferingid:" + bean.getProductofferingid());
                System.out.println("getContractterm: " + bean.getContractterm());
                System.out.println("---------------\n\n");

                if(csvRecord.getRecordNumber() >= 5)
                    break;*/
            }
        }
        return Mono.just(beanList);
    }
}
