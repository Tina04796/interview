《共享空間預約系統 (ShareSpace)》

此專案為基於 Spring Boot 框架的共享空間預約管理系統後端功能。提供完整的 RESTful API、認證授權 (JWT)、空間管理 (Admin CRUD)、時段預約和衝突檢查邏輯。



〈技術棧〉

- 框架: Spring Boot 3

- 安全: Spring Security, JSON Web Token (JWT)

- 資料庫: SQL Server

- ORM 層: Spring Data JPA / Hibernate

- 輔助: Lombok

- 測試: Postman

- 部屬: CLI



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

- 使用者與認證

	註冊: (POST) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/users/register
		{
		    "username": "2",
		    "password": "123",
		    "email": "2@gmail.com"
		} 


	登入: (POST) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/users/login
	(回傳 Token)
		{
		    "usernameOrEmail": "2",
		    "password": "123"
		} 


	刪除: (DEL) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/users/2

- 空間查詢與管理

	創建新空間: (POST) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/rooms
	{
	    "name":"Room A",
	    "location":"1st Floor",
	    "capacity":10
	}

	查詢所有空間: (GET) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/rooms
	(測試 JWT 是否有效，應返回 200 OK)

	查詢單一空間: (GET) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/rooms/1

	更新空間資料: (PUT) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/rooms/1
	{
	    "name":"Room A",
	    "location":"2nd Floor",
	    "capacity":20
	}
　　　　
	刪除空間: (DELETE) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/rooms/1

- 預約

	查詢某房間在某天的可用時段: (GET) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/reservations/slots/1?date=2026-03-15
	(不用登入)

	建立預約: (POST) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/reservations
		{
		"roomId": 1,
		"selectedSlotTimes": [
		"2026-03-15T10:00:00",
		"2026-03-15T10:30:00",
		"2026-03-15T11:00:00"
 		 ]
		})

	用使用者id查詢該使用者的預約: (GET) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/reservations/user/2
	(限自己或 ADMIN)

	用預約id查詢預約的內容: (GET) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/reservations/2

　	用預約id取消預約: (DELETE) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/reservations/2
	(服務層會檢查是不是自己的預約)

	用空間id查詢該空間在日期範圍內的預約: (GET) https://reservation-api-svc-675340599080.asia-east1.run.app/interview/api/reservations/room/1?startDate=2026-03-15&endDate=2026-03-15
