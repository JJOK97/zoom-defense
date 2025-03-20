package model;

/**
 * 사용자 정보를 담는 모델 클래스
 */
public class User {
    private int userId;        // 사용자 고유 ID
    private String loginId;    // 사용자 로그인 아이디
    private String password;   // 사용자 비밀번호
    private String nickname;   // 사용자 닉네임
    
    // 사용자 정보 조회
    public User(int userId, String loginId, String nickname) {
    	this.userId = userId;
    	this.loginId = loginId;
    	this.nickname = nickname;
    }
    
    // 로그인 여부 생성자
    public User(String loginId, String password) {
    	this.loginId = loginId;
    	this.password = password;
    }
    
    // 기본 생성자
    public User() {
    }
    
    // 회원가입용 생성자
    public User(String loginId, String password, String nickname) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
    }
    
    // 전체 필드 생성자
    public User(int userId, String loginId, String password, String nickname) {
        this.userId = userId;
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
    }
    
    // Getter와 Setter 메소드
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getLoginId() {
        return loginId;
    }
    
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    @Override
    public String toString() {
        return "User [userId=" + userId + ", loginId=" + loginId + ", nickname=" + nickname + "]";
    }
} 