-- ZOOM Defense 게임 데이터베이스 스키마

-- 사용자 테이블 생성
-- 회원가입과 로그인에 필요한 정보를 저장하는 테이블입니다.
CREATE TABLE USERS (
    USER_ID NUMBER PRIMARY KEY,              -- 사용자 고유 번호
    USER_LOGIN_ID VARCHAR2(50) UNIQUE NOT NULL,  -- 로그인 아이디 (중복 불가)
    PASSWORD VARCHAR2(100) NOT NULL,         -- 비밀번호
    NICKNAME VARCHAR2(100) NOT NULL          -- 게임에서 표시될 닉네임
);

-- ID 자동 증가를 위한 시퀀스
-- 새로운 사용자가 회원가입할 때마다 USER_ID가 자동으로 1씩 증가합니다.
CREATE SEQUENCE USER_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- ID 자동 생성을 위한 트리거
-- 사용자 정보가 추가될 때 자동으로 USER_ID를 생성합니다.
CREATE OR REPLACE TRIGGER USER_TRG
    BEFORE INSERT ON USERS
    FOR EACH ROW
BEGIN
    SELECT USER_SEQ.NEXTVAL
    INTO :NEW.USER_ID
    FROM DUAL;
END;
/

-- 타워 정보 테이블
-- 게임에서 사용할 수 있는 다양한 방어 타워의 정보를 저장합니다.
CREATE TABLE TOWERS (
    TOWER_ID NUMBER PRIMARY KEY,             -- 타워 고유 번호
    TOWER_NAME VARCHAR2(100) NOT NULL,       -- 타워 이름
    DAMAGE NUMBER DEFAULT 1,                 -- 공격력: 적에게 입히는 데미지
    RANGE NUMBER DEFAULT 5,                  -- 공격 범위: 타워가 공격할 수 있는 범위
    ATTACK_SPEED NUMBER DEFAULT 1,           -- 공격 속도: 초당 공격 횟수
    COST NUMBER DEFAULT 100,                 -- 구매 비용: 타워 설치에 필요한 비용
    UPGRADE_COST NUMBER DEFAULT 50,          -- 업그레이드 비용: 타워 강화에 필요한 비용
    LEVEL NUMBER DEFAULT 1                   -- 타워 레벨(업그레이드 단계)
);

-- 타워 시퀀스
-- 새로운 타워가 추가될 때마다 TOWER_ID가 자동으로 1씩 증가합니다.
CREATE SEQUENCE TOWER_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 타워 트리거
-- 타워 정보가 추가될 때 자동으로 TOWER_ID를 생성합니다.
CREATE OR REPLACE TRIGGER TOWER_TRG
    BEFORE INSERT ON TOWERS
    FOR EACH ROW
BEGIN
    SELECT TOWER_SEQ.NEXTVAL
    INTO :NEW.TOWER_ID
    FROM DUAL;
END;
/

-- 적 정보 테이블
-- 게임에 등장하는 다양한 적의 정보를 저장합니다.
CREATE TABLE ENEMIES (
    ENEMY_ID NUMBER PRIMARY KEY,             -- 적 고유 번호
    ENEMY_NAME VARCHAR2(100) NOT NULL,       -- 적 이름
    HEALTH NUMBER DEFAULT 10,                -- 체력: 적이 죽기 전까지 버틸 수 있는 데미지 양
    SPEED NUMBER DEFAULT 1,                  -- 이동 속도: 적이 이동하는 속도
    REWARD NUMBER DEFAULT 5,                 -- 보상: 적을 처치했을 때 받는 돈
    DAMAGE NUMBER DEFAULT 1                  -- 데미지: 적이 목적지에 도달했을 때 입히는 피해
);

-- 적 시퀀스
-- 새로운 적이 추가될 때마다 ENEMY_ID가 자동으로 1씩 증가합니다.
CREATE SEQUENCE ENEMY_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 적 트리거
-- 적 정보가 추가될 때 자동으로 ENEMY_ID를 생성합니다.
CREATE OR REPLACE TRIGGER ENEMY_TRG
    BEFORE INSERT ON ENEMIES
    FOR EACH ROW
