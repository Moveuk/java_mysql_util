package com.ldu.mysqlutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlUtil {
	// db 접속 정보 세팅
	private static String dbHost;
	private static String dbLoginId;
	private static String dbLoginPw;
	private static String dbName;
	private static boolean isDevMode;

	// 일종의 커넥션 풀을 만드는 개념.
	// JNDI Resources DataSource 같은 개념을 직접 만들은듯.
	private static Map<Long, Connection> connections;

	static {
		connections = new HashMap<>();
	}

	public static void setDevMode(boolean isDevMode) {
		MysqlUtil.isDevMode = isDevMode;
	}

	public static boolean isDevMode() {
		return isDevMode;
	}

	// db 호스트 메소드 작성
	// 객체에 db가 저장될 수 있도록 생성자 만들어줌.
	public static void setDBInfo(String dbHost, String dbLoginId, String dbLoginPw, String dbName) {
		MysqlUtil.dbHost = dbHost;
		MysqlUtil.dbLoginId = dbLoginId;
		MysqlUtil.dbLoginPw = dbLoginPw;
		MysqlUtil.dbName = dbName;
	}

	public static void closeConnection() {
		long currentThreadId = Thread.currentThread().getId();

		if (!connections.containsKey(currentThreadId)) {
			return;
		}

		Connection connection = connections.get(currentThreadId);

		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		connections.remove(currentThreadId);
	}

	// test.java 에서 Connection을 얻는 과정
	// conn drivermanager 거쳐서, stmt sql 하고 이런과정 한번에 하도록 기능 구현
	private static Connection getConnection() {
		// 현재 getConnection 부른 스레드 id 가져옴.
		long currentThreadId = Thread.currentThread().getId();

		// 기존에 커넥션 키값이 존재하는지
		// 존재 하지 않으면 처음 커넥션 시도이므로 Driver 찾아서(Class.forname: 드라이버정보넘김)
		if (!connections.containsKey(currentThreadId)) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				throw new MysqlUtilException(e);
			}

			Connection connection = null;

			// 수업에서는 oracle을 사용해서 String url = "jdbc:oracle:thin:@localhost:1521:xe"; 이렇게 넣음
			// 지금 mysql을 사용하고 localhost 넣어주고
			String url = "jdbc:mysql://" + dbHost + "/" + dbName
					+ "?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull&connectTimeout=60";
			try {
				// 드라이버 커넥션 접속함.
				connection = DriverManager.getConnection(url, dbLoginId, dbLoginPw);
				// 커넥션 흔적을 남기기 위해서 현재 스레드 아이디값을 key값으로 이 커넥션을 저장해둠.
				connections.put(currentThreadId, connection);

			} catch (SQLException e) {
				closeConnection();
				throw new MysqlUtilException(e);
			}
		}

		return connections.get(currentThreadId);
	}

	public static Map<String, Object> selectRow(SecSql sql) {
		List<Map<String, Object>> rows = selectRows(sql);

		if (rows.size() == 0) {
			return new HashMap<>();
		}

		return rows.get(0);
	}

	public static List<Map<String, Object>> selectRows(SecSql sql) throws MysqlUtilException {
		List<Map<String, Object>> rows = new ArrayList<>();

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = sql.getPreparedStatement(getConnection());
			rs = stmt.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnSize = metaData.getColumnCount();

			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();

				for (int columnIndex = 0; columnIndex < columnSize; columnIndex++) {
					String columnName = metaData.getColumnName(columnIndex + 1);
					Object value = rs.getObject(columnName);

					if (value instanceof Long) {
						int numValue = (int) (long) value;
						row.put(columnName, numValue);
					} else if (value instanceof Timestamp) {
						String dateValue = value.toString();
						dateValue = dateValue.substring(0, dateValue.length() - 2);
						row.put(columnName, dateValue);
					} else {
						row.put(columnName, value);
					}
				}

				rows.add(row);
			}
		} catch (SQLException e) {
			closeConnection();
			throw new MysqlUtilException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					closeConnection();
					throw new MysqlUtilException(e);
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					closeConnection();
					throw new MysqlUtilException(e);
				}
			}
		}

		return rows;
	}

	public static int selectRowIntValue(SecSql sql) {
		Map<String, Object> row = selectRow(sql);

		for (String key : row.keySet()) {
			return (int) row.get(key);
		}

		return -1;
	}

	public static String selectRowStringValue(SecSql sql) {
		Map<String, Object> row = selectRow(sql);

		for (String key : row.keySet()) {
			return (String) row.get(key);
		}

		return "";
	}

	public static boolean selectRowBooleanValue(SecSql sql) {
		Map<String, Object> row = selectRow(sql);

		for (String key : row.keySet()) {
			return ((int) row.get(key)) == 1;
		}

		return false;
	}

	public static int insert(SecSql sql) {
		int id = -1;

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = sql.getPreparedStatement(getConnection());
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();

			if (rs.next()) {
				id = rs.getInt(1);
			}

		} catch (SQLException e) {
			closeConnection();
			throw new MysqlUtilException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					closeConnection();
					throw new MysqlUtilException(e);
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					closeConnection();
					throw new MysqlUtilException(e);
				}
			}

		}

		return id;
	}

	public static int update(SecSql sql) {
		int affectedRows = 0;

		PreparedStatement stmt = null;

		try {
			stmt = sql.getPreparedStatement(getConnection());
			affectedRows = stmt.executeUpdate();
		} catch (SQLException e) {
			closeConnection();
			throw new MysqlUtilException(e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					closeConnection();
					throw new MysqlUtilException(e);
				}
			}
		}

		return affectedRows;
	}

	public static int delete(SecSql sql) {
		return update(sql);
	}
}
