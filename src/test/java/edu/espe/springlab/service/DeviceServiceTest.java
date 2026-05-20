package edu.espe.springlab.service;

import edu.espe.springlab.domain.Device;
import edu.espe.springlab.dto.DeviceRequestData;
import edu.espe.springlab.dto.DeviceResponse;
import edu.espe.springlab.dto.InventoryStatsResponse;
import edu.espe.springlab.repository.DeviceRepository;
import edu.espe.springlab.service.impl.DeviceServiceImpl;
import edu.espe.springlab.web.advice.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(DeviceServiceImpl.class)
public class DeviceServiceTest {

    @Autowired
    private DeviceServiceImpl service;

    @Autowired
    private DeviceRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    // =====================================================================
    // PRUEBA 1 — Evitar serial duplicado
    // =====================================================================
    @Test
    @DisplayName("Prueba 1 - Vannessa Ayala: No debe permitir serial duplicado")
    void prueba1_shouldNotAllowDuplicateSerial() {
        // Registrar dispositivo con serial ABC-001
        Device existing = new Device();
        existing.setName("Laptop");
        existing.setSerial("ABC-001");
        existing.setCategory("Computadoras");
        existing.setStock(10);
        existing.setAvailable(true);
        existing.setDeleted(false);
        repository.save(existing);

        // Intentar registrar otro con el mismo serial
        DeviceRequestData req = new DeviceRequestData();
        req.setName("Otra Laptop");
        req.setSerial("ABC-001");
        req.setCategory("Computadoras");
        req.setStock(5);

        // Verificar que lanza excepción y no hay duplicados
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(ConflictException.class);

        assertThat(repository.count()).isEqualTo(1);
    }

    // =====================================================================
    // PRUEBA 2 — No permitir stock negativo
    // =====================================================================
    @Test
    @DisplayName("Prueba 2 - Vannessa Ayala: No debe permitir stock negativo")
    void prueba2_shouldNotAllowNegativeStock() {
        DeviceRequestData req = new DeviceRequestData();
        req.setName("Monitor");
        req.setSerial("MON-001");
        req.setCategory("Perifericos");
        req.setStock(-5);

        // Verificar que lanza excepción
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(Exception.class);
    }

    // =====================================================================
    // PRUEBA 3 — Desactivar dispositivo
    // =====================================================================
    @Test
    @DisplayName("Prueba 3 - Vannessa Ayala: Debe desactivar dispositivo correctamente")
    void prueba3_shouldDeactivateDevice() {
        // Crear dispositivo activo
        Device device = new Device();
        device.setName("Tablet");
        device.setSerial("TAB-001");
        device.setCategory("Moviles");
        device.setStock(3);
        device.setAvailable(true);
        device.setDeleted(false);
        Device saved = repository.save(device);

        // Desactivar
        DeviceResponse result = service.deactivate(saved.getId());

        // Verificar available = false y que conserva los datos
        assertThat(result.getAvailable()).isFalse();
        assertThat(result.getName()).isEqualTo("Tablet");
        assertThat(result.getSerial()).isEqualTo("TAB-001");
        assertThat(result.getCategory()).isEqualTo("Moviles");
    }

    // =====================================================================
    // PRUEBA 4 — Estadísticas de inventario
    // =====================================================================
    @Test
    @DisplayName("Prueba 4 - Vannessa Ayala: Debe retornar estadisticas correctas")
    void prueba4_shouldReturnCorrectStats() {
        // 2 disponibles
        Device d1 = new Device();
        d1.setName("Laptop 1"); d1.setSerial("LT-001");
        d1.setCategory("Computadoras"); d1.setStock(5);
        d1.setAvailable(true); d1.setDeleted(false);

        Device d2 = new Device();
        d2.setName("Laptop 2"); d2.setSerial("LT-002");
        d2.setCategory("Computadoras"); d2.setStock(3);
        d2.setAvailable(true); d2.setDeleted(false);

        // 1 no disponible
        Device d3 = new Device();
        d3.setName("Router"); d3.setSerial("RT-001");
        d3.setCategory("Redes"); d3.setStock(0);
        d3.setAvailable(false); d3.setDeleted(false);

        repository.saveAll(List.of(d1, d2, d3));

        // Obtener estadísticas
        InventoryStatsResponse stats = service.stats();

        assertThat(stats.getTotal()).isEqualTo(3);
        assertThat(stats.getAvailable()).isEqualTo(2);
        assertThat(stats.getUnavailable()).isEqualTo(1);
    }

    // =====================================================================
    // PRUEBA 5 — Eliminación lógica
    // =====================================================================
    @Test
    @DisplayName("Prueba 5 - Vannessa Ayala: Debe marcar como eliminado sin borrar de BD")
    void prueba5_shouldSoftDeleteDevice() {
        // Crear dispositivo
        Device device = new Device();
        device.setName("Switch");
        device.setSerial("SW-001");
        device.setCategory("Redes");
        device.setStock(2);
        device.setAvailable(true);
        device.setDeleted(false);
        Device saved = repository.save(device);

        // Eliminación lógica
        DeviceResponse result = service.softDelete(saved.getId());

        // Sigue en BD
        assertThat(repository.findById(saved.getId())).isPresent();

        // deleted = true
        assertThat(result.getDeleted()).isTrue();

        // No aparece en consultas normales
        List<Device> active = repository.findByDeletedFalse();
        assertThat(active).noneMatch(d -> d.getId().equals(saved.getId()));
    }

    // =====================================================================
    // PRUEBA 6 — Búsqueda parcial por categoría
    // =====================================================================
    @Test
    @DisplayName("Prueba 6 - Vannessa Ayala: Debe buscar correctamente por categoria parcial")
    void prueba6_shouldSearchByCategoryPartial() {
        // Registrar Laptop, Laptop Gamer, Router
        DeviceRequestData r1 = new DeviceRequestData();
        r1.setName("Laptop HP"); r1.setSerial("HP-001");
        r1.setCategory("Laptop"); r1.setStock(5);
        service.create(r1);

        DeviceRequestData r2 = new DeviceRequestData();
        r2.setName("Laptop Gamer Asus"); r2.setSerial("AS-001");
        r2.setCategory("Laptop Gamer"); r2.setStock(3);
        service.create(r2);

        DeviceRequestData r3 = new DeviceRequestData();
        r3.setName("Router TP-Link"); r3.setSerial("TP-001");
        r3.setCategory("Router"); r3.setStock(8);
        service.create(r3);

        // Buscar "lap"
        List<DeviceResponse> results = service.searchByCategory("lap");

        // Retorna Laptop y Laptop Gamer, NO Router
        assertThat(results).hasSize(2);
        assertThat(results).extracting(DeviceResponse::getCategory)
                .allMatch(cat -> cat.toLowerCase().contains("lap"));
        assertThat(results).extracting(DeviceResponse::getCategory)
                .doesNotContain("Router");
    }
}