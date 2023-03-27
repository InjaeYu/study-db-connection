package com.study.db_connection.repository.jpa;

import com.study.db_connection.entity.Pet;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaPetRepository {

    private final EntityManager em;

    @Transactional
    public Pet save(Pet pet) {
        if (pet.getMember() == null) {
            throw new IllegalStateException("Member not nullable");
        }
        em.persist(pet);
        return pet;
    }

    public Pet findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id not nullable");
        }
        List<Pet> result = em.createQuery("select p from Pet p join fetch p.member", Pet.class)
            .getResultList();
        return result.size() == 0 ? null : result.get(0);
    }

    public List<Pet> findAll() {
        return em.createQuery("select p from Pet p join fetch p.member", Pet.class).getResultList();
    }

    @Transactional
    public void delete(Pet pet) {
        if (pet == null) {
            throw new IllegalArgumentException("Pet not nullable");
        }
        em.remove(pet);
    }

    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id not nullable");
        }
        em.createQuery("delete from Pet p where p.id = :id")
            .setParameter("id", id)
            .executeUpdate();
    }

    @Transactional
    public void deleteAll() {
        em.createQuery("delete from Pet")
            .executeUpdate();
    }

    @Transactional
    public Pet update(Long id, Pet updateData) {
        if (id == null) {
            throw new IllegalArgumentException("Id not nullable");
        }
        if (updateData == null) {
            throw new IllegalArgumentException("Pet not nullable");
        }
        Pet findPet = em.find(Pet.class, id);
        if (findPet == null) {
            throw new NoSuchElementException("Not found pet");
        }
        findPet.update(updateData);
        return findPet;
    }
}
