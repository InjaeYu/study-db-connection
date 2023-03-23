package com.study.db_connection.repository.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdbcUtils {
    public static void close(Connection con, PreparedStatement pstmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.warn("ResultSet close error", e);
            }
        }

        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                log.warn("PreparedStatement close error", e);
            }
        }

        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                log.warn("Connection close error", e);
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:h2:mem:test";
        String username = "sa";
        String password = "";
        return DriverManager.getConnection(url, username, password);
    }

}
