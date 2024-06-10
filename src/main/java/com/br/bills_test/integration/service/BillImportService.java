package com.br.bills_test.integration.service;

import com.br.bills_test.bill.entity.Bill;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BillImportService {

    List<Bill> importBills(MultipartFile file);
}
