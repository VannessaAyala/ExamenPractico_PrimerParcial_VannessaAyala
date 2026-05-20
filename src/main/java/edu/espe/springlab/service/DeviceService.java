package edu.espe.springlab.service;

import edu.espe.springlab.dto.DeviceRequestData;
import edu.espe.springlab.dto.DeviceResponse;
import edu.espe.springlab.dto.InventoryStatsResponse;

import java.util.List;

public interface DeviceService {
    DeviceResponse create(DeviceRequestData request);
    DeviceResponse deactivate(Long id);
    InventoryStatsResponse stats();
    DeviceResponse softDelete(Long id);
    List<DeviceResponse> searchByCategory(String category);
    List<DeviceResponse> getLowStock();
}