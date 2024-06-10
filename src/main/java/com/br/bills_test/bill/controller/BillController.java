package com.br.bills_test.bill.controller;

import com.br.bills_test.bill.dto.BillRequest;
import com.br.bills_test.bill.dto.BillResponse;
import com.br.bills_test.bill.dto.CustomPage;
import com.br.bills_test.bill.dto.TotalPaidResponse;
import com.br.bills_test.bill.entity.Bill;
import com.br.bills_test.bill.service.BillService;
import com.br.bills_test.bill.service.BillServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/bills")
public class BillController {

    public final BillService billService;

    @Autowired
    public BillController(BillServiceImpl billService) {
        this.billService = billService;
    }

    /**
     * Retrieves a page of unpaid bills.
     *
     * @param  pageable   the pageable object specifying the page size, sorting, and pagination information
     * @return            a ResponseEntity containing a CustomPage of BillResponse objects and a status code of OK
     */
    @GetMapping("/unpaid")
    public ResponseEntity<CustomPage<BillResponse>> getUnpaidBills(@PageableDefault(sort = {"expiringDate"}, direction = Sort.Direction.ASC, value = 10) final Pageable pageable) {
        Page<BillResponse> billResponses = billService.getUnpaidBills(pageable).map(BillResponse::create);
        return new ResponseEntity<>(new CustomPage<>(billResponses), HttpStatus.OK);
    }

    @GetMapping("/paidAmount")
    public ResponseEntity<TotalPaidResponse> getPaidAmount(
            @RequestParam @DateTimeFormat(pattern="dd-MM-yyyy") Date startDate,
            @RequestParam @DateTimeFormat(pattern="dd-MM-yyyy") Date endDate
    ) {
        BigDecimal totalPaid = billService.getTotalPaid(startDate, endDate);
        return new ResponseEntity<>(new TotalPaidResponse(totalPaid), HttpStatus.OK);
    }

    /**
     * This method is used to mark a bill as paid.
     * It takes a bill ID as a path variable and returns a ResponseEntity.
     * If the bill is found and marked as paid, it returns a ResponseEntity with a status of NO_CONTENT.
     * If the bill is not found, it returns a ResponseEntity with a status of NOT_FOUND.
     *
     * @param id The ID of the bill to be paid.
     * @return A ResponseEntity containing a BillResponse object and a status code.
     */
    @PatchMapping(path = "pay/{id}")
    public ResponseEntity<BillResponse> pay(@PathVariable Long id) {
        Optional<Bill> paidBill = billService.pay(id);
        if (paidBill.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<BillResponse> update(@PathVariable Long id, @RequestBody BillRequest billRequest ) {
        Optional<Bill> paidBill = billService.update(id, billRequest.toBill());
        if (paidBill.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieves a bill by its ID and returns it as a ResponseEntity.
     * If the bill is found, it returns a ResponseEntity with the bill data and a status of OK.
     * If the bill is not found, it returns a ResponseEntity with a status of NOT_FOUND.
     *
     * @param id The ID of the bill to retrieve.
     * @return A ResponseEntity containing a BillResponse object and a status code.
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<BillResponse> getById(@PathVariable Long id) {
        Optional<Bill> optionalBill = billService.get(id);
        return optionalBill.map(bill ->
                new ResponseEntity<>(BillResponse.create(bill), HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND)
        );
    }

    /**
     * Creates a new bill using the provided BillRequest object and returns the created bill as a ResponseEntity.
     *
     * @param  billRequest  the BillRequest object containing the details of the bill to be created
     * @return              a ResponseEntity containing the created BillResponse object and a status code of CREATED
     */
    @PostMapping
    public ResponseEntity<BillResponse> createBill(@RequestBody BillRequest billRequest) {
        Bill bill = billService.create(billRequest.toBill());
        return new ResponseEntity<>(BillResponse.create(bill), HttpStatus.CREATED);
    }

 }
