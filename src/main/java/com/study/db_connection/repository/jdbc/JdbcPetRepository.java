package com.study.db_connection.repository.jdbc;

import static com.study.db_connection.repository.jdbc.JdbcUtils.close;
import static com.study.db_connection.repository.jdbc.JdbcUtils.getConnection;

import com.study.db_connection.entity.Member;
import com.study.db_connection.entity.Pet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcPetRepository {

    private final JdbcMemberRepository memberRepository;

    public Pet save(Long memberId, Pet pet) {
        Member findMember = memberRepository.findById(memberId);
        String sql = "insert into"
            + " pet(name, species, age, member_id, created_date, last_modified_date)"
            + " values(?, ?, ?, ?, now(), now())";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);

            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, pet.getName());
            pstmt.setString(2, pet.getSpecies());
            pstmt.setInt(3, pet.getAge());
            pstmt.setLong(4, memberId);
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                long id = rs.getLong(1);
                pet.setId(id);
                con.commit();
                findMember.addPet(pet);
                return pet;
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

    public Pet findById(Long id) {
        String sql = "select * from pet where id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member findMember = memberRepository.findById(rs.getLong("member_id"));
                return Pet.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .species(rs.getString("species"))
                    .age(rs.getInt("age"))
                    .member(findMember)
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

    public List<Pet> findAll() {
        String sql = "select * from pet";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            List<Pet> result = new ArrayList<>();
            while (rs.next()) {
                Member findMember = memberRepository.findById(rs.getLong("member_id"));
                result.add(Pet.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .species(rs.getString("species"))
                    .age(rs.getInt("age"))
                    .member(findMember)
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
        String sql = "delete from pet";

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
        String sql = "delete from pet where id = ?";

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

    public Pet update(Long memberId, Pet pet) {
        StringBuilder sqlBuilder = new StringBuilder("update pet set");
        if (pet.getId() == null) {
            throw new IllegalStateException("id not nullable");
        }
        boolean flag = false;
        boolean nameFlag = StringUtils.hasText(pet.getName());
        boolean speciesFlag = StringUtils.hasText(pet.getSpecies());
        boolean ageFlag = pet.getAge() >= 0;
        boolean memberFlag = false;
        if (memberId != null) {
            // member 존재 여부 확인
            memberRepository.findById(memberId);
            memberFlag = true;
        }

        if (nameFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" name = ?");
            flag = true;
        }
        if (speciesFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" species = ?");
            flag = true;
        }
        if (ageFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" age = ?");
            flag = true;
        }
        if (memberFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" member_id = ?");
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
                pstmt.setString(index++, pet.getName());
            }
            if (speciesFlag) {
                pstmt.setString(index++, pet.getSpecies());
            }
            if (ageFlag) {
                pstmt.setInt(index++, pet.getAge());
            }
            if (memberFlag) {
                pstmt.setLong(index++, memberId);
            }
            pstmt.setLong(index, pet.getId());
            pstmt.executeUpdate();
            con.commit();
            return findById(pet.getId());
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
