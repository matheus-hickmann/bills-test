package com.br.bills_test.integration.controller;

import com.br.bills_test.bill.dto.BillResponse;
import com.br.bills_test.bill.entity.Bill;
import com.br.bills_test.integration.service.BillImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("bills/upload")
public class BillIntegrationController {

    private final BillImportService billImportService;

    @Autowired
    public BillIntegrationController(BillImportService billImportService) {
        this.billImportService = billImportService;
    }

    /**
     * This method is responsible for importing bills from a multipart file.
     * It uses the BillImportService to handle the import and then maps the imported
     * bills to BillResponse objects.
     *
     * @param file The file containing the bills to be imported.
     * @return A ResponseEntity containing a list of BillResponse objects.
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<List<BillResponse>> importBills(@RequestParam MultipartFile file) {
        List<Bill> bills = billImportService.importBills(file);
        return ResponseEntity.ok(bills.stream().map(BillResponse::create).toList());
    }
}
