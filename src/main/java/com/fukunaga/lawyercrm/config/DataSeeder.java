package com.fukunaga.lawyercrm.config;

import com.fukunaga.lawyercrm.entity.*;
import com.fukunaga.lawyercrm.repository.*;
import com.fukunaga.lawyercrm.vertical.Vertical;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 初回起動時のシードデータ。テナントが空のときだけ投入する。
 * - tenant_id=1: 弁護士事務所（LegalDesk）、admin/admin123
 * - tenant_id=2: 税理士事務所（TaxDesk）、tax/testpass123  ← マルチテナント検証用
 * PHP版 setup.sql / undo_demo_data.sql のデモデータに相当。
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(TenantRepository tenantRepo, UserRepository userRepo,
                           ClientRepository clientRepo, CaseRepository caseRepo,
                           DeadlineRepository deadlineRepo, ActivityRepository activityRepo,
                           BillingRepository billingRepo, PasswordEncoder encoder) {
        return args -> {
            if (tenantRepo.count() > 0) {
                return;
            }

            // --- テナント1: 弁護士事務所 ---
            Tenant lawyer = new Tenant();
            lawyer.setName("福永法律事務所");
            lawyer.setVertical(Vertical.LAWYER);
            lawyer.setPlan("standard");
            tenantRepo.save(lawyer);

            User admin = new User();
            admin.setTenantId(lawyer.getId());
            admin.setLoginId("admin");
            admin.setName("管理者");
            admin.setEmail("admin@example.com");
            admin.setPasswordHash(encoder.encode("admin123"));
            admin.setRole(User.Role.admin);
            admin.setSuperAdmin(true);
            userRepo.save(admin);

            // 依頼者・案件・期日・活動・請求のデモ
            Client c1 = newClient(lawyer.getId(), admin.getId(), "山田 太郎", "ヤマダ タロウ",
                    "yamada@example.com", "090-1111-2222", "東京都新宿区1-1-1", "会社員");
            Client c2 = newClient(lawyer.getId(), admin.getId(), "佐藤 花子", "サトウ ハナコ",
                    "sato@example.com", "080-3333-4444", "神奈川県横浜市2-2-2", "自営業");
            Client c3 = newClient(lawyer.getId(), admin.getId(), "株式会社ABC商事", "エービーシーショウジ",
                    "info@abc.example.com", "03-5555-6666", "東京都千代田区3-3-3", "");
            clientRepo.save(c1); clientRepo.save(c2); clientRepo.save(c3);

            LegalCase case1 = newCase(lawyer.getId(), c1.getId(), admin.getId(),
                    "2026-001", "山田氏 離婚調停", "divorce", "東京家庭裁判所", "山田 配偶者");
            LegalCase case2 = newCase(lawyer.getId(), c2.getId(), admin.getId(),
                    "2026-002", "佐藤家 遺産分割", "inheritance", "横浜地方裁判所", "");
            LegalCase case3 = newCase(lawyer.getId(), c3.getId(), admin.getId(),
                    "2026-003", "ABC商事 売掛金回収", "civil", "東京地方裁判所", "取引先D社");
            caseRepo.save(case1); caseRepo.save(case2); caseRepo.save(case3);

            deadlineRepo.save(newDeadline(lawyer.getId(), case1.getId(), "第1回調停期日",
                    LocalDateTime.now().plusDays(10).withHour(13).withMinute(30), Deadline.Type.court));
            deadlineRepo.save(newDeadline(lawyer.getId(), case2.getId(), "遺産目録 提出期限",
                    LocalDateTime.now().plusDays(20).withHour(17).withMinute(0), Deadline.Type.document));
            deadlineRepo.save(newDeadline(lawyer.getId(), case3.getId(), "内容証明 発送",
                    LocalDateTime.now().plusDays(3).withHour(10).withMinute(0), Deadline.Type.other));

            activityRepo.save(newActivity(lawyer.getId(), c1.getId(), case1.getId(), admin.getId(),
                    Activity.Type.meeting, "初回相談", "離婚条件・親権について聞き取り。", 60));
            activityRepo.save(newActivity(lawyer.getId(), c3.getId(), case3.getId(), admin.getId(),
                    Activity.Type.phone, "支払督促の連絡", "先方より分割払いの相談あり。", 15));

            billingRepo.save(newBilling(lawyer.getId(), case1.getId(), Billing.Type.retainer,
                    "着手金", new BigDecimal("330000")));
            billingRepo.save(newBilling(lawyer.getId(), case3.getId(), Billing.Type.retainer,
                    "着手金", new BigDecimal("220000")));

            // --- テナント2: 税理士事務所（マルチテナント分離の検証用） ---
            Tenant tax = new Tenant();
            tax.setName("テスト税理士事務所");
            tax.setVertical(Vertical.TAX_ACCOUNTANT);
            tax.setPlan("solo");
            tenantRepo.save(tax);

            User taxUser = new User();
            taxUser.setTenantId(tax.getId());
            taxUser.setLoginId("tax");
            taxUser.setName("税理士テスト");
            taxUser.setEmail("tax@example.com");
            taxUser.setPasswordHash(encoder.encode("testpass123"));
            taxUser.setRole(User.Role.admin);
            userRepo.save(taxUser);

            Client tc = newClient(tax.getId(), taxUser.getId(), "顧問先 株式会社ニコニコ", "ニコニコ",
                    "kanjo@nikoniko.example.com", "03-7777-8888", "東京都港区4-4-4", "");
            clientRepo.save(tc);
            LegalCase taxCase = newCase(tax.getId(), tc.getId(), taxUser.getId(),
                    "T-2026-001", "ニコニコ社 法人税申告", "corp_tax", "", "");
            caseRepo.save(taxCase);
        };
    }

    private Client newClient(Long tid, Long lawyerId, String name, String kana,
                             String email, String tel, String addr, String occ) {
        Client c = new Client();
        c.setTenantId(tid);
        c.setName(name);
        c.setKana(kana);
        c.setEmail(email);
        c.setTel(tel);
        c.setAddress(addr);
        c.setOccupation(occ);
        c.setAssignedLawyerId(lawyerId);
        return c;
    }

    private LegalCase newCase(Long tid, Long clientId, Long lawyerId, String number,
                             String name, String type, String court, String opponent) {
        LegalCase c = new LegalCase();
        c.setTenantId(tid);
        c.setClientId(clientId);
        c.setCaseNumber(number);
        c.setCaseName(name);
        c.setCaseType(type);
        c.setAssignedLawyerId(lawyerId);
        c.setCourtName(court);
        c.setOpponent(opponent);
        c.setOpenedDate(LocalDate.now().minusDays(15));
        return c;
    }

    private Deadline newDeadline(Long tid, Long caseId, String title, LocalDateTime when, Deadline.Type type) {
        Deadline d = new Deadline();
        d.setTenantId(tid);
        d.setCaseId(caseId);
        d.setTitle(title);
        d.setDeadlineDate(when);
        d.setDeadlineType(type);
        return d;
    }

    private Activity newActivity(Long tid, Long clientId, Long caseId, Long userId,
                                 Activity.Type type, String title, String content, int min) {
        Activity a = new Activity();
        a.setTenantId(tid);
        a.setClientId(clientId);
        a.setCaseId(caseId);
        a.setUserId(userId);
        a.setActivityType(type);
        a.setActivityAt(LocalDateTime.now().minusDays(2));
        a.setTitle(title);
        a.setContent(content);
        a.setDurationMin(min);
        return a;
    }

    private Billing newBilling(Long tid, Long caseId, Billing.Type type, String title, BigDecimal amount) {
        Billing b = new Billing();
        b.setTenantId(tid);
        b.setCaseId(caseId);
        b.setBillingType(type);
        b.setTitle(title);
        b.setAmount(amount);
        b.setBilledDate(LocalDate.now().minusDays(10));
        return b;
    }
}
