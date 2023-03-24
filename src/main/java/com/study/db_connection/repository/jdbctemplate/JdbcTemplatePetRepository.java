package com.study.db_connection.repository.jdbctemplate;

import com.study.db_connection.entity.Member;
import com.study.db_connection.entity.Pet;
import java.time.LocalDateTime;
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
public class JdbcTemplatePetRepository {

    private final JdbcTemplateMemberRepository memberRepository;
    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;
    private final TransactionTemplate txTemplate;

    public JdbcTemplatePetRepository(JdbcTemplateMemberRepository memberRepository,
        DataSource dataSource, PlatformTransactionManager transactionManager) {
        this.memberRepository = memberRepository;
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("pet")
            .usingGeneratedKeyColumns("id");
        this.txTemplate = new TransactionTemplate(transactionManager);
    }

    public Pet save(Long memberId, Pet pet) {
        Member findMember = memberRepository.findById(memberId);
        SqlParameterSource param = getParam(memberId, pet);

        Number key = txTemplate.execute(status -> jdbcInsert.executeAndReturnKey(param));
        if (key == null) {
            throw new IllegalStateException("Not Generated Key");
        } else {
            pet.setId(key.longValue());
            findMember.addPet(pet);
            return pet;
        }
    }

    public Pet findById(Long id) {
        if (id == null) {
            throw new IllegalStateException("id not nullable");
        }
        String sql = "select * from pet where id = :id";
        Map<String, Object> param = Map.of("id", id);
        try {
            return template.queryForObject(sql, param, petRowMapper());
        } catch (DataAccessException e) {
            throw new NoSuchElementException("Member not found(id : " + id + ")", e);
        }
    }

    public List<Pet> findAll() {
        String sql = "select * from pet";
        return template.query(sql, Map.of(), petRowMapper());
    }

    public void deleteAll() {
        String sql = "delete from pet";
        template.update(sql, Map.of());
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalStateException("id not nullable");
        }
        String sql = "delete from pet where id = :id";
        Map<String, Object> param = Map.of("id", id);
        template.update(sql, param);
    }

    public Pet update(Long memberId, Pet pet) {
        if (pet.getId() == null) {
            throw new IllegalStateException("id not nullable");
        }
        String sql = getUpdateSql(memberId, pet);
        MapSqlParameterSource param = getParam(memberId, pet);
        txTemplate.executeWithoutResult(status -> template.update(sql, param));
        return findById(pet.getId());
    }

    private String getUpdateSql(Long memberId, Pet pet) {
        StringBuilder sqlBuilder = new StringBuilder("update pet set");
        boolean flag = false;
        boolean nameFlag = StringUtils.hasText(pet.getName());
        boolean speciesFlag = StringUtils.hasText(pet.getSpecies());
        boolean ageFlag = pet.getAge() != null;
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
            sqlBuilder.append(" name = :name");
            flag = true;
        }
        if (speciesFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" species = :species");
            flag = true;
        }
        if (ageFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" age = :age");
            flag = true;
        }
        if (memberFlag) {
            if (flag) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(" member_id = :member_id");
            flag = true;
        }
        if (flag) {
            sqlBuilder.append(",");
        }
        sqlBuilder.append(" last_modified_date = now()").append(" where id = :id");
        return sqlBuilder.toString();
    }

    private MapSqlParameterSource getParam(Long memberId, Pet pet) {
        LocalDateTime now = LocalDateTime.now();
        return new MapSqlParameterSource()
            .addValue("id", pet.getId())
            .addValue("name", pet.getName())
            .addValue("species", pet.getSpecies())
            .addValue("age", pet.getAge())
            .addValue("member_id", memberId)
            .addValue("created_date", now)
            .addValue("last_modified_date", now);
    }

    private RowMapper<Pet> petRowMapper() {
        return (rs, rowNum) -> {
            Member findMember = memberRepository.findById(rs.getLong("member_id"));

            Pet pet = Pet.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .species(rs.getString("species"))
                .age(rs.getInt("age"))
                .member(findMember)
                .build();
            pet.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
            pet.setLastModifiedDate(rs.getTimestamp("last_modified_date").toLocalDateTime());
            return pet;
        };
    }
}
