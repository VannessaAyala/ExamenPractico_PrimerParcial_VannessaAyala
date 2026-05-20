package edu.espe.springlab.repository;

import edu.espe.springlab.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findBySerial(String serial);
    boolean existsBySerial(String serial);
    long countByAvailable(Boolean available);

    // Para búsqueda parcial por categoría (solo no eliminados)
    List<Device> findByCategoryContainingIgnoreCaseAndDeletedFalse(String category);

    // Para consultas normales (excluye eliminados)
    List<Device> findByDeletedFalse();

    // Para el bonus: stock bajo
    List<Device> findByStockLessThanAndDeletedFalse(Integer stock);
}