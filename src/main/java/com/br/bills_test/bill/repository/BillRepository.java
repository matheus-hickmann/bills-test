package com.br.bills_test.bill.repository;

import com.br.bills_test.bill.BillStatus;
import com.br.bills_test.bill.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BillRepository extends PagingAndSortingRepository<Bill, Long>, JpaRepository<Bill, Long> {
    Page<Bill> findByStatusNot(BillStatus status, Pageable pageable);
    List<Bill> findByStatusAndPaymentDateBetween(BillStatus status, Date startDate, Date endDate);
}
