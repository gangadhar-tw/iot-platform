package com.assignment.IoT.platform.service;

import com.assignment.IoT.platform.model.Sensor;
import com.assignment.IoT.platform.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @InjectMocks
    private SensorService sensorService;

    @Mock
    private SensorRepository sensorRepository;

    @Test
    void shouldReturnEmptyListOfSensorsWhenGetAllSensorsIsCalledButNoSensorsExist() {
        when(sensorRepository.findAll()).thenReturn(List.of());

        List<Sensor> result = sensorService.getAllSensors();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnListOfSensorsWhenGetAllSensorsIsCalled() {
        List<Sensor> sensors = getSensors();
        when(sensorRepository.findAll()).thenReturn(sensors);

        List<Sensor> result = sensorService.getAllSensors();

        assertNotNull(result);
        assertEquals(sensors.size(), result.size());
        assertTrue(result.containsAll(sensors));
        assertEquals("Sensor 1", result.get(0).getName());
    }

    @Test
    void shouldReturnSensorWhenGetSensorByIdIsCalledWithExistingId() {
        Sensor sensor = getSensor();
        when(sensorRepository.findById("1")).thenReturn(Optional.of(sensor));

        Optional<Sensor> resultSensorWithIdOne = sensorService.getSensorById("1");

        assertTrue(resultSensorWithIdOne.isPresent());
        assertEquals("Sensor 1", resultSensorWithIdOne.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenGetSensorByIdIsCalledWithNonExistingId() {
        when(sensorRepository.findById("99")).thenReturn(Optional.empty());

        Optional<Sensor> result = sensorService.getSensorById("99");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSaveSensorWhenCreateSensorIsCalled() {
        Sensor sensor = getSensor();
        when(sensorRepository.save(sensor)).thenReturn(sensor);

        Sensor savedSensor = sensorService.createSensor(sensor);

        assertNotNull(savedSensor);
        verify(sensorRepository, times(1)).save(sensor);
        assertEquals("Sensor 1", savedSensor.getName());
    }

    @Test
    void shouldReturnTrueWhenDeleteSensorIsCalledWithExistingSensorId() {
        Sensor sensor = getSensor();
        when(sensorRepository.findById("1")).thenReturn(Optional.of(sensor));

        boolean isSensorDeleted = sensorService.deleteSensor("1");

        assertTrue(isSensorDeleted);
        verify(sensorRepository, times(1)).delete(sensor);
    }

    @Test
    void shouldReturnFalseWhenDeleteSensorIsCalledWithNonExistingSensorId() {
        when(sensorRepository.findById("99")).thenReturn(Optional.empty());

        boolean isSensorDeleted = sensorService.deleteSensor("99");

        assertFalse(isSensorDeleted);
        verify(sensorRepository, never()).delete(any(Sensor.class));
    }

    @Test
    void shouldReturnUpdatedSensorWhenUpdateSensorIsCalledWithExistingSensorId() {
        Sensor existingSensor = getSensor();
        Sensor updatedSensor = getSensor();
        updatedSensor.setName("updated sensor");
        when(sensorRepository.findById("1")).thenReturn(Optional.of(existingSensor));
        when(sensorRepository.save(existingSensor)).thenReturn(updatedSensor);

        Optional<Sensor> result = sensorService.updateSensor("1", updatedSensor);

        assertNotNull(result);
        assertEquals("updated sensor", result.get().getName());
        verify(sensorRepository, times(1)).save(existingSensor);
    }

    @Test
    void shouldReturnEmptyWhenUpdateSensorIsCalledWithNonExistingSensorId() {
        Sensor updatedSensor = getSensor();
        when(sensorRepository.findById("99")).thenReturn(Optional.empty());

        Optional<Sensor> result = sensorService.updateSensor("99", updatedSensor);

        assertTrue(result.isEmpty());
        verify(sensorRepository, never()).save(any(Sensor.class));
    }

    private Sensor getSensor() {
        return Sensor.builder().id("1").name("Sensor 1").temperature(23.42).latitude(34.6548).longitude(165.3291).build();
    }

    private List<Sensor> getSensors() {
        return Arrays.asList(getSensor(), Sensor.builder().id("2").name("Sensor 2").temperature(18.5).latitude(-4.1234).longitude(56.8344).build());
    }
}