package com.fukunaga.lawyercrm.web;

import com.fukunaga.lawyercrm.entity.LegalCase;
import com.fukunaga.lawyercrm.repository.*;
import com.fukunaga.lawyercrm.security.CrmUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** 案件管理。一覧・詳細（期日/活動/請求を内包）・新規/編集・削除。 */
@Controller
@RequestMapping("/cases")
public class CaseController {

    private final CaseRepository caseRepo;
    private final ClientRepository clientRepo;
    private final DeadlineRepository deadlineRepo;
    private final ActivityRepository activityRepo;
    private final BillingRepository billingRepo;
    private final UserRepository userRepo;

    public CaseController(CaseRepository caseRepo, ClientRepository clientRepo,
                          DeadlineRepository deadlineRepo, ActivityRepository activityRepo,
                          BillingRepository billingRepo, UserRepository userRepo) {
        this.caseRepo = caseRepo;
        this.clientRepo = clientRepo;
        this.deadlineRepo = deadlineRepo;
        this.activityRepo = activityRepo;
        this.billingRepo = billingRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal CrmUserDetails me, Model model) {
        Long tid = me.getTenantId();
        model.addAttribute("cases", caseRepo.findByTenantIdOrderByCreatedAtDesc(tid));
        model.addAttribute("clientMap", clientNameMap(tid));
        return "cases/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, @AuthenticationPrincipal CrmUserDetails me, Model model) {
        Long tid = me.getTenantId();
        LegalCase c = caseRepo.findByIdAndTenantId(id, tid).orElseThrow();
        model.addAttribute("legalCase", c);
        model.addAttribute("client", clientRepo.findByIdAndTenantId(c.getClientId(), tid).orElse(null));
        model.addAttribute("deadlines", deadlineRepo.findByTenantIdAndCaseIdOrderByDeadlineDateAsc(tid, id));
        model.addAttribute("activities", activityRepo.findByTenantIdAndCaseIdOrderByActivityAtDesc(tid, id));
        model.addAttribute("billings", billingRepo.findByTenantIdAndCaseIdOrderByCreatedAtDesc(tid, id));
        return "cases/view";
    }

    @GetMapping("/new")
    public String createForm(@AuthenticationPrincipal CrmUserDetails me, Model model,
                             @RequestParam(required = false) Long clientId) {
        LegalCase c = new LegalCase();
        if (clientId != null) {
            c.setClientId(clientId);
        }
        model.addAttribute("legalCase", c);
        addFormRefs(me, model);
        return "cases/edit";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, @AuthenticationPrincipal CrmUserDetails me, Model model) {
        model.addAttribute("legalCase", caseRepo.findByIdAndTenantId(id, me.getTenantId()).orElseThrow());
        addFormRefs(me, model);
        return "cases/edit";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("legalCase") LegalCase form,
                       @AuthenticationPrincipal CrmUserDetails me, RedirectAttributes ra) {
        Long tid = me.getTenantId();
        LegalCase c;
        if (form.getId() != null) {
            c = caseRepo.findByIdAndTenantId(form.getId(), tid).orElseThrow();
        } else {
            c = new LegalCase();
            c.setTenantId(tid);
        }
        c.setClientId(form.getClientId());
        c.setCaseNumber(nz(form.getCaseNumber()));
        c.setCaseName(form.getCaseName());
        c.setCaseType(form.getCaseType() != null ? form.getCaseType() : "other");
        c.setStatus(form.getStatus() != null ? form.getStatus() : LegalCase.Status.active);
        c.setAssignedLawyerId(form.getAssignedLawyerId());
        c.setOpenedDate(form.getOpenedDate());
        c.setClosedDate(form.getClosedDate());
        c.setCourtName(nz(form.getCourtName()));
        c.setOpponent(nz(form.getOpponent()));
        c.setMemo(form.getMemo());
        caseRepo.save(c);
        ra.addFlashAttribute("flash", "案件を保存しました。");
        return "redirect:/cases/" + c.getId();
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal CrmUserDetails me,
                        RedirectAttributes ra) {
        caseRepo.findByIdAndTenantId(id, me.getTenantId()).ifPresent(caseRepo::delete);
        ra.addFlashAttribute("flash", "案件を削除しました。");
        return "redirect:/cases";
    }

    private void addFormRefs(CrmUserDetails me, Model model) {
        Long tid = me.getTenantId();
        model.addAttribute("clients", clientRepo.findByTenantIdOrderByCreatedAtDesc(tid));
        model.addAttribute("lawyers", userRepo.findByTenantIdOrderByIdAsc(tid));
    }

    private java.util.Map<Long, String> clientNameMap(Long tid) {
        java.util.Map<Long, String> m = new java.util.HashMap<>();
        clientRepo.findByTenantIdOrderByCreatedAtDesc(tid).forEach(c -> m.put(c.getId(), c.getName()));
        return m;
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
