### 建構 (Build Stage) - 用 Maven 和 Java 17 編譯 ###
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# 設定工作目錄
WORKDIR /app

# 複製 Maven 專案設定檔
COPY pom.xml .
COPY .mvn .mvn

# 複製 Spring Boot 程式碼
COPY src src

# 執行 Maven 建置，產生 JAR 檔
# 跳過測試 (因為在 Cloud Build 環境中執行單元測試可能更複雜)
RUN mvn clean install -DskipTests

# 執行 (Runtime Stage) - Java 17 JRE 
FROM eclipse-temurin:17-jre-alpine

# 設定時區和語言環境
ENV TZ Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 將 JAR 檔從建構階段複製過來
# 假設生成的 JAR 檔只有一個
COPY --from=builder /app/target/*.jar app.jar

# 暴露應用程式使用的 Port (Spring Boot 預設為 8080)
EXPOSE 8080

# 啟動 Spring Boot 應用程式
ENTRYPOINT ["java", "-jar", "app.jar"]