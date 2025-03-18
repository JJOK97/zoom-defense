# dao/

이 폴더는 **DB 접근 계층**을 담당합니다.  
JDBC를 사용하여 DB와 상호작용하며, 데이터를 읽고 쓰는 작업을 처리합니다.

<br>

### 주요 파일

PlayerDAO.java : 유저 데이터를 DB에 저장하거나 불러오는 클래스

```java
PlayerDAO playerDAO = new PlayerDAO();
```