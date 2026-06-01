# lawyer-crm-java

PHP版 `lawyer_crm` / `lawyer_crm_saas`（LegalDesk / TaxDesk / JudicialDesk / AdminDesk）の
**Java (Spring Boot) 移植版**。4業種対応マルチテナント法務CRMのコア機能を Java で再実装したもの。

## 技術スタック

- Java 17 / Spring Boot 3.3
- Spring MVC + Thymeleaf（サーバサイドレンダリング、PHP版と同じ画面構成）
- Spring Security（`login_id` + パスワード認証、BCrypt）
- Spring Data JPA
- DB: H2（デフォルト・インメモリ／すぐ起動）／ MySQL（本番プロファイル）
- UI: Bootstrap 5（CDN）

## 実装済み機能

| 機能 | 内容 |
|------|------|
| マルチテナント | 全業務テーブルに `tenant_id`。ログインユーザーのテナントで全クエリをスコープ |
| 4業種対応 | `Vertical` enum でブランド名・用語・案件種別を業種別に切替（PHP版 verticals.php 相当） |
| 認証 | login_id + パスワード（BCrypt）。Spring Security のフォームログイン |
| 依頼者（顧問先）管理 | 一覧・詳細・新規/編集・削除 |
| 案件管理 | 一覧・詳細・新規/編集・削除。業種別の案件種別 |
| 期日管理 | 案件への期日追加・完了トグル・未完了一覧 |
| 活動記録 | 電話/面談/メール等の記録を依頼者・案件に追加 |
| 請求管理 | 着手金/報酬金/実費等の登録・入金トグル |
| ダッシュボード | 件数サマリ・直近期日・最近の活動 |

> PHP版にあった Stripe/PayPay 決済、AIによる契約書レビュー、見積算出、顧客予約システムは
> 本移植のスコープ外（コアCRMに集中）。必要なら追加可能。

## 起動方法

### 1. H2（インメモリ・すぐ動く）

```bash
export JAVA_HOME=/home/afky5/tools/jdk-17.0.13+11
export PATH=$JAVA_HOME/bin:/home/afky5/tools/apache-maven-3.9.9/bin:$PATH
cd /home/afky5/lawyer_crm_java
mvn spring-boot:run
```

→ http://localhost:8080/login

初回起動時にデモデータを自動投入：

| ログインID | パスワード | 業種 | テナント |
|-----------|-----------|------|---------|
| `admin` | `admin123` | 弁護士（LegalDesk） | 福永法律事務所 |
| `tax` | `testpass123` | 税理士（TaxDesk） | テスト税理士事務所 |

2アカウントでログインし分けると、ブランド名・用語・表示データが
テナントごとに完全に分離されることを確認できます。

### 2. MySQL（本番）

```bash
mvn package
java -jar target/lawyer-crm-1.0.0.jar --spring.profiles.active=mysql
```

接続情報は環境変数 `DB_URL` / `DB_USER` / `DB_PASS` で上書き可能
（`src/main/resources/application-mysql.properties` 参照）。
Java版は `tenant_id` 列を要求するため、既存PHP版DBとはスキーマが異なります。
新規DB（例: `lawyer_crm_java`）での利用を推奨。

## ディレクトリ構成

```
src/main/java/com/fukunaga/lawyercrm/
├── LawyerCrmApplication.java   起動クラス
├── vertical/Vertical.java      4業種マスタ（enum）
├── entity/                     JPAエンティティ（全て tenant_id 保持）
├── repository/                 Spring Data リポジトリ（tenantId スコープ）
├── security/                   Spring Security 設定・UserDetails
├── config/DataSeeder.java      初回デモデータ投入
└── web/                        コントローラ
src/main/resources/
├── templates/                  Thymeleaf（fragments/ に共通レイアウト）
└── application*.properties     設定（H2 / mysql プロファイル）
```
