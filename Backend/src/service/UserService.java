package service;

import model.User;

/**
 * 사용자 관련 비즈니스 로직을 정의하는 인터페이스
 */
public interface UserService {
    /**
     * 회원가입
     * @param user 등록할 사용자 정보
     * @return 등록 성공 여부
     */
    boolean register(User user);
    
    /**
     * 아이디 중복 체크
     * @param loginId 체크할 아이디
     * @return 중복 여부 (true: 중복, false: 사용 가능)
     */
    boolean isIdDuplicated(String loginId);
    
    /** 
     * 로그인 정보 확인
     * @param userLoginId 체크할 아이디
     * @param password 체크할 비밀번호
     * @return 인증된 사용자 정보, 실패 시 null
     */
	User validate(String userLoginId, String password);
	
	/**
     * 사용자 ID로 사용자 정보 조회
     * @param userId 사용자 ID
     * @return 사용자 정보, 없으면 null
     */
	User getUserById(int userId);
} 