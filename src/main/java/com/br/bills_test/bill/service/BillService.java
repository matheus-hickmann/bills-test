package com.br.bills_test.bill.service;

import com.br.bills_test.bill.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

public interface BillService {
    Optional<Bill> pay(Long id) throws IllegalArgumentException;
    Optional<Bill> get(Long id);
    Page<Bill> getUnpaidBills(Pageable pageable);
    Bill create(Bill bill);
    BigDecimal getTotalPaid(Date startDate, Date endDate);
    Optional<Bill> update(Long id, Bill bill);
}
