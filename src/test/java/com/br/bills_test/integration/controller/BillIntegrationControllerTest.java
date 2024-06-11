package com.br.bills_test.integration.controller;

import com.br.bills_test.bill.dto.BillResponse;
import com.br.bills_test.bill.entity.Bill;
import com.br.bills_test.integration.service.BillImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillIntegrationControllerTest {

    @Mock
    private BillImportService billImportService;
    private BillIntegrationController controller;

    @BeforeEach
    void setUp() {
        controller = new BillIntegrationController(billImportService);
    }

    @Test
    void testImportBills_validFile() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        when(billImportService.importBills(file)).thenReturn(Arrays.asList(new Bill(), new Bill()));
        ResponseEntity<List<BillResponse>> response = controller.importBills(file);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testImportBills_emptyFile() {
        // Test case 2: Importing bills with empty file
        MultipartFile file = Mockito.mock(MultipartFile.class);
        ResponseEntity<List<BillResponse>> response = controller.importBills(file);
        assertEquals(200, response.getStatusCodeValue());
    }

}