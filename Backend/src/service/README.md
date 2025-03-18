# service/

이 폴더는 **게임의 비즈니스 로직**을 담당합니다.  
게임의 진행 상황을 처리하고, 유저의 점수, 레벨 등을 관리하며, 여러 DAO를 조합하여 게임의 주요 로직을 구현합니다.

<br>

### 주요 파일
`GameService.java` (인터페이스) → **게임 흐름을 정의**  
`GameServiceImpl.java` (구현체) → **게임의 실제 로직을 구현**


```java
GameService gameService = new GameServiceImpl();
gameService.startGame();
gameService.nextStage();
```