package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 데이터베이스 연결을 관리하는 싱글톤 클래스
 */
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    
    // DB 연결 정보
    private final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private final String URL = "jdbc:oracle:thin:@project-db-campus.smhrd.com:1523:xe";
    private final String USER = "hapjeong_24SW_DS_p1_5";
    private final String PASSWORD = "smhrd5";
    
    // 생성자는 private으로 외부에서 인스턴스 생성 방지
    private DBConnection() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로딩 실패: " + e.getMessage());
        }
    }
    
    // 싱글톤 패턴 구현 메소드
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
    
    // DB 연결 가져오기
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("DB 연결 성공!");
            }
        } catch (SQLException e) {
            System.out.println("DB 연결 실패: " + e.getMessage());
        }
        return connection;
    }
    
    // 연결 닫기
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("DB 연결 닫기 실패: " + e.getMessage());
            }
        }
    }
} 