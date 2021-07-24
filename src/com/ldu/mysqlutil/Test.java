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

		// select rows 예제
		SecSql sql1 = new SecSql();
		sql1.append("SELECT * FROM article ORDER BY id DESC");
		// MysqlUtil을 통해서 자동으로 쿼리 결과를 호출함. (executeQuery까지 약 100줄 생략.)
		List<Map<String, Object>> articleListMap = MysqlUtil.selectRows(sql1);
		System.out.println("articleListMap : " + articleListMap);

		// select row 예제
		SecSql sql2 = new SecSql();
		sql2.append("SELECT * FROM article WHERE id = ?", 1);
		Map<String, Object> articleMap = MysqlUtil.selectRow(sql2);
		System.out.println("articleMap : " + articleMap);

		// select row String value 예제
		SecSql sql3 = new SecSql();
		sql3.append("SELECT title FROM article WHERE id = ?", 1);
		String title = MysqlUtil.selectRowStringValue(sql3);
		System.out.println("title : " + title);

		// select row int value 예제
		SecSql sql4 = new SecSql();
		sql4.append("SELECT id FROM article WHERE id = ?", 1);
		int id = MysqlUtil.selectRowIntValue(sql4);
		System.out.println("id : " + id);

		// select row Boolean value 예제
		SecSql sql5 = new SecSql();
		sql5.append("SELECT id = 1 FROM article WHERE id = ?", 1);
		boolean idIs1 = MysqlUtil.selectRowBooleanValue(sql5);
		System.out.println("id is 1 : " + idIs1);

		// insert 예제
		// 관련 변수 한번에 수정하기 alt shift r

		String newTitle = "새 제목";
		String newBody = "새 내용";
		SecSql sql6 = new SecSql().append("INSERT INTO article");
		sql6.append("SET regDate = NOW()");
		sql6.append(", updateDate = NOW()");
		sql6.append(", title = ?", newTitle);
		sql6.append(", body = ?", newBody);

		int newArticleId = MysqlUtil.insert(sql6);
		System.out.println("newArticleId : " + newArticleId);

		// Update 테스트
		SecSql sql7 = new SecSql().append("UPDATE article");
		sql7.append("SET updateDate = NOW()");
		sql7.append(", title = CONCAT(title, '_NEW')");
		sql7.append("WHERE id = ?", 3);
		MysqlUtil.update(sql7);

		// select row 예제
		SecSql sql8 = new SecSql();
		sql8.append("SELECT title FROM article WHERE id = ?", 3);
		String article__title = MysqlUtil.selectRowStringValue(sql8);
		System.out.println("article__title : " + article__title);

		// Delete 테스트
		SecSql sql9 = new SecSql();
		sql9.append("DELETE FROM article WHERE id = ?", newArticleId);
		MysqlUtil.delete(sql9);

		// Delete 확인 예제
		SecSql sql10 = new SecSql();
		// 만들자마자 삭제
		sql10.append("SELECT COUNT(*) AS cnt FROM article WHERE id = ?", newArticleId);
		int count = MysqlUtil.selectRowIntValue(sql10);
		System.out.println("count : " + count);

		MysqlUtil.closeConnection();

//		// selectRows : 쿼리 결과를 리턴함.
//		// SecSql에서 쿼리문 정리한걸 mysqlutil로 보내서 커넥션 접속해서 값을 가져옴.
//		
//		// 여러 튜플(여러 줄)을 가져올때 Rows
//		List<Map<String, Object>> articleListMap = MysqlUtil.selectRows(new SecSql().append("SELECT * FROM article"));
//		System.out.println("articleListMap : " + articleListMap);
//		
//		// 단일 튜플(MAP : 컬럼 정보 한개씩)을 가져올때 Ros
//		// ex) id가 PK라서 한개밖에 안나올때 사용
//		Map<String, Object> articleMap = MysqlUtil
//				.selectRow(new SecSql().append("SELECT * FROM article WHERE id = ?", 1));
//		System.out.println("articleMap : " + articleMap);
//
//		// 데이터 딱 한개만 얻을 때.
//		String title = MysqlUtil.selectRowStringValue(new SecSql().append("SELECT title FROM article WHERE id = ?", 1));
//		System.out.println("title : " + title);
//
//		// 데이터 딱 한개가 숫자일 때
//		int id = MysqlUtil.selectRowIntValue(new SecSql().append("SELECT id FROM article WHERE id = ?", 1));
//		System.out.println("memberId : " + id);
//
//		// 값이 참일 때
//		boolean idIs1 = MysqlUtil
//				.selectRowBooleanValue(new SecSql().append("SELECT id = 1 FROM article WHERE id = ?", 1));
//		System.out.println("id is 1 : " + idIs1);
//		
//		// Insert 사용할 때.
//		String newTitle = "새 제목";
//		String newBody = "새 내용";
//		
//		SecSql sql = new SecSql().append("INSERT INTO article");
//		sql.append("SET regDate = NOW()");
//		sql.append(", updateDate = NOW()");
//		sql.append(", title = ?", newTitle);
//		sql.append(", body = ?", newBody);
//		
//		MysqlUtil.insert(sql);
//		
//		MysqlUtil.closeConnection();
	}

	// 기존 방식
	/*
	 * // JDBC driver name and database URL static final String JDBC_DRIVER =
	 * "com.mysql.cj.jdbc.Driver"; static final String DB_URL =
	 * "jdbc:mysql://localhost:3306/mysqlutil?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull&connectTimeout=60";
	 * 
	 * // Database credentials static final String USER = "moveuk"; static final
	 * String PASS = "1234";
	 * 
	 * public static void main(String[] args) { Connection conn = null; Statement
	 * stmt = null; try { // STEP 2: Register JDBC driver
	 * Class.forName(JDBC_DRIVER);
	 * 
	 * // STEP 3: Open a connection
	 * System.out.println("Connecting to a selected database..."); conn =
	 * DriverManager.getConnection(DB_URL, USER, PASS);
	 * System.out.println("Connected database successfully...");
	 * 
	 * // STEP 4: Execute a query System.out.println("Creating statement..."); stmt
	 * = conn.createStatement();
	 * 
	 * String sql = "SELECT * FROM article"; ResultSet rs = stmt.executeQuery(sql);
	 * // STEP 5: Extract data from result set while (rs.next()) { // Retrieve by
	 * column name int id = rs.getInt("id"); String regDate =
	 * rs.getString("regDate"); String title = rs.getString("title"); String body =
	 * rs.getString("body");
	 * 
	 * // Display values System.out.print("id: " + id);
	 * System.out.print(", regDate: " + regDate); System.out.print(", title: " +
	 * title); System.out.println(", body: " + body); } rs.close(); } catch
	 * (SQLException se) { // Handle errors for JDBC se.printStackTrace(); } catch
	 * (Exception e) { // Handle errors for Class.forName e.printStackTrace(); }
	 * finally { // finally block used to close resources try { if (stmt != null)
	 * conn.close(); } catch (SQLException se) { } // do nothing try { if (conn !=
	 * null) conn.close(); } catch (SQLException se) { se.printStackTrace(); } //
	 * end finally try } // end try System.out.println("Goodbye!"); }// end main
	 */
}