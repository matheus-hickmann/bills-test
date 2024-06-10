package com.br.bills_test.integration.service;

import com.br.bills_test.bill.dto.BillRequest;
import com.br.bills_test.bill.entity.Bill;
import com.br.bills_test.bill.service.BillService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Service
@Slf4j
public class BillImportServiceImpl implements BillImportService {

    private final BillService billService;

    @Autowired
    public BillImportServiceImpl(BillService billService) {
        this.billService = billService;
    }

    /**
     * Imports bills from an external source and returns a list of created Bill objects.
     *
     * @return a list of Bill objects representing the imported bills
     */
    @Override
    public List<Bill> importBills(MultipartFile file) {
        List<Bill> bills = parseFileToBills(file);
        return bills.stream().map(billService::create).toList();
    }

    /**
     * Parses a multipart file containing CSV data into a list of Bill objects.
     *
     * @param  file  the multipart file to parse
     * @return       a list of Bill objects representing the parsed data
     */
    private static List<Bill> parseFileToBills(MultipartFile file) {
        Reader reader = null;
        try {
            log.debug("Parsing file: {}", file.getName());
            reader = new InputStreamReader(file.getInputStream());
        } catch (IOException e) {
            log.error("Failed to read file: {}", file.getName(), e);
            throw new RuntimeException(e);
        }
        CsvToBean<BillRequest> csvToBean = new CsvToBeanBuilder<BillRequest>(reader)
                .withType(BillRequest.class)
                .build();

        return csvToBean.parse().stream().map(BillRequest::toBill).toList();
    }
}
