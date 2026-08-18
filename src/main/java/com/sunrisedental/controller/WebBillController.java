package com.sunrisedental.controller;

import com.sunrisedental.dto.BillResponse;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.service.BillService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web MVC Controller serving Thymeleaf views for Billing and Receipt generation.
 */
@Controller
@RequestMapping("/bills")
public class WebBillController {

    private final BillService billService;

    public WebBillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping
    public String listBills(Model model) {
        model.addAttribute("bills", billService.getAllBills());
        return "bills";
    }

    @PostMapping("/generate/{appointmentId}")
    public String generateBill(@PathVariable Long appointmentId, RedirectAttributes redirectAttributes) {
        try {
            BillResponse bill = billService.generateBill(appointmentId);
            redirectAttributes.addFlashAttribute("successMessage", "Bill " + bill.getBillNumber() + " generated successfully.");
            return "redirect:/bills/receipt/" + bill.getId();
        } catch (CustomApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // If already billed, redirect to the existing receipt if available
            try {
                BillResponse existingBill = billService.getBillByAppointmentId(appointmentId);
                return "redirect:/bills/receipt/" + existingBill.getId();
            } catch (Exception ex) {
                return "redirect:/appointments/" + appointmentId;
            }
        }
    }

    @GetMapping("/receipt/{id}")
    public String viewReceipt(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            BillResponse bill = billService.getBillById(id);
            model.addAttribute("bill", bill);
            return "receipt";
        } catch (CustomApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/appointments";
        }
    }

    @GetMapping("/appointment/{appointmentId}")
    public String viewReceiptByAppointment(@PathVariable Long appointmentId, RedirectAttributes redirectAttributes) {
        try {
            BillResponse bill = billService.getBillByAppointmentId(appointmentId);
            return "redirect:/bills/receipt/" + bill.getId();
        } catch (CustomApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/appointments/" + appointmentId;
        }
    }
}