BEGIN
    SELECT ENEMY_SEQ.NEXTVAL
    INTO :NEW.ENEMY_ID
    FROM DUAL;
END;
/

-- 게임 세션 테이블
-- 플레이어가 게임을 플레이하는 각 세션의 정보를 저장합니다.
CREATE TABLE GAME_SESSIONS (
    SESSION_ID NUMBER PRIMARY KEY,           -- 세션 고유 번호
    USER_ID NUMBER REFERENCES USERS(USER_ID) NOT NULL,  -- 사용자 ID
    LOAD_TIME TIMESTAMP DEFAULT SYSTIMESTAMP,-- 세션 시작 시간
    SCORE NUMBER DEFAULT 0,                  -- 획득한 점수
    WAVE NUMBER DEFAULT 1,                   -- 현재 웨이브
    MONEY NUMBER NOT NULL,                   -- 보유 금액
    LIFE NUMBER NOT NULL                     -- 남은 생명력
);

-- 게임 세션 시퀀스
-- 새 게임 세션이 시작될 때마다 SESSION_ID가 자동으로 1씩 증가합니다.
CREATE SEQUENCE SESSION_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 게임 세션 트리거
-- 게임 세션이 시작될 때 자동으로 SESSION_ID를 생성합니다.
CREATE OR REPLACE TRIGGER SESSION_TRG
    BEFORE INSERT ON GAME_SESSIONS
    FOR EACH ROW
BEGIN
    SELECT SESSION_SEQ.NEXTVAL
    INTO :NEW.SESSION_ID
    FROM DUAL;
END;
/

-- 타워 배치 테이블
-- 게임 중에 플레이어가 배치한 타워의 위치 정보를 저장합니다.
CREATE TABLE TOWER_PLACEMENTS (
    PLACEMENT_ID NUMBER PRIMARY KEY,         -- 배치 고유 번호
    SESSION_ID NUMBER REFERENCES GAME_SESSIONS(SESSION_ID) NOT NULL,  -- 게임 세션 ID
    TOWER_ID NUMBER REFERENCES TOWERS(TOWER_ID) NOT NULL,  -- 타워 ID
    POSITION_X NUMBER NOT NULL,              -- X좌표
    POSITION_Y NUMBER NOT NULL              -- Y좌표
);

-- 타워 배치 시퀀스
-- 새로운 타워가 배치될 때마다 PLACEMENT_ID가 자동으로 1씩 증가합니다.
CREATE SEQUENCE PLACEMENT_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 타워 배치 트리거
-- 타워가 배치될 때 자동으로 PLACEMENT_ID를 생성합니다.
CREATE OR REPLACE TRIGGER PLACEMENT_TRG
    BEFORE INSERT ON TOWER_PLACEMENTS
    FOR EACH ROW
BEGIN
    SELECT PLACEMENT_SEQ.NEXTVAL
    INTO :NEW.PLACEMENT_ID
    FROM DUAL;
END;
/

-- 랭킹 테이블
-- 게임의 최고 점수 기록을 저장합니다.
CREATE TABLE RANKINGS (
    RANKING_ID NUMBER PRIMARY KEY,           -- 랭킹 고유 번호
    USER_ID NUMBER REFERENCES USERS(USER_ID) NOT NULL,  -- 사용자 ID
    SCORE NUMBER NOT NULL,                   -- 획득한 점수
    DATE DATE DEFAULT SYSDATE                -- 기록 달성 날짜
);

-- 랭킹 시퀀스
-- 새로운 랭킹이 추가될 때마다 RANKING_ID가 자동으로 1씩 증가합니다.
CREATE SEQUENCE RANKING_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 랭킹 트리거
-- 랭킹 정보가 추가될 때 자동으로 RANKING_ID를 생성합니다.
CREATE OR REPLACE TRIGGER RANKING_TRG
    BEFORE INSERT ON RANKINGS
    FOR EACH ROW
BEGIN
    SELECT RANKING_SEQ.NEXTVAL
    INTO :NEW.RANKING_ID
    FROM DUAL;
END;
/