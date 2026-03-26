《共享空間預約系統 (ShareSpace)》

此專案為基於 Spring Boot 框架的共享空間預約管理系統後端功能。提供完整的 RESTful API、認證授權 (JWT)、空間管理 (Admin CRUD)、時段預約和衝突檢查邏輯。



〈技術棧〉

- 框架: Spring Boot

- 安全: Spring Security, JSON Web Token (JWT)

- 資料庫: SQL Server

- ORM 層: Spring Data JPA / Hibernate

- 輔助: Lombok

- 測試: Postman

- 雲端佈署: CLI



〈架構〉

- 核心

	三層架構分離 (Controller / Service / Repository)，確保可讀性與可維護性

	數據傳輸物件 (DTO): 分離 Contract 與 Entity，讓資料不外洩

- 認證與安全

	無需登入即可查詢空間的可用時段

	Token 儲存 User ID 為身份識別

	基於 JWT 的無狀態認證，密碼使用 BCrypt 加密

	限制只有 ADMIN 能增刪改空間

	用戶只能查詢和取消自己的預約，防橫向越權

- 穩健性

	中央例外控制 (Global Exception Handling): 透過 @ControllerAdvice 中央化例外處理，將後端例外統一對應為 HTTP 狀態碼 (400, 401, 404, 409)

	預約業務邏輯: 支援多個連續時段，並包含衝突檢查
