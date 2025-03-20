package service;

import dao.UserDAO;
import model.User;

/**
 * UserService 인터페이스 구현체
 */
public class UserServiceImpl implements UserService {
    private UserDAO userDAO;
    
    // 생성자
    public UserServiceImpl() {
        this.userDAO = new UserDAO();
    }
    
    @Override
    public boolean register(User user) {
        
    	// 사용자 정보 조회
    	if(getUserById(user.getUserId(),user.getLoginId(),user.getNickname())) {
    		System.out.println("사용자 정보 조회에 실패했습니다. 다시 시도해주세요.");
    		return false;
    	}
    	
    	// 로그인 성공 여부 체크
    	if(validate(user.getLoginId(),user.getPassword())) {
    		System.out.println("로그인에 실패하셨습니다. 다시 입력해주세요.");
    		return false;
    	}
    	
    	// 아이디 중복 체크 후 등록
        if (isIdDuplicated(user.getLoginId())) {
            System.out.println("이미 사용 중인 아이디입니다.");
            return false;
        }
        
        // 유효성 검사
        if (user.getLoginId() == null || user.getLoginId().trim().isEmpty()) {
            System.out.println("아이디는 필수 입력 항목입니다.");
            return false;
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            System.out.println("비밀번호는 필수 입력 항목입니다.");
            return false;
        }
        
        if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            System.out.println("닉네임은 필수 입력 항목입니다.");
            return false;
        }
        
        return userDAO.registerUser(user);
    }
    
    
	@Override
    public boolean isIdDuplicated(String loginId) {
        return userDAO.checkDuplicateId(loginId);
    }

	@Override
	public boolean validate(String loginId, String password) {
		
		return false;
	}

	@Override
	public boolean getUserById(int userId, String loginId, String nickname) {
		
		return false;
	}

	
	
	
	
	


	
	
} 