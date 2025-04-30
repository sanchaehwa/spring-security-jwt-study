## 2025 Spring🍃 Security JWT study

### 진행 날짜
25.04.  ~  

### Spring Security JWT 실습 환경 
- Java: 17
- Spring 3.4.4
- Spring Security
- Spring Data JPA - MySQL
- Lombok
- Gradle

### Spring JWT Security 
기존의 스프링 시큐리티는 서버 세션에 인증 정보를 저장하고 이를 통해 사용자를 식별했지만, JWT 방식은 로그인 시 발급된 토큰을 클라이언트가 보관하고, 요청마다 헤더에 담아 전송함으로써 인증을 처리한다. 
### Spring JWT Security 동작원리
![9A9FBC06-04A1-48F4-A9A2-07C58EB38070_1_102_o](https://github.com/user-attachments/assets/d352d8f1-ac2e-4888-b28a-f96df828d369)
**회원가입 - POST /join**

1. `JoinController`가 요청을 받고 DTO를 JoinService에 전달
2. `JoinService`는 DTO를 `UserEntity로` 변환하여 `UserRepository`를 통해 DB에 저장
3. 회원가입 완료 후 단순 성공 응답 반환 (※ JWT 토큰 발급 없음, 인증 과정 없음)

**로그인 - POST /login**

1. 클라이언트가 JSON 형식으로 로그인 요청 전송

```
POST /login
Content-Type: application/json
{
  "username": "admin",
  "password": "1234"
}

```

1. `UsernamePasswordAuthenticationFilter`가 요청을 가로채 `AuthenticationManager`에 인증 요청
2. `AuthenticationManager`는 `UserDetailsService`를 통해 사용자 정보를 로드
3. 인증 성공 시 `JWTUtil`을 통해 JWT 토큰 생성 및 응답
4. 클라이언트는 이후 요청 시 JWT 토큰을 헤더에 포함

```java
GET /api/users
Authorization: Bearer <JWT_TOKEN>

```

1. 서버는 `JwtAuthenticationFilter`에서 토큰 검증 후, `SecurityContext`에 인증 정보 저장 → 인증된 사용자 처리

```
현재 실습에서는 Access Token과 Refresh Token을 분리하지 않고, 단일 JWT 토큰만으로 인증을 처리하였고
→ 로그인 시 발급된 하나의 JWT를 클라이언트가 보관하고, 요청마다 헤더에 포함시켜 인증함

```
