package com.fukunaga.lawyercrm.web;

import com.fukunaga.lawyercrm.entity.Tenant;
import com.fukunaga.lawyercrm.repository.TenantRepository;
import com.fukunaga.lawyercrm.security.CrmUserDetails;
import com.fukunaga.lawyercrm.vertical.Vertical;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 全画面共通のモデル属性。ログイン中テナントの業種（vertical）設定を注入し、
 * テンプレート側でブランド名・用語ラベル・案件種別を業種別に出し分ける。
 */
@ControllerAdvice(assignableTypes = {
        DashboardController.class, ClientController.class,
        CaseController.class, DeadlineController.class
})
public class GlobalModelAdvice {

    private final TenantRepository tenantRepository;

    public GlobalModelAdvice(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @ModelAttribute
    public void addCommon(@AuthenticationPrincipal CrmUserDetails me, Model model) {
        if (me == null) {
            return;
        }
        Tenant tenant = tenantRepository.findById(me.getTenantId()).orElse(null);
        Vertical vertical = tenant != null ? tenant.getVertical() : Vertical.LAWYER;
        model.addAttribute("vertical", vertical);
        model.addAttribute("tenant", tenant);
        model.addAttribute("currentUserName", me.getDisplayName());
    }
}
