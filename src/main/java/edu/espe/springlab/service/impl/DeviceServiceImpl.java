package edu.espe.springlab.service.impl;

import edu.espe.springlab.domain.Device;
import edu.espe.springlab.dto.DeviceRequestData;
import edu.espe.springlab.dto.DeviceResponse;
import edu.espe.springlab.dto.InventoryStatsResponse;
import edu.espe.springlab.repository.DeviceRepository;
import edu.espe.springlab.service.DeviceService;
import edu.espe.springlab.web.advice.ConflictException;
import edu.espe.springlab.web.advice.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository repo;

    public DeviceServiceImpl(DeviceRepository repo) { this.repo = repo; }

    @Override
    public DeviceResponse create(DeviceRequestData request) {
        if (repo.existsBySerial(request.getSerial())) {
            throw new ConflictException("El serial ya esta registrado");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            throw new IllegalArgumentException("Stock no puede ser negativo");
        }
        Device device = new Device();
        device.setName(request.getName());
        device.setSerial(request.getSerial());
        device.setCategory(request.getCategory());
        device.setStock(request.getStock());
        device.setAvailable(true);
        device.setDeleted(false);
        return toResponse(repo.save(device));
    }

    @Override
    public DeviceResponse deactivate(Long id) {
        Device device = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Dispositivo no encontrado"));
        device.setAvailable(false);
        return toResponse(repo.save(device));
    }

    @Override
    public InventoryStatsResponse stats() {
        List<Device> active = repo.findByDeletedFalse();
        long total = active.size();
        long available = active.stream().filter(d -> Boolean.TRUE.equals(d.getAvailable())).count();
        long unavailable = total - available;
        return new InventoryStatsResponse(total, available, unavailable);
    }

    @Override
    public DeviceResponse softDelete(Long id) {
        Device device = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Dispositivo no encontrado"));
        device.setDeleted(true);
        return toResponse(repo.save(device));
    }

    @Override
    public List<DeviceResponse> searchByCategory(String category) {
        return repo.findByCategoryContainingIgnoreCaseAndDeletedFalse(category)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<DeviceResponse> getLowStock() {
        return repo.findByStockLessThanAndDeletedFalse(5)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private DeviceResponse toResponse(Device device) {
        DeviceResponse r = new DeviceResponse();
        r.setId(device.getId());
        r.setName(device.getName());
        r.setSerial(device.getSerial());
        r.setCategory(device.getCategory());
        r.setStock(device.getStock());
        r.setAvailable(device.getAvailable());
        r.setDeleted(device.getDeleted());
        return r;
    }
}