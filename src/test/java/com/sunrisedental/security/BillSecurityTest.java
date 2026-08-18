package com.sunrisedental.security;

import com.sunrisedental.dto.BillResponse;
import com.sunrisedental.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BillSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillService billService;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private PatientService patientService;

    @MockBean
    private DentistService dentistService;

    @MockBean
    private TreatmentService treatmentService;

    @Test
    @DisplayName("Unauthenticated user cannot access /bills - redirected to login")
    void testUnauthenticatedAccessToBills() throws Exception {
        mockMvc.perform(get("/bills"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/bills/receipt/1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("ADMIN can access GET /bills (Billing Management page)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminCanAccessBillingManagementPage() throws Exception {
        mockMvc.perform(get("/bills"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("RECEPTIONIST can access GET /bills (Billing Management page)")
    @WithMockUser(username = "recep1", roles = {"RECEPTIONIST"})
    void testReceptionistCanAccessBillingManagementPage() throws Exception {
        mockMvc.perform(get("/bills"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DENTIST can access GET /bills (Billing Management page)")
    @WithMockUser(username = "dentist1", roles = {"DENTIST"})
    void testDentistCanAccessBillingManagementPage() throws Exception {
        mockMvc.perform(get("/bills"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN can access GET /bills/receipt/1")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminCanAccessReceipt() throws Exception {
        when(billService.getBillById(1L)).thenReturn(new BillResponse());

        mockMvc.perform(get("/bills/receipt/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("RECEPTIONIST can access GET /bills/receipt/1")
    @WithMockUser(username = "recep1", roles = {"RECEPTIONIST"})
    void testReceptionistCanAccessReceipt() throws Exception {
        when(billService.getBillById(1L)).thenReturn(new BillResponse());

        mockMvc.perform(get("/bills/receipt/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DENTIST can access GET /bills/receipt/1 (read-only)")
    @WithMockUser(username = "dentist1", roles = {"DENTIST"})
    void testDentistCanViewReceipt() throws Exception {
        when(billService.getBillById(1L)).thenReturn(new BillResponse());

        mockMvc.perform(get("/bills/receipt/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN can post to /bills/generate/1")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminCanGenerateBillWeb() throws Exception {
        BillResponse response = new BillResponse();
        response.setId(10L);
        response.setBillNumber("REC-000010");
        when(billService.generateBill(1L)).thenReturn(response);

        mockMvc.perform(post("/bills/generate/1").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("DENTIST cannot post to /bills/generate/1 (403 Forbidden)")
    @WithMockUser(username = "dentist1", roles = {"DENTIST"})
    void testDentistCannotGenerateBillWeb() throws Exception {
        mockMvc.perform(post("/bills/generate/1").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Unauthenticated user cannot access /api/v1/bills - redirected")
    void testUnauthenticatedAccessToApiBills() throws Exception {
        mockMvc.perform(get("/api/v1/bills"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("DENTIST can access GET /api/v1/bills")
    @WithMockUser(username = "dentist1", roles = {"DENTIST"})
    void testDentistCanAccessApiBills() throws Exception {
        mockMvc.perform(get("/api/v1/bills"))
                .andExpect(status().isOk());
    }
}
