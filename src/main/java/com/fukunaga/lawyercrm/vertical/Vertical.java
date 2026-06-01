package com.fukunaga.lawyercrm.vertical;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 業種（vertical）別マスタ設定。PHP版 includes/verticals.php の移植。
 * ブランド名・用語・案件種別の選択肢を業種ごとに保持する。
 */
public enum Vertical {

    LAWYER("lawyer", "弁護士事務所", "弁護士", "LegalDesk",
            "依頼者・案件管理システム", "弁護士", "依頼者", "案件",
            "紙とExcelの案件管理から、もう卒業しませんか。",
            caseTypes(
                    "divorce", "離婚",
                    "inheritance", "相続",
                    "criminal", "刑事",
                    "civil", "民事",
                    "labor", "労働",
                    "real_estate", "不動産",
                    "corporate", "企業法務",
                    "debt", "債務整理",
                    "other", "その他")),

    TAX_ACCOUNTANT("tax_accountant", "税理士事務所", "税理士", "TaxDesk",
            "顧問先・税務案件管理システム", "税理士", "顧問先", "案件",
            "顧問先の月次・決算・期限を、ひとつの画面で。",
            caseTypes(
                    "corp_tax", "法人税",
                    "income_tax", "所得税",
                    "inheritance_tax", "相続税",
                    "consumption", "消費税",
                    "year_end", "年末調整",
                    "monthly", "月次顧問",
                    "closing", "決算",
                    "tax_audit", "税務調査",
                    "other", "その他")),

    JUDICIAL_SCRIVENER("judicial_scrivener", "司法書士事務所", "司法書士", "JudicialDesk",
            "依頼者・登記案件管理システム", "司法書士", "依頼者", "案件",
            "登記案件と決済期日を、見落とさない仕組みに。",
            caseTypes(
                    "real_estate_reg", "不動産登記",
                    "commercial_reg", "商業登記",
                    "inheritance_reg", "相続登記",
                    "company_setup", "会社設立",
                    "debt", "債務整理",
                    "guardian", "成年後見",
                    "litigation", "簡裁訴訟",
                    "other", "その他")),

    ADMINISTRATIVE_SCRIVENER("administrative_scrivener", "行政書士事務所", "行政書士", "AdminDesk",
            "依頼者・許認可案件管理システム", "行政書士", "依頼者", "案件",
            "許認可・期限・必要書類を、案件ごとに一元管理。",
            caseTypes(
                    "construction", "建設業許可",
                    "visa", "在留資格",
                    "inheritance", "相続",
                    "will", "遺言",
                    "company_setup", "会社設立",
                    "waste", "産廃許可",
                    "car_garage", "車庫証明",
                    "contract", "契約書作成",
                    "other", "その他"));

    private final String key;
    private final String label;
    private final String shortName;
    private final String brand;
    private final String sub;
    private final String roleLawyer;
    private final String clientLabel;
    private final String caseLabel;
    private final String lpHeadline;
    private final Map<String, String> caseTypes;

    Vertical(String key, String label, String shortName, String brand, String sub,
             String roleLawyer, String clientLabel, String caseLabel, String lpHeadline,
             Map<String, String> caseTypes) {
        this.key = key;
        this.label = label;
        this.shortName = shortName;
        this.brand = brand;
        this.sub = sub;
        this.roleLawyer = roleLawyer;
        this.clientLabel = clientLabel;
        this.caseLabel = caseLabel;
        this.lpHeadline = lpHeadline;
        this.caseTypes = caseTypes;
    }

    private static Map<String, String> caseTypes(String... kv) {
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put(kv[i], kv[i + 1]);
        }
        return m;
    }

    /** DB保存値（key）から Vertical を引く。未知なら LAWYER。 */
    public static Vertical fromKey(String key) {
        if (key != null) {
            for (Vertical v : values()) {
                if (v.key.equals(key)) {
                    return v;
                }
            }
        }
        return LAWYER;
    }

    /** case_type コードを業種別の日本語ラベルに変換。未知ならコードをそのまま返す。 */
    public String caseTypeLabel(String code) {
        return caseTypes.getOrDefault(code, code);
    }

    public String getKey() { return key; }
    public String getLabel() { return label; }
    public String getShortName() { return shortName; }
    public String getBrand() { return brand; }
    public String getSub() { return sub; }
    public String getRoleLawyer() { return roleLawyer; }
    public String getClientLabel() { return clientLabel; }
    public String getCaseLabel() { return caseLabel; }
    public String getLpHeadline() { return lpHeadline; }
    public Map<String, String> getCaseTypes() { return caseTypes; }
}
