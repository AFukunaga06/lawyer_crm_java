package com.fukunaga.lawyercrm.web;

import com.fukunaga.lawyercrm.entity.LegalCase;
import com.fukunaga.lawyercrm.repository.*;
import com.fukunaga.lawyercrm.security.CrmUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

/** ダッシュボード（index.php 相当）。件数サマリと直近期日・活動を表示。 */
@Controller
public class DashboardController {

    private final ClientRepository clientRepo;
    private final CaseRepository caseRepo;
    private final DeadlineRepository deadlineRepo;
    private final ActivityRepository activityRepo;

    public DashboardController(ClientRepository clientRepo, CaseRepository caseRepo,
                               DeadlineRepository deadlineRepo, ActivityRepository activityRepo) {
        this.clientRepo = clientRepo;
        this.caseRepo = caseRepo;
        this.deadlineRepo = deadlineRepo;
        this.activityRepo = activityRepo;
    }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal CrmUserDetails me, Model model) {
        Long tid = me.getTenantId();
        model.addAttribute("clientCount", clientRepo.countByTenantId(tid));
        model.addAttribute("caseCount", caseRepo.countByTenantId(tid));
        model.addAttribute("activeCaseCount", caseRepo.countByTenantIdAndStatus(tid, LegalCase.Status.active));

        LocalDateTime now = LocalDateTime.now();
        model.addAttribute("upcomingDeadlines",
                deadlineRepo.findByTenantIdAndDoneFalseAndDeadlineDateBetweenOrderByDeadlineDateAsc(
                        tid, now.minusDays(7), now.plusDays(30)));
        model.addAttribute("recentActivities",
                activityRepo.findTop20ByTenantIdOrderByActivityAtDesc(tid));
        return "dashboard";
    }
}
