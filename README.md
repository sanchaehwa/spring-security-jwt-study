## 2025 Spring🍃 Security Advanced JWT study

### 진행 날짜
25.05.05  ~  

### 진행 내용

#### 다중 토큰 방식 도입
- 기존 구조의 한계: Access Token 단독 사용
  - Access Token은 유효기간이 있으며, 유효기간이 지나면 무조건 로그인을 다시 해야 함.
  - 사용자 편의성을 위해 유효기간을 길게 설정하면, 탈취당했을 경우, 장시간 인증된 사용자로 위장한 요청이 가능해짐.
  - 유효기간을 짧게 설정하면 보안은 향상되지만,사용자가 자주 로그인을 반복해야 해 불편함이 발생.
- 해결 방법: Refresh Token 도입
  - Access Token은 짧은 유효기간으로 설정하여 탈취 위험을 줄이고, Refresh Token은 긴 유효기간을 가지며, Access Token이 만료되었을 때 새로 발급받기 위한 수단으로 사용.

#### Refresh Token 보안 문제 - (해결) RTR 도입
❗문제점
- Refresh Token이 탈취되면, 만료 전까지 공격자가 재발급을 통해 Access Token을 지속적으로 얻을 수 있음.
- 해결책: Refresh Token Rotation (RTR)
  - Access Token을 재발급할 때마다 새로운 Refresh Token도 함께 재발급.
  - 서버는 들어온 Refresh Token을 Redis에 저장된 것과 비교: 일치하지 않으면 재로그인 요구.

> 스터디에서는 보안을 강화하기 위해, Access Token + Refresh Token을 활용한 다중 토큰 방식적용. Redis(In-Memory 저장소)를 사용하여 Refresh Token 관리

#### 다중 토큰 JWT 흐름 (Redis 사용)

![흐름도](https://github.com/user-attachments/assets/4a62a499-9d87-4a82-a69f-7bc9675024c2)


### Spring Security JWT 심화 실습 환경 
- Java: 17
- Spring 3.4.4
- Spring Security
- Spring Data JPA - MySQL
- redis: 8.0.1
- Lombok
- Gradle

