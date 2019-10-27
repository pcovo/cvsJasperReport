package com.netcracker.report.vivo.service;

import com.netcracker.report.vivo.model.file.csv.TotalPriceReport;
import com.netcracker.report.vivo.model.report.wonopportunities.bysegment.WonOpportunitiesBySegment;
import com.netcracker.report.vivo.model.report.wonopportunities.bysegment.WonOpportunitiesBySegmentToReport;
import com.netcracker.report.vivo.storage.ReportRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Slf4j
@Service
public class ReportServiceImpl {


    private ReportRepositoryImpl reportRepositoryImpl;

    @Value("classpath:reports/weekly/Blank_A4_Landscape.jrxml")
    private Resource resourceBusinessWeeklyReport;

    @Value("classpath:reports/weekly/coresLaterais.png")
    private Resource coresLateraisImg;

    @Value("classpath:reports/weekly/logo.png")
    private Resource logoImg;

    public ReportServiceImpl(){
        this.reportRepositoryImpl = new ReportRepositoryImpl();
    }

    public Mono<byte[]> getBusinessWeeklyReport() throws Exception {

        // Data for Build Specific Report
        String path = resourceBusinessWeeklyReport.getURL().getPath();//"C:/Users/paco0619/Documents/NC/REPORT/report.vivo/src/main/resources/reports/weekly/Blank_A4_Landscape.jrxml";
        Map<String,Object> params = new HashMap<>();
        WonOpportunitiesBySegmentToReport.headers.forEach((k, v) -> params.put(k,v));
        Mono<WonOpportunitiesBySegmentToReport> beans = this.getBeansToBusinessWeeklyReport();
        params.put("empresasSummary",beans.block().getEmpresas());
        params.put("massivoSummary",beans.block().getMassivo());
        params.put("topSummary",beans.block().getTop());
        params.put("grandTotalSummary",beans.block().getGrandTotal());

        // Imagens do PDF
        Image coresLaterais = new ImageIcon(coresLateraisImg.getURL()).getImage();
        Image logo = new ImageIcon(logoImg.getURL()).getImage();
        params.put("coresLaterais", coresLaterais);
        params.put("logo", logo);

        //(beans.block()).getWonOpportunitiesBySegmentList().addAll((beans.block()).getWonOpportunitiesBySegmentList());
        //(beans.block()).getWonOpportunitiesBySegmentList().addAll((beans.block()).getWonOpportunitiesBySegmentList());
        //(beans.block()).getWonOpportunitiesBySegmentList().addAll((beans.block()).getWonOpportunitiesBySegmentList());

        // Generate Report
        JasperPrint impressao = JasperFillManager.fillReport(JasperCompileManager.compileReport(path),params, new JRBeanArrayDataSource((beans.block()).getWonOpportunitiesBySegmentList().toArray()));
        //In case need to save in a Directory -> JasperExportManager.exportReportToPdfFile(impressao,"C:/Users/paco0619/Documents/NC/REPORT/report.vivo/src/main/resources/reports/weekly/Blank_A4_Landscape.pdf");
        return Mono.just(JasperExportManager.exportReportToPdf(impressao));
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    private Mono<WonOpportunitiesBySegmentToReport> getBeansToBusinessWeeklyReport() throws Exception{
    //public static void main (String[]args) throws Exception{

        //Recuperar Beans com dados do CSV
        Mono<List<TotalPriceReport>> totalPriceReportList = (new ReportRepositoryImpl()).getTotalPriceFileData();

        //AGRUPANDO por mês (pegar o mês de “quotecreateddate”) para as “quoteid” que tenham “status” = “DELIVERED” ou “SUBMITTED_TO_LEGACY”
        Map<Integer, List<TotalPriceReport>> groupbyMoth = this.agruparTotalPricePorMes(totalPriceReportList);

        //Dados para o Relatório
        List<WonOpportunitiesBySegment> beanListToQuarter = new ArrayList<>();
        List<WonOpportunitiesBySegment> wonOpportunitiesBySegmentList = new ArrayList<>();
        WonOpportunitiesBySegmentToReport wonOpportunitiesBySegmentToReport = new WonOpportunitiesBySegmentToReport();
        BigDecimal sumEmpresasToReport = new BigDecimal(0);
        BigDecimal sumMassivoToReport = new BigDecimal(0);
        BigDecimal sumTOPToReport = new BigDecimal(0);
        BigDecimal sumGrandTotalToReport = new BigDecimal(0);

        SimpleDateFormat formatMonthNumberAndMonthName = new SimpleDateFormat("MM - MMMM");
        SimpleDateFormat formatMonthYearNumber = new SimpleDateFormat("MM");

        // Inicializa dados para Quarter
        int month = Integer.valueOf(formatMonthYearNumber.format(groupbyMoth.get(groupbyMoth.keySet().iterator().next()).get(0).getQuotecreateddate()));
        String lastbeanQuarter = (month <= 3 ? "04" : month <= 6 ? "01" : month <= 9 ? "02" : "03");

        // Somar para todos os Agrupamentos
        for (Integer key : groupbyMoth.keySet()) {
            WonOpportunitiesBySegment bean = new WonOpportunitiesBySegment();
            List<TotalPriceReport> listTotalPriceReport = groupbyMoth.get(key);

            // QUARTER - Empresas - Massivo - TOP  (seguimento) / Grand Total (soma dos seguimentos) - Calcula por Trimestre
            month = Integer.valueOf(formatMonthYearNumber.format(listTotalPriceReport.get(0).getQuotecreateddate()));
            String atualbeanQuarter = (month <= 3 ? "04" : month <= 6 ? "01" : month <= 9 ? "02" : "03");
           if (!beanListToQuarter.isEmpty() && !atualbeanQuarter.equalsIgnoreCase(lastbeanQuarter)){
               lastbeanQuarter = atualbeanQuarter;
                wonOpportunitiesBySegmentList.add(this.getQuarterBean(beanListToQuarter,atualbeanQuarter));
                beanListToQuarter = new ArrayList<>();
           }

           // Agruda Beans para Calcular o Quarter
            beanListToQuarter.add(bean);

            //Label da Coluna Month
            bean.setMonth(formatMonthNumberAndMonthName.format(listTotalPriceReport.get(0).getQuotecreateddate()));

            //Valores das Colunas -> Empresas - Massivo - TOP  (seguimento) / Grand Total (soma dos seguimentos) // BigDecimal result = listTotalPriceReport.stream().filter(beanForSum -> beanForSum.getSegment().equalsIgnoreCase("Empresas")).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumEmpresas = new BigDecimal(0);
            BigDecimal sumMassivo = new BigDecimal(0);
            BigDecimal sumTOP = new BigDecimal(0);
            for(TotalPriceReport beanForSum : listTotalPriceReport){
                switch (beanForSum.getSegment()){
                    case "Empresas": sumEmpresas = sumEmpresas.add(new BigDecimal(beanForSum.getTotalprice().doubleValue()));
                        break;
                    case "Massivo": sumMassivo = sumMassivo.add(new BigDecimal(beanForSum.getTotalprice().doubleValue()));
                        break;
                    case "TOP": sumTOP = sumTOP.add(new BigDecimal(beanForSum.getTotalprice().doubleValue()));
                        break;
                }
            }
            BigDecimal sumGrandTotal = sumEmpresas.add(sumMassivo).add(sumTOP);
            bean.setGrandTotal(sumGrandTotal);
            bean.setEmpresas(sumEmpresas);
            bean.setMassivo(sumMassivo);
            bean.setTop(sumTOP);

            //Coluna “Increase/Decrease”: E valor da coluna Increase/Decrease é a divisão do Grand Total Mês/Grand Total Mês Anterior.
            if(!wonOpportunitiesBySegmentList.isEmpty()){
                WonOpportunitiesBySegment lastBean = wonOpportunitiesBySegmentList.get(wonOpportunitiesBySegmentList.size()-1);
                if(lastBean.getMonth().contains("Quarter")){
                    WonOpportunitiesBySegment lastBeanQuarter = lastBean;
                    for(int i = wonOpportunitiesBySegmentList.size()-2; i >0; i--) {
                        if(wonOpportunitiesBySegmentList.get(i).getMonth().contains("Quarter")){
                            BigDecimal increaseDecreaseQuarter = lastBeanQuarter.getGrandTotal().divide(wonOpportunitiesBySegmentList.get(i).getGrandTotal(), 2, RoundingMode.HALF_UP).subtract(new BigDecimal(1));
                            lastBeanQuarter.setMonthlyIncreaseDecrease(increaseDecreaseQuarter);
                            break;
                        }
                    }
                    lastBean = wonOpportunitiesBySegmentList.get(wonOpportunitiesBySegmentList.size()-2);
                }

                BigDecimal increaseDecrease = sumGrandTotal.divide(lastBean.getGrandTotal(), 2, RoundingMode.HALF_UP).subtract(new BigDecimal(1));
                bean.setMonthlyIncreaseDecrease(increaseDecrease);
            }

            //Pronta a Linha do mês para o Relatório
            wonOpportunitiesBySegmentList.add(bean);

            //Calculando a Soma Total do Relatório
            sumGrandTotalToReport = sumEmpresasToReport.add(sumMassivoToReport).add(sumTOPToReport);
            sumEmpresasToReport = sumEmpresasToReport.add(new BigDecimal(sumEmpresas.doubleValue()));
            sumMassivoToReport = sumMassivoToReport.add(new BigDecimal(sumMassivo.doubleValue()));
            sumTOPToReport = sumTOPToReport.add(new BigDecimal(sumTOP.doubleValue()));
        } //for(WonOpportunitiesBySegment bean : wonOpportunitiesBySegmentList){System.out.println("Month;" + bean.getMonth() + ";Empresas;" + NumberFormat.getInstance(Locale.US).getCurrencyInstance().format(bean.getEmpresas() == null ? 0 : bean.getEmpresas())+ ";Massivo;" + NumberFormat.getInstance(Locale.US).getCurrencyInstance().format(bean.getMassivo() == null ? 0 : bean.getMassivo())+ ";TOP;" + NumberFormat.getInstance(Locale.US).getCurrencyInstance().format(bean.getTop() == null ? 0 : bean.getTop())+ ";GrandTotal;" + NumberFormat.getInstance(Locale.US).getCurrencyInstance().format(bean.getGrandTotal() == null ? 0 : bean.getGrandTotal())+ ";MonthlyIncreaseDecrease;" + NumberFormat.getInstance(Locale.US).getCurrencyInstance().format(bean.getMonthlyIncreaseDecrease() == null ? 0 : bean.getMonthlyIncreaseDecrease()));}

        //Dados Prontos para o Relatório
        wonOpportunitiesBySegmentToReport.setWonOpportunitiesBySegmentList(wonOpportunitiesBySegmentList);
        wonOpportunitiesBySegmentToReport.setEmpresas(sumEmpresasToReport);
        wonOpportunitiesBySegmentToReport.setMassivo(sumMassivoToReport);
        wonOpportunitiesBySegmentToReport.setTop(sumTOPToReport);
        wonOpportunitiesBySegmentToReport.setGrandTotal(sumGrandTotalToReport);

        //System.out.println("Report  --  Empresas;" + NumberFormat.getInstance(Locale.US).getCurrencyInstance().format(wonOpportunitiesBySegmentToReport.getEmpresas()) + ";Massivo;"+NumberFormat.getInstance(Locale.US).getCurrencyInstance().format(wonOpportunitiesBySegmentToReport.getMassivo())+ ";TOP;"+NumberFormat.getInstance(Locale.US).getCurrencyInstance().format(wonOpportunitiesBySegmentToReport.getTop())+ ";GrandTotal;"+NumberFormat.getPercentInstance().format(wonOpportunitiesBySegmentToReport.getGrandTotal()));

        return Mono.just(wonOpportunitiesBySegmentToReport);
    }

    /**
     * 
     * @param totalPriceReportList
     * @return
     * @throws Exception
     */
    private Map<Integer, List<TotalPriceReport>> agruparTotalPricePorMes(Mono<List<TotalPriceReport>> totalPriceReportList) throws Exception {
        Map<Integer, List<TotalPriceReport>> groupbyMoth = new TreeMap<>();
        SimpleDateFormat formatMonthYearNumber = new SimpleDateFormat("MMyyyy");
        for (TotalPriceReport bean : totalPriceReportList.block()) {
            if(bean.getStatus().trim().equalsIgnoreCase(TotalPriceReport.STATUS.DELIVERED.name()) || bean.getStatus().trim().equalsIgnoreCase(TotalPriceReport.STATUS.SUBMITTED_TO_LEGACY.name())){
                List<TotalPriceReport> listTotalPriceReport = groupbyMoth.get(Integer.valueOf(formatMonthYearNumber.format(bean.getQuotecreateddate())));
                if (listTotalPriceReport != null) {
                    listTotalPriceReport.add(bean);
                } else {
                    listTotalPriceReport = new ArrayList(){{add(bean);}};
                    groupbyMoth.put(Integer.valueOf(formatMonthYearNumber.format(bean.getQuotecreateddate())), listTotalPriceReport);
                }
            }
        } //for (Integer key : groupbyMoth.keySet()) {System.out.println("key: " + key + "; size " + groupbyMoth.get(key).size());}
        
        return groupbyMoth;
    }

    /**
     *
     * @param beanListToQuarter
     * @param atualbeanQuarter
     * @return
     */
    private WonOpportunitiesBySegment getQuarterBean(List<WonOpportunitiesBySegment> beanListToQuarter, String atualbeanQuarter){
        WonOpportunitiesBySegment beanQuarter = new WonOpportunitiesBySegment();
        beanQuarter.setMonth("Quarter-"+atualbeanQuarter);

        //Empresas - Massivo - TOP  (seguimento) / Grand Total (soma dos seguimentos)
        BigDecimal sumEmpresasQuarter = new BigDecimal(0);
        BigDecimal sumMassivoQuarter = new BigDecimal(0);
        BigDecimal sumTOPQuarter = new BigDecimal(0);

        for(WonOpportunitiesBySegment beanForSum : beanListToQuarter){
            sumEmpresasQuarter = sumEmpresasQuarter.add(new BigDecimal(beanForSum.getEmpresas().doubleValue()));
            sumMassivoQuarter = sumMassivoQuarter.add(new BigDecimal(beanForSum.getMassivo().doubleValue()));
            sumTOPQuarter = sumTOPQuarter.add(new BigDecimal(beanForSum.getTop().doubleValue()));
        }
        BigDecimal sumGrandTotalQuarter = sumEmpresasQuarter.add(sumMassivoQuarter).add(sumTOPQuarter);

        beanQuarter.setGrandTotal(sumGrandTotalQuarter);
        beanQuarter.setEmpresas(sumEmpresasQuarter);
        beanQuarter.setMassivo(sumMassivoQuarter);
        beanQuarter.setTop(sumTOPQuarter);

        return beanQuarter;
    }
}
