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
     * @param loginId 체크할 아이디
     * @param loginPw 체크할 비밀번호
     * @return 로그인 성공 여부(true: 로그인성공, false: 로그인실패)
     */
	boolean validate(String loginId, String password);
	
	/**
     * 사용자 정보 조회
     * @param getUserById 체크할 아이디
     * @return 정보 조회 성공 여부 (true: 성공, false: 실패)
     */
	boolean getUserById(int userId, String loginId, String nickname);
	
} 