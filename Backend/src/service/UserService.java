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
} 