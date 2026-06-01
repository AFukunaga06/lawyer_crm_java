package com.fukunaga.lawyercrm.web;

import com.fukunaga.lawyercrm.entity.Billing;
import com.fukunaga.lawyercrm.repository.BillingRepository;
import com.fukunaga.lawyercrm.repository.CaseRepository;
import com.fukunaga.lawyercrm.security.CrmUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

/** 請求の追加・入金トグル。案件詳細から呼ばれ、案件詳細へ戻る。 */
@Controller
@RequestMapping("/billing")
public class BillingController {

    private final BillingRepository billingRepo;
    private final CaseRepository caseRepo;

    public BillingController(BillingRepository billingRepo, CaseRepository caseRepo) {
        this.billingRepo = billingRepo;
        this.caseRepo = caseRepo;
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Billing form, @AuthenticationPrincipal CrmUserDetails me,
                       RedirectAttributes ra) {
        Long tid = me.getTenantId();
        caseRepo.findByIdAndTenantId(form.getCaseId(), tid).orElseThrow();
        Billing b = new Billing();
        b.setTenantId(tid);
        b.setCaseId(form.getCaseId());
        b.setBillingType(form.getBillingType() != null ? form.getBillingType() : Billing.Type.retainer);
        b.setTitle(form.getTitle() == null ? "" : form.getTitle());
        b.setAmount(form.getAmount() != null ? form.getAmount() : BigDecimal.ZERO);
        b.setHours(form.getHours());
        b.setBilledDate(form.getBilledDate());
        b.setMemo(form.getMemo());
        billingRepo.save(b);
        ra.addFlashAttribute("flash", "請求を追加しました。");
        return "redirect:/cases/" + form.getCaseId();
    }

    @PostMapping("/{id}/toggle-paid")
    public String togglePaid(@PathVariable Long id, @AuthenticationPrincipal CrmUserDetails me,
                            @ModelAttribute("caseId") Long caseId) {
        billingRepo.findById(id)
                .filter(b -> b.getTenantId().equals(me.getTenantId()))
                .ifPresent(b -> {
                    b.setPaid(!b.isPaid());
                    b.setPaidDate(b.isPaid() ? java.time.LocalDate.now() : null);
                    billingRepo.save(b);
                });
        return "redirect:/cases/" + caseId;
    }
}
