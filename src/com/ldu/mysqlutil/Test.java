package com.ldu.mysqlutil;

import java.util.List;
import java.util.Map;

public class Test {
	
	public static void main(String[] args) {
	// 새로운 방식
		// 맨처음 딱 한번만 호스트, 아이디, 패스워드, db명 설정
		// MysqlUtil.java로 보내서 private 변수에 저장함.
		MysqlUtil.setDBInfo("localhost", "moveuk", "1234", "mysqlutil");
		
		// 개발모드 실행 : 작동된 쿼리문 호출
		MysqlUtil.setDevMode(true);
		
		// selectRows : 쿼리 결과를 리턴함.
		// SecSql에서 쿼리문 정리한걸 mysqlutil로 보내서 커넥션 접속해서 값을 가져옴.
		
		// 여러 튜플(여러 줄)을 가져올때 Rows
		List<Map<String, Object>> articleListMap = MysqlUtil.selectRows(new SecSql().append("SELECT * FROM article"));
		System.out.println("articleListMap : " + articleListMap);
		
		// 단일 튜플(MAP : 컬럼 정보 한개씩)을 가져올때 Ros
		// ex) id가 PK라서 한개밖에 안나올때 사용
		Map<String, Object> articleMap = MysqlUtil
				.selectRow(new SecSql().append("SELECT * FROM article WHERE id = ?", 1));
		System.out.println("articleMap : " + articleMap);

		// 데이터 딱 한개만 얻을 때.
		String title = MysqlUtil.selectRowStringValue(new SecSql().append("SELECT title FROM article WHERE id = ?", 1));
		System.out.println("title : " + title);

		// 데이터 딱 한개가 숫자일 때
		int id = MysqlUtil.selectRowIntValue(new SecSql().append("SELECT id FROM article WHERE id = ?", 1));
		System.out.println("memberId : " + id);

		// 값이 참일 때
		boolean idIs1 = MysqlUtil
				.selectRowBooleanValue(new SecSql().append("SELECT id = 1 FROM article WHERE id = ?", 1));
		System.out.println("id is 1 : " + idIs1);
		
		// Insert 사용할 때.
		String newTitle = "새 제목";
		String newBody = "새 내용";
		
		SecSql sql = new SecSql().append("INSERT INTO article");
		sql.append("SET regDate = NOW()");
		sql.append(", updateDate = NOW()");
		sql.append(", title = ?", newTitle);
		sql.append(", body = ?", newBody);
		
		MysqlUtil.insert(sql);
		
		MysqlUtil.closeConnection();
	}
	
	// 기존 방식 
/*
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/mysqlutil?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull&connectTimeout=60";

	// Database credentials
	static final String USER = "moveuk";
	static final String PASS = "1234";

	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");

			// STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();

			String sql = "SELECT * FROM article";
			ResultSet rs = stmt.executeQuery(sql);
			// STEP 5: Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				int id = rs.getInt("id");
				String regDate = rs.getString("regDate");
				String title = rs.getString("title");
				String body = rs.getString("body");

				// Display values
				System.out.print("id: " + id);
				System.out.print(", regDate: " + regDate);
				System.out.print(", title: " + title);
				System.out.println(", body: " + body);
			}
			rs.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		System.out.println("Goodbye!");
	}// end main
*/
}