package com.study.db_connection.repository.jdbc;

import static com.study.db_connection.repository.jdbc.JdbcUtils.close;
import static com.study.db_connection.repository.jdbc.JdbcUtils.getConnection;

import com.study.db_connection.entity.Address;
import com.study.db_connection.entity.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
public class JdbcMemberRepository {

    public Member save(Member member) {
        String sql = "insert into"
            + " member(name, age, city, street, zip_code, created_date, last_modified_date)"
            + " values(?, ?, ?, ?, ?, now(), now())";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);

            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, member.getName());
            pstmt.setInt(2, member.getAge());
            pstmt.setString(3, member.getAddress().getCity());
            pstmt.setString(4, member.getAddress().getStreet());
            pstmt.setString(5, member.getAddress().getZipCode());
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                long id = rs.getLong(1);
                member.setId(id);
                con.commit();
                return member;
            } else {
                throw new IllegalStateException("Not Generated Key");
            }
        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                throw new RuntimeException("Rollback fail", ex);
            }
            throw new IllegalStateException(e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    public Member findById(Long id) {
        if (id == null) {
            throw new IllegalStateException("id not nullable");
        }
        String sql = "select * from member where id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                return Member.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .age(rs.getInt("age"))
                    .address(new Address(rs.getString("city"), rs.getString("street"),
                        rs.getString("zip_code")))
                    .build();
            } else {
                throw new NoSuchElementException("Member not found(id : " + id + ")");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    public List<Member> findAll() {
        String sql = "select * from member";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            List<Member> result = new ArrayList<>();
            while (rs.next()) {
                result.add(Member.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .age(rs.getInt("age"))
                    .address(new Address(rs.getString("city"), rs.getString("street"),
                        rs.getString("zip_code")))
                    .build());
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    public void deleteAll() {
        String sql = "delete from member";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalStateException("id not nullable");
        }
        String sql = "delete from member where id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    public Member update(Member member) {
        StringBuilder sqlBuilder = new StringBuilder("update member set");
        if (member.getId() == null) {
            throw new IllegalStateException("id not nullable");
        }
        boolean flag = false;
        boolean nameFlag = StringUtils.hasText(member.getName());
        boolean ageFlag = member.getAge() >= 0;
        Address address = member.getAddress();
        boolean cityFlag = StringUtils.hasText(address.getCity());
        boolean streetFlag = StringUtils.hasText(address.getStreet());
        boolean zipCodeFlag = StringUtils.hasText(address.getZipCode());

        if (nameFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" name = ?");
            flag = true;
        }
        if (ageFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" age = ?");
            flag = true;
        }
        if (cityFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" city = ?");
            flag = true;
        }
        if (streetFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" street = ?");
            flag = true;
        }
        if (zipCodeFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" zip_code = ?");
            flag = true;
        }
        if (flag) {
            sqlBuilder.append(",");
        }
        sqlBuilder.append(" last_modified_date = now()").append(" where id = ?");
        String sql = sqlBuilder.toString();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);

            pstmt = con.prepareStatement(sql);
            int index = 1;
            if (nameFlag) {
                pstmt.setString(index++, member.getName());
            }
            if (ageFlag) {
                pstmt.setInt(index++, member.getAge());
            }
            if (cityFlag) {
                pstmt.setString(index++, address.getCity());
            }
            if (streetFlag) {
                pstmt.setString(index++, address.getStreet());
            }
            if (zipCodeFlag) {
                pstmt.setString(index++, address.getZipCode());
            }
            pstmt.setLong(index, member.getId());
            pstmt.executeUpdate();
            con.commit();
            return findById(member.getId());
        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                throw new RuntimeException("Rollback fail", ex);
            }
            throw new IllegalStateException(e);
        } finally {
            close(con, pstmt, rs);
        }
    }
}
