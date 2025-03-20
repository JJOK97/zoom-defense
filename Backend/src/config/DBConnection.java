package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 데이터베이스 연결을 관리하는 싱글톤 클래스
 */
public class DBConnection {
    private static DBConnection instance;
    
    // DB 연결 정보
    private final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private final String URL = "jdbc:oracle:thin:@project-db-campus.smhrd.com:1523:xe";
    private final String USER = "hapjeong_24SW_DS_p1_5";
    private final String PASSWORD = "smhrd5";
    
    // Connection을 쓰레드별로 관리 (쓰레드 로컬 변수)
    private ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    
    // 생성자는 private으로 외부에서 인스턴스 생성 방지
    private DBConnection() {
        try {
            Class.forName(DRIVER);
            System.out.println("Oracle JDBC 드라이버 로드 성공");
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로딩 실패: " + e.getMessage());
            e.printStackTrace();
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
        Connection conn = connectionHolder.get();
        
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                conn.setAutoCommit(false); // 트랜잭션 자동 커밋 비활성화
                connectionHolder.set(conn);
                System.out.println("DB 연결 성공! (쓰레드: " + Thread.currentThread().getName() + ")");
            }
        } catch (SQLException e) {
            System.out.println("DB 연결 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        return conn;
    }
    
    // 트랜잭션 커밋
    public void commit() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.commit();
                    System.out.println("트랜잭션 커밋 완료");
                }
            } catch (SQLException e) {
                System.out.println("트랜잭션 커밋 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    // 트랜잭션 롤백
    public void rollback() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.rollback();
                    System.out.println("트랜잭션 롤백 완료");
                }
            } catch (SQLException e) {
                System.out.println("트랜잭션 롤백 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    // 연결 닫기
    public void closeConnection() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("DB 연결 닫기 완료");
                }
            } catch (SQLException e) {
                System.out.println("DB 연결 닫기 실패: " + e.getMessage());
                e.printStackTrace();
            } finally {
                connectionHolder.remove(); // 쓰레드 로컬에서 제거
            }
        }
    }
} 