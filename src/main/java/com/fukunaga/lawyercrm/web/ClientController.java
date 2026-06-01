package com.fukunaga.lawyercrm.web;

import com.fukunaga.lawyercrm.entity.Client;
import com.fukunaga.lawyercrm.repository.*;
import com.fukunaga.lawyercrm.security.CrmUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** 依頼者（顧問先）管理。一覧・詳細・新規/編集・削除。全操作テナントスコープ。 */
@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ClientRepository clientRepo;
    private final CaseRepository caseRepo;
    private final ActivityRepository activityRepo;
    private final UserRepository userRepo;

    public ClientController(ClientRepository clientRepo, CaseRepository caseRepo,
                            ActivityRepository activityRepo, UserRepository userRepo) {
        this.clientRepo = clientRepo;
        this.caseRepo = caseRepo;
        this.activityRepo = activityRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal CrmUserDetails me, Model model) {
        model.addAttribute("clients", clientRepo.findByTenantIdOrderByCreatedAtDesc(me.getTenantId()));
        return "clients/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, @AuthenticationPrincipal CrmUserDetails me, Model model) {
        Client client = clientRepo.findByIdAndTenantId(id, me.getTenantId()).orElseThrow();
        model.addAttribute("client", client);
        model.addAttribute("cases",
                caseRepo.findByTenantIdAndClientIdOrderByCreatedAtDesc(me.getTenantId(), id));
        model.addAttribute("activities",
                activityRepo.findByTenantIdAndClientIdOrderByActivityAtDesc(me.getTenantId(), id));
        return "clients/view";
    }

    @GetMapping("/new")
    public String createForm(@AuthenticationPrincipal CrmUserDetails me, Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("lawyers", userRepo.findByTenantIdOrderByIdAsc(me.getTenantId()));
        return "clients/edit";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, @AuthenticationPrincipal CrmUserDetails me, Model model) {
        model.addAttribute("client", clientRepo.findByIdAndTenantId(id, me.getTenantId()).orElseThrow());
        model.addAttribute("lawyers", userRepo.findByTenantIdOrderByIdAsc(me.getTenantId()));
        return "clients/edit";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Client form, @AuthenticationPrincipal CrmUserDetails me,
                       RedirectAttributes ra) {
        Client client;
        if (form.getId() != null) {
            client = clientRepo.findByIdAndTenantId(form.getId(), me.getTenantId()).orElseThrow();
        } else {
            client = new Client();
            client.setTenantId(me.getTenantId());
        }
        client.setName(form.getName());
        client.setKana(nz(form.getKana()));
        client.setEmail(nz(form.getEmail()));
        client.setTel(nz(form.getTel()));
        client.setAddress(nz(form.getAddress()));
        client.setBirthDate(form.getBirthDate());
        client.setOccupation(nz(form.getOccupation()));
        client.setAssignedLawyerId(form.getAssignedLawyerId());
        client.setStatus(form.getStatus() != null ? form.getStatus() : Client.Status.active);
        client.setMemo(form.getMemo());
        clientRepo.save(client);
        ra.addFlashAttribute("flash", "依頼者を保存しました。");
        return "redirect:/clients/" + client.getId();
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal CrmUserDetails me,
                        RedirectAttributes ra) {
        clientRepo.findByIdAndTenantId(id, me.getTenantId()).ifPresent(clientRepo::delete);
        ra.addFlashAttribute("flash", "依頼者を削除しました。");
        return "redirect:/clients";
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
