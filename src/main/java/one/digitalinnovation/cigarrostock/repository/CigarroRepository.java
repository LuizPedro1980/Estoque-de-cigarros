package one.digitalinnovation.cigarrostock.repository;

import one.digitalinnovation.cigarrostock.entity.Cigarro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CigarroRepository extends JpaRepository<Cigarro, Long> {

    Optional<Cigarro> findByName(String name);
}
