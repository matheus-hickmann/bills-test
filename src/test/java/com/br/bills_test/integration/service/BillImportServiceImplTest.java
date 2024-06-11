package com.br.bills_test.integration.service;

import com.br.bills_test.bill.entity.Bill;
import com.br.bills_test.bill.service.BillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BillImportServiceImplTest {

    @Mock
    private BillService billService;

    @InjectMocks
    private BillImportServiceImpl billImportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testImportBillsEmptyFile() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("test.csv", new byte[0]);

        // Act
        List<Bill> bills = billImportService.importBills(file);

        // Assert
        assertEquals(0, bills.size());
    }

    @Test
    void testImportBillsValidData() throws ParseException {
        // Arrange
        String csvData = "expiringDate,paymentDate,amount,description\n" +
                "01-01-2022,15-01-2022,100.00,Rent\n" +
                "15-02-2022,01-03-2022,200.00,Internet";
        MockMultipartFile file = new MockMultipartFile("bills.csv", csvData.getBytes());
        when(billService.create(any())).thenAnswer(i -> i.getArguments()[0]);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        // Act
        List<Bill> bills = billImportService.importBills(file);
        // Assert
        assertEquals(2, bills.size());
        assertEquals("Rent", bills.get(0).getDescription());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(bills.get(0).getAmount()));
        assertEquals("01-01-2022", sdf.format(bills.get(0).getExpiringDate()));
        assertEquals("15-01-2022", sdf.format(bills.get(0).getPaymentDate()));
        assertEquals("Internet", bills.get(1).getDescription());
        assertEquals(0, BigDecimal.valueOf(200).compareTo(bills.get(1).getAmount()));
        assertEquals("01-03-2022", sdf.format(bills.get(1).getPaymentDate()));
        assertEquals("15-02-2022", sdf.format(bills.get(1).getExpiringDate()));
    }

}