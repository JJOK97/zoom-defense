# config/

이 폴더는 **JDBC 설정 및 유틸리티 클래스**를 포함합니다.  
DB 연결과 관련된 설정을 관리하고, 전역적으로 사용할 수 있는 공통 유틸리티 클래스를 제공합니다.

<br>

### 주요 파일  
**DBConnection.java** : DB 연결을 관리하는 싱글톤 클래스  

```java
Connection conn = DBConnection.getInstance().getConnection();
```
