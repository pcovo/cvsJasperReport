package com.netcracker.report.vivo.storage;

import com.netcracker.report.vivo.model.file.csv.TotalPriceReport;
import com.netcracker.report.vivo.util.FileUtil;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public class ReportRepositoryImpl {

    public Mono<List<TotalPriceReport>> getTotalPriceFileData() throws IOException, Exception{
        FileUtil f = new FileUtil();
        return f.getValues();
    }
}
