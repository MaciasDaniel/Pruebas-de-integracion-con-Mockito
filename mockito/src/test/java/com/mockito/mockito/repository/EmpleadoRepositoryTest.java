package com.mockito.mockito.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mockito.mockito.entities.Empleado;

@DataJpaTest
public class EmpleadoRepositoryTest {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    private Empleado empleado;

    @BeforeEach
    void setup() {
        empleado = Empleado.builder()
                .nombre("David")
                .apellido("Anderson")
                .email("davidanderson@mail.com")
                .build();
    }

    @DisplayName("Test para guardar un empleado")
    @Test
    void testGuardarEmpleado() {
        // given
        Empleado empleado2 = Empleado.builder()
                .nombre("Karen")
                .apellido("Garcia")
                .email("karengarcia@mail.com")
                .build();

        // when
        Empleado empleadoGuardado = empleadoRepository.save(empleado2);

        // then
        assertThat(empleadoGuardado).isNotNull();
        assertThat(empleadoGuardado.getId()).isGreaterThan(0);
    }

    @DisplayName("Test para listar a los empleados")
    @Test
    void testListarEmpleados() {
        // given
        Empleado empleado2 = Empleado.builder()
                .nombre("Sergio")
                .apellido("Rodriguez")
                .email("checordz@mail.com")
                .build();

        empleadoRepository.save(empleado);
        empleadoRepository.save(empleado2);

        // when
        List<Empleado> listaEmpleados = empleadoRepository.findAll();

        // then
        assertThat(listaEmpleados).isNotNull();
        assertThat(listaEmpleados.size()).isEqualTo(2);
    }

    @DisplayName("Test para obtener un empleado por ID")
    @Test
    void testObtenerEmpleadoPorId() {
        empleadoRepository.save(empleado);

        Empleado empleadoBD = empleadoRepository.findById(empleado.getId()).get();

        assertThat(empleadoBD).isNotNull();
    }

    @DisplayName("Test para actualizar un empleado")
    @Test
    void testActualizarEmpleado() {
        empleadoRepository.save(empleado);

        Empleado empleadoGuardado = empleadoRepository.findById(empleado.getId()).get();
        empleadoGuardado.setNombre("William");
        empleadoGuardado.setApellido("Thompson");
        empleadoGuardado.setEmail("willthompson@mail.com");
        Empleado empleadoActualizado = empleadoRepository.save(empleadoGuardado);

        assertThat(empleadoActualizado.getNombre()).isEqualTo("William");
        assertThat(empleadoActualizado.getEmail()).isEqualTo("willthompson@mail.com");
    }

    @DisplayName("Test para eliminar un emplado")
    @Test
    void testEliminarEmpleado() {
        empleadoRepository.save(empleado);

        empleadoRepository.deleteById(empleado.getId());
        Optional<Empleado> empleadoOptional = empleadoRepository.findById(empleado.getId());

        assertThat(empleadoOptional).isEmpty();
    }
}