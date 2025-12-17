# --- Stage 1: Build ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy file pom.xml và tải dependencies trước (để tận dụng cache layer của Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy toàn bộ source code và build
COPY src ./src
# Build ra file jar, bỏ qua test để nhanh hơn (khuyên dùng clean package)
RUN mvn clean package -DskipTests

# --- Stage 2: Run ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy file jar từ Stage 1 sang Stage 2
COPY --from=build /app/target/*.jar app.jar

# Tạo thư mục uploads để lưu ảnh
RUN mkdir uploads

# Expose port 8080
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]