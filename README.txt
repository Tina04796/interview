《共享空間預約系統 (ShareSpace)》

此專案為基於 Spring Boot 框架的共享空間預約管理系統後端功能。提供完整的 RESTful API、認證授權 (JWT)、空間管理 (Admin CRUD)、時段預約和衝突檢查邏輯。



〈技術棧〉

- 框架: Spring Boot 3

- 安全: Spring Security, JSON Web Token (JWT)

- 資料庫: SQL Server

- ORM 層: Spring Data JPA / Hibernate

- 輔助: Lombok

- 測試: Postman



〈架構〉

- 核心

	三層架構分離 (Controller / Service / Repository)，確保可讀性與可維護性

	數據傳輸物件 (DTO): 分離 Contract 與 Entity，用於 JSR 380 驗證，確保敏感資料不外洩

- 認證與安全

	無需登入即可查詢空間的可用時段

	Token 儲存 User ID 為身份識別

	基於 JWT 的無狀態認證，密碼使用 BCrypt 加密

	限制只有 ADMIN 能增刪改空間

	用戶只能查詢和取消自己的預約，防橫向越權

- 穩健性

	中央例外控制 (Global Exception Handling): 透過 @ControllerAdvice 中央化例外處理，將後端例外統一對應為 HTTP 狀態碼 (400, 401, 404, 409)

	預約業務邏輯: 支援多個連續時段，並包含衝突檢查



〈測試〉

*所有 API 請求為 8081/interview/api*

- 使用者與認證

	註冊: (POST) /users/register
	(創建新的 USER 帳戶)

	登入: (POST) /users/login
	(成功時回傳 Token)

	查詢自己: (GET) /users/{id}
	(測試能否透過 JWT 獲取自己的資料)

	查詢所有 Rooms: (GET) /rooms
	(測試 JWT 是否有效，應返回 200 OK)

	Headers 格式: Authorization > Bearer Token > 右方輸入登入後的 Token

- 空間查詢與管理

	查詢所有空間: (GET) /rooms

	查詢單一空間: (GET) /rooms/{id}

	創建新空間: (POST) /rooms
	(測試 403 Forbidden)

	更新空間資料: (PUT) /rooms/{id}
　　　　
	刪除空間: (DELETE) /rooms/{id}

- 預約

	查詢某房間在某天的可用時段: (GET) /reservations/slots/{roomId}
	(需帶 date ex: http://localhost:8081/interview/api/reservations/slots/1?date=2026-03-15)

	建立預約: (POST) /reservations
	(Body 需含 roomId 和時段列表 ex: 	{
					"roomId": 1,
					"selectedSlotTimes": [
					"2026-03-15T10:00:00",
					"2026-03-15T10:30:00",
					"2026-03-15T11:00:00"
 					 ]
					})

	查詢自己的預約: (GET) /reservations/user/{userId}
	(限自己或 ADMIN)

　	取消預約: (DELETE) /reservations/{id}
	(服務層會檢查是不是自己的預約)

	查詢空間在日期範圍內的預約: (GET) /reservations/room/{roomId}
	(需帶 startDate 和 endDate ex: http://localhost:8081/interview/api/reservations/room/1?startDate=2026-03-15&endDate=2026-03-15)

