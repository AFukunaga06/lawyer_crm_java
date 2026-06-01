package com.fukunaga.lawyercrm.web;

import com.fukunaga.lawyercrm.entity.Activity;
import com.fukunaga.lawyercrm.repository.ActivityRepository;
import com.fukunaga.lawyercrm.repository.CaseRepository;
import com.fukunaga.lawyercrm.repository.ClientRepository;
import com.fukunaga.lawyercrm.security.CrmUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** 活動記録の追加。案件詳細または依頼者詳細から呼ばれ、元画面へ戻る。 */
@Controller
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityRepository activityRepo;
    private final ClientRepository clientRepo;
    private final CaseRepository caseRepo;

    public ActivityController(ActivityRepository activityRepo, ClientRepository clientRepo,
                              CaseRepository caseRepo) {
        this.activityRepo = activityRepo;
        this.clientRepo = clientRepo;
        this.caseRepo = caseRepo;
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Activity form, @AuthenticationPrincipal CrmUserDetails me,
                       RedirectAttributes ra) {
        Long tid = me.getTenantId();
        // 依頼者がテナントに属することを確認
        clientRepo.findByIdAndTenantId(form.getClientId(), tid).orElseThrow();
        Activity a = new Activity();
        a.setTenantId(tid);
        a.setClientId(form.getClientId());
        a.setCaseId(form.getCaseId());
        a.setUserId(me.getUserId());
        a.setActivityType(form.getActivityType() != null ? form.getActivityType() : Activity.Type.other);
        a.setActivityAt(form.getActivityAt());
        a.setDurationMin(form.getDurationMin());
        a.setTitle(form.getTitle() == null ? "" : form.getTitle());
        a.setContent(form.getContent());
        a.setResult(form.getResult());
        activityRepo.save(a);
        ra.addFlashAttribute("flash", "活動記録を追加しました。");
        if (form.getCaseId() != null) {
            return "redirect:/cases/" + form.getCaseId();
        }
        return "redirect:/clients/" + form.getClientId();
    }
}
