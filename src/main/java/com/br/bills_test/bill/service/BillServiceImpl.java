package com.br.bills_test.bill.service;

import com.br.bills_test.bill.BillStatus;
import com.br.bills_test.bill.entity.Bill;
import com.br.bills_test.bill.repository.BillRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BillServiceImpl implements BillService {

    private final BillRepository repository;

    @Autowired
    public BillServiceImpl(BillRepository repository) {
        this.repository = repository;
    }

    /**
     * Pays a bill with the given ID by updating its status to PAID and saving it to the repository.
     *
     * @param  id  the ID of the bill to be paid
     * @return     an Optional containing the paid bill, or an empty Optional if the bill was not found
     * @throws IllegalArgumentException if the ID is null
     */
    @Override
    public Optional<Bill> pay(Long id) throws IllegalArgumentException {
        log.debug("Attempting to pay bill with id: {}", id);
        Optional<Bill> optionalBill = repository.findById(id);
        if (optionalBill.isEmpty()) {
            log.warn("Bill with id {} not found", id);
            return Optional.empty();
        }
        Bill bill = optionalBill.get();
        bill.pay();
        log.debug("Bill with id {} status updated to PAID", id);
        repository.save(bill);
        return Optional.of(bill);
    }

    /**
     * Retrieves an Optional containing a Bill object by its ID.
     *
     * @param  id  the ID of the Bill to retrieve
     * @return     an Optional containing the Bill object, or an empty Optional if the Bill was not found
     */
    @Override
    public Optional<Bill> get(Long id) {
        return repository.findById(id);
    }

    /**
     * Retrieves a page of unpaid bills.
     *
     * @param  pageable   the pageable object specifying the page size, sorting, and pagination information
     * @return            a Page of Bill objects representing the unpaid bills
     */
    @Override
    public Page<Bill> getUnpaidBills(Pageable pageable) {
        return repository.findByStatusNot(BillStatus.PAID, pageable);
    }

    /**
     * Saves a Bill object to the repository.
     *
     * @param  bill  the Bill object to be saved
     * @return        the saved Bill object
     */
    @Override
    public Bill create(Bill bill) {
        return repository.save(bill);
    }

    /**
     * Retrieves the total amount paid for bills within the specified date range.
     *
     * @param  startDate  the start date of the range (inclusive)
     * @param  endDate    the end date of the range (inclusive)
     * @return            the total amount paid, or zero if no paid bills were found
     */
    @Override
    public BigDecimal getTotalPaid(Date startDate, Date endDate) {
        List<Bill> paidBills = repository.findByStatusAndPaymentDateBetween(BillStatus.PAID, startDate, endDate);
        if (paidBills.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return paidBills.stream().map(Bill::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Updates a bill with the given ID by updating its status to PAID and saving it to the repository.
     *
     * @param  id  the ID of the bill to be updated
     * @param  bill  the updated bill object
     * @return     an Optional containing the updated bill, or an empty Optional if the bill was not found
     */
    @Override
    public Optional<Bill> update(Long id, Bill bill) {
        Optional<Bill> optionalBill = repository.findById(id);
        if (optionalBill.isEmpty()) {
            return Optional.empty();
        }
        bill.setId(id);
        repository.save(bill);
        return Optional.of(bill);
    }
}
