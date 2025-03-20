package contorller;

import model.User;
import service.UserService;
import service.UserServiceImpl;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러 클래스
 */
public class UserController {
    private UserService userService;
    
    // 생성자
    public UserController() {
        this.userService = new UserServiceImpl();
    }
    
    /**
     * 회원가입 요청 처리
     * @param loginId 사용자 로그인 아이디
     * @param password 비밀번호
     * @param nickname 닉네임
     * @return 회원가입 성공 여부
     */
    public boolean processRegister(String loginId, String password, String nickname) {
        User user = new User(loginId, password, nickname);
        return userService.register(user);
    }
    
    /**
     * 아이디 중복 체크 요청 처리
     * @param loginId 체크할 아이디
     * @return 중복 여부 (true: 중복, false: 사용 가능)
     */
    public boolean checkDuplicateId(String loginId) {
        return userService.isIdDuplicated(loginId);
    }
    
    /**
     * 로그인 인증 메소드
     * @param userLoginId 체크할 아이디
     * @param password 체크할 비밀번호
     * @return 인증된 사용자 정보, 실패 시 null
     */
    public User validateLogin(String userLoginId, String password) {
        return userService.validate(userLoginId, password);
    }
    
    /**
     * 사용자 ID로 사용자 정보 조회
     * @param userId 사용자 ID
     * @return 사용자 정보, 없으면 null
     */
    public User getUserById(int userId) {
        return userService.getUserById(userId);
    }
    
} 