package com.study.db_connection.repository.jdbctemplate;

import com.study.db_connection.entity.Address;
import com.study.db_connection.entity.Member;
import com.study.db_connection.entity.Pet;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

@Repository
public class JdbcTemplateMemberRepository {

    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;
    private final TransactionTemplate txTemplate;

    public JdbcTemplateMemberRepository(DataSource dataSource,
        PlatformTransactionManager transactionManager) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("member")
            .usingGeneratedKeyColumns("id");
        this.txTemplate = new TransactionTemplate(transactionManager);
    }

    public Member save(Member member) {
        SqlParameterSource param = getParam(member);

        Number key = txTemplate.execute(status -> jdbcInsert.executeAndReturnKey(param));
        if (key == null) {
            throw new IllegalStateException("Not Generated Key");
        }
        member.setId(key.longValue());
        return member;
    }

    public Member findById(Long id) {
        if (id == null) {
            throw new IllegalStateException("id not nullable");
        }
        Map<String, Object> param = Map.of("id", id);
        String sql = "select * from pet where member_id = :id";
        List<Pet> petList = template.query(sql, param, petRowMapper());
        for (Pet pet : petList) {
            System.out.println("pet : " + pet.getId());
        }

        sql = "select * from member m where id = :id";
        try {
            Member member = template.queryForObject(sql, param, memberRowMapper());
            if (member != null) {
                member.getPets().addAll(petList);
            }
            return member;
        } catch (DataAccessException e) {
            throw new NoSuchElementException("Member not found(id : " + id + ")", e);
        }
    }

    public List<Member> findAll() {
        String sql = "select * from member";
        List<Member> userList = template.query(sql, Map.of(), memberRowMapper());
        List<Long> idList = userList.stream().map(Member::getId).toList();

        StringBuilder sqlBuilder = new StringBuilder("select * from pet where member_id in (");
        Iterator<Long> iter = idList.iterator();
        while (iter.hasNext()) {
            sqlBuilder.append(iter.next());
            if (iter.hasNext()) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(")");

        List<Pet> petList = template.query(sqlBuilder.toString(), Map.of(), petRowMapper());
        userList.forEach(m -> petList.forEach(p -> {
            if (p.getMember().getId().equals(m.getId())) {
                m.getPets().add(p);
            }
        }));
        return userList;
    }

    public void deleteAll() {
        String sql = "delete from member";
        template.update(sql, Map.of());
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalStateException("id not nullable");
        }
        String sql = "delete from member where id = :id";
        Map<String, Object> param = Map.of("id", id);
        template.update(sql, param);
    }

    public Member update(Member member) {
        if (member.getId() == null) {
            throw new IllegalStateException("id not nullable");
        }
        String sql = getUpdateSql(member);
        SqlParameterSource param = getParam(member);
        txTemplate.executeWithoutResult(status -> template.update(sql, param));
        return findById(member.getId());
    }

    private String getUpdateSql(Member member) {
        StringBuilder sqlBuilder = new StringBuilder("update member set");
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
            sqlBuilder.append(" name = :name");
            flag = true;
        }
        if (ageFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" age = :age");
            flag = true;
        }
        if (cityFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" city = :city");
            flag = true;
        }
        if (streetFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" street = :street");
            flag = true;
        }
        if (zipCodeFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" zip_code = :zip_code");
            flag = true;
        }
        if (flag) {
            sqlBuilder.append(",");
        }
        sqlBuilder.append(" last_modified_date = now()").append(" where id = :id");
        return sqlBuilder.toString();
    }

    private SqlParameterSource getParam(Member member) {
        LocalDateTime now = LocalDateTime.now();
        return new MapSqlParameterSource()
            .addValue("id", member.getId())
            .addValue("name", member.getName())
            .addValue("age", member.getAge())
            .addValue("city", member.getAddress().getCity())
            .addValue("street", member.getAddress().getStreet())
            .addValue("zip_code", member.getAddress().getZipCode())
            .addValue("created_date", now)
            .addValue("last_modified_date", now);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = Member.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .age(rs.getInt("age"))
                .address(new Address(
                    rs.getString("city"),
                    rs.getString("street"),
                    rs.getString("zip_code")))
                .build();
            member.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
            member.setLastModifiedDate(rs.getTimestamp("last_modified_date").toLocalDateTime());
            return member;
        };
    }

    private RowMapper<Pet> petRowMapper() {
        return (rs, rowNum) -> Pet.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .species(rs.getString("species"))
            .age(rs.getInt("age"))
            .member(new Member(rs.getLong("member_id"), null, 0, null))
            .build();
    }
}
