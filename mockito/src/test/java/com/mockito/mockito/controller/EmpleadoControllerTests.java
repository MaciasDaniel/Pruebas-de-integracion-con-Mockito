package com.mockito.mockito.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockito.mockito.entities.Empleado;
import com.mockito.mockito.service.EmpleadoService;

@WebMvcTest
public class EmpleadoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGuardarEmpleado() throws Exception {
        // given
        Empleado empleado = Empleado.builder()
                .id(1L)
                .nombre("James")
                .apellido("Clark")
                .email("jamesclark@mail.com")
                .build();

        given(empleadoService.saveEmpleado(any(Empleado.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        // whe
        ResultActions response = mockMvc.perform(post("/api/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleado)));

        // then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is(empleado.getNombre())))
                .andExpect(jsonPath("$.apellido", is(empleado.getApellido())))
                .andExpect(jsonPath("$.email", is(empleado.getEmail())));
    }

    @Test
    void testListarEmpleados() throws Exception {
        // given
        List<Empleado> listaEmpleados = new ArrayList<>();
        listaEmpleados.add(Empleado.builder().nombre("Daniel").apellido("Carter").email("danycarter@mail.com").build());
        listaEmpleados.add(Empleado.builder().nombre("Kimberly").apellido("Allen").email("kimallen@mail.com").build());
        listaEmpleados.add(Empleado.builder().nombre("Ryan").apellido("Davis").email("ryandavis@mail.com").build());
        listaEmpleados.add(Empleado.builder().nombre("Fernando").apellido("Gutierrez").email("fergtz@mail.com").build());
        listaEmpleados.add(Empleado.builder().nombre("Michelle").apellido("Wilson").email("michwilson@mail.com").build());
        given(empleadoService.getAllEmpleados()).willReturn(listaEmpleados);

        // when
        ResultActions response = mockMvc.perform(get("/api/empleados"));

        // then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(listaEmpleados.size())));
    }

    @Test
    void testObtenerEmpleadoPorId() throws Exception {
        // given
        long empleadoId = 1L;
        Empleado empleado = Empleado.builder()
                .id(1L)
                .nombre("Samantha")
                .apellido("Scott")
                .email("samscott@mail.com")
                .build();
        given(empleadoService.getEmpleadoById(empleadoId)).willReturn(Optional.of(empleado));

        // when
        ResultActions response = mockMvc.perform(get("/api/empleados/{id}", empleadoId));

        // then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.nombre", is(empleado.getNombre())))
                .andExpect(jsonPath("$.apellido", is(empleado.getApellido())))
                .andExpect(jsonPath("$.email", is(empleado.getEmail())));
    }

    @Test
    void testObtenerEmpleadoNoEncontrado() throws Exception {
        // given
        long empleadoId = 1L;
        given(empleadoService.getEmpleadoById(empleadoId)).willReturn(Optional.empty());

        // when
        ResultActions response = mockMvc.perform(get("/api/empleados/{id}", empleadoId));

        // then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void testActualizarEmpleado() throws Exception {
        // given
        long empleadoId = 1L;
        Empleado empleadoGuardado = Empleado.builder()
                .nombre("Steve")
                .apellido("Adams")
                .email("steveadams@mail.com")
                .build();

        Empleado empleadoActualizado = Empleado.builder()
                .nombre("Juan")
                .apellido("Sanchez")
                .email("js@mail.com")
                .build();

        given(empleadoService.getEmpleadoById(empleadoId)).willReturn(Optional.of(empleadoGuardado));
        given(empleadoService.updateEmpleado(any(Empleado.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        // when
        ResultActions response = mockMvc.perform(put("/api/empleados/{id}", empleadoId)
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(objectMapper.writeValueAsString(empleadoActualizado)));

        // then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.nombre", is(empleadoActualizado.getNombre())))
                .andExpect(jsonPath("$.apellido", is(empleadoActualizado.getApellido())))
                .andExpect(jsonPath("$.email", is(empleadoActualizado.getEmail())));
    }

    @Test
    void testActualizarEmpleadoNoEncontrado() throws Exception {
        // given
        long empleadoId = 1L;
        Empleado empleadoActualizado = Empleado.builder()
                .nombre("Amanda")
                .apellido("Baker")
                .email("amandabaker@mail.com")
                .build();

        given(empleadoService.getEmpleadoById(empleadoId)).willReturn(Optional.empty());
        given(empleadoService.updateEmpleado(any(Empleado.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        // when
        ResultActions response = mockMvc.perform(put("/api/empleados/{id}", empleadoId)
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(objectMapper.writeValueAsString(empleadoActualizado)));

        // then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void testEliminarEmpleado() throws Exception {
        // given
        long empleadoId = 1L;
        willDoNothing().given(empleadoService).deleteEmpleado(empleadoId);

        // when
        ResultActions response = mockMvc.perform(delete("/api/empleados/{id}", empleadoId));

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }
}