package br.com.likwi.completablefuture.dao.repository;

import br.com.likwi.completablefuture.dao.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
