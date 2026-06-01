package com.fukunaga.lawyercrm.web;

import com.fukunaga.lawyercrm.entity.Deadline;
import com.fukunaga.lawyercrm.entity.LegalCase;
import com.fukunaga.lawyercrm.repository.CaseRepository;
import com.fukunaga.lawyercrm.repository.DeadlineRepository;
import com.fukunaga.lawyercrm.security.CrmUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/** 期日管理。未完了期日の一覧、案件詳細からの追加、完了トグル。 */
@Controller
@RequestMapping("/deadlines")
public class DeadlineController {

    private final DeadlineRepository deadlineRepo;
    private final CaseRepository caseRepo;

    public DeadlineController(DeadlineRepository deadlineRepo, CaseRepository caseRepo) {
        this.deadlineRepo = deadlineRepo;
        this.caseRepo = caseRepo;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal CrmUserDetails me, Model model) {
        Long tid = me.getTenantId();
        var deadlines = deadlineRepo.findByTenantIdAndDoneFalseOrderByDeadlineDateAsc(tid);
        Map<Long, String> caseNames = new HashMap<>();
        for (LegalCase c : caseRepo.findByTenantIdOrderByCreatedAtDesc(tid)) {
            caseNames.put(c.getId(), c.getCaseName());
        }
        model.addAttribute("deadlines", deadlines);
        model.addAttribute("caseNames", caseNames);
        return "deadlines/list";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Deadline form, @AuthenticationPrincipal CrmUserDetails me,
                       RedirectAttributes ra) {
        Long tid = me.getTenantId();
        // 案件がテナントに属することを確認
        caseRepo.findByIdAndTenantId(form.getCaseId(), tid).orElseThrow();
        Deadline d = new Deadline();
        d.setTenantId(tid);
        d.setCaseId(form.getCaseId());
        d.setTitle(form.getTitle());
        d.setDeadlineDate(form.getDeadlineDate());
        d.setDeadlineType(form.getDeadlineType() != null ? form.getDeadlineType() : Deadline.Type.other);
        d.setMemo(form.getMemo());
        deadlineRepo.save(d);
        ra.addFlashAttribute("flash", "期日を追加しました。");
        return "redirect:/cases/" + form.getCaseId();
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, @AuthenticationPrincipal CrmUserDetails me,
                        @RequestParam(required = false) String back) {
        deadlineRepo.findByIdAndTenantId(id, me.getTenantId()).ifPresent(d -> {
            d.setDone(!d.isDone());
            deadlineRepo.save(d);
        });
        return "redirect:" + (back != null ? back : "/deadlines");
    }
}
