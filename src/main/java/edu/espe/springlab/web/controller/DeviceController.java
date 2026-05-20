package edu.espe.springlab.web.controller;

import edu.espe.springlab.dto.DeviceRequestData;
import edu.espe.springlab.dto.DeviceResponse;
import edu.espe.springlab.dto.InventoryStatsResponse;
import edu.espe.springlab.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vannessaayala/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    public ResponseEntity<DeviceResponse> create(@Valid @RequestBody DeviceRequestData request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceService.create(request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<DeviceResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.deactivate(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeviceResponse> softDelete(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.softDelete(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<InventoryStatsResponse> stats() {
        return ResponseEntity.ok(deviceService.stats());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DeviceResponse>> search(@RequestParam String category) {
        return ResponseEntity.ok(deviceService.searchByCategory(category));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<DeviceResponse>> lowStock() {
        return ResponseEntity.ok(deviceService.getLowStock());
    }
}