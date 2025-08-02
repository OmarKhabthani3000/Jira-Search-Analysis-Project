package net.codejava.spring;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface LieuRepository extends CrudRepository<Lieu, Long> {

    List<Lieu> findByLastName(String lastName);
}
