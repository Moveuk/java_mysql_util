package com.ldu.mysqlutil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SecSql {
	// StringBuilder String에 String을 메모리 할당과 메모리 해제가 발생하여 연산에 부하가 감.
	// 그래서 기존 String에 데이터를 더하는 방식으로 처리함.
	private StringBuilder sqlBuilder;
	// List 타입이며 (받을 수 있는 객체는 Object의 자손) Data(sql문 결과)를 각각 저장함.
	// sql 문 결과 조각이 다양한 타입이므로
	private List<Object> datas;

	@Override
	public String toString() {
		return "rawSql=" + getRawSql() + ", data=" + datas;
	}

	// 생성자
	public SecSql() {
		// 객체 생성 list는 ArrayList 객체 생성
		sqlBuilder = new StringBuilder();
		datas = new ArrayList<>();
	}

	public boolean isInsert() {
		// trim된 sql문이 insert로 시작하는지 물음.
		return getFormat().startsWith("INSERT");
	}

	// sql문 추가 메소드 가변인자 사용 
	// 0 sql문 1부터 와일드카드
	public SecSql append(Object... args) {
		// 내부 길이가 0보다 크면
		if (args.length > 0) {
			String sqlBit = (String) args[0];
			// sqlBuilder에 계속 추가함.
			sqlBuilder.append(sqlBit + " ");
		}

		// ArrayList 1부터 하는 이유는 0번이 sql문 1번부터 ?와일드 카드 사용하기 때문
		// 즉 변수값을 넣어줌.
		for (int i = 1; i < args.length; i++) {
			datas.add(args[i]);
		}

		return this;
	}

	public PreparedStatement getPreparedStatement(Connection connection) throws SQLException {
		PreparedStatement stmt = null;

		// insert로 시작하는 sql 문이면
		if (isInsert()) {
			// RETURN_GENERATED_KEYS
			// 생성 된 키를 검색에 사용할 수 있어야 함을 나타내는 상수입니다.
			// https://runebook.dev/ko/docs/openjdk/java.sql/java/sql/statement
			stmt = connection.prepareStatement(getFormat(), Statement.RETURN_GENERATED_KEYS);
		} else {
			// insert문 아니면 
			stmt = connection.prepareStatement(getFormat());
		}
		
		// setString setInt 자동으로 해줌.
		for (int i = 0; i < datas.size(); i++) {
			Object data = datas.get(i);
			int parameterIndex = i + 1;

			// 숫자면 숫자로 넣고 문자면 문자로 넣음
			if (data instanceof Integer) {
				stmt.setInt(parameterIndex, (int) data);
			} else if (data instanceof String) {
				stmt.setString(parameterIndex, (String) data);
			}
		}

		// 개발모드 true면 sql문 출력
		if (MysqlUtil.isDevMode()) {
			System.out.println("rawSql : " + getRawSql());
		}

		return stmt;
	}

	public String getFormat() {
		// 우리가 넣은 sql문을 string으로 바꿔서 좌우 공백 제거
		return sqlBuilder.toString().trim();
	}

	// sql문 출력과정 만약 와일드카드가 존재하고 인자들이 있으면 \\?'값'형식으로 넣어줌.
	public String getRawSql() {
		String rawSql = getFormat();

		for (int i = 0; i < datas.size(); i++) {
			Object data = datas.get(i);

			rawSql = rawSql.replaceFirst("\\?", "'" + data + "'");
		}

		return rawSql;
	}

	public static SecSql from(String sql) {
		return new SecSql().append(sql);
	}
} 