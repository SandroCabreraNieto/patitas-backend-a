package pe.edu.cibertec.patitas_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.patitas_backend.dto.LoginRequestDTO;
import pe.edu.cibertec.patitas_backend.dto.LoginResponseDTO;
import pe.edu.cibertec.patitas_backend.dto.LogoutRequestDTO;
import pe.edu.cibertec.patitas_backend.service.AutenticacionService;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;


@RestController
@RequestMapping("/autenticacion")
public class AutenticacionController {

    @Autowired
    AutenticacionService autenticacionService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {

        try {
            Thread.sleep(Duration.ofSeconds(5));
            String[] datosUsuario = autenticacionService.validarUsuario(loginRequestDTO);
            System.out.println("Resultado: " + Arrays.toString(datosUsuario));
            if (datosUsuario == null){
                return new LoginResponseDTO("01", "Error: Usuario no encontrado","", "");
            }
            return new LoginResponseDTO("00", "",datosUsuario[0], datosUsuario[1]);

        } catch (Exception e) {
            return new LoginResponseDTO("99", "Error: Ocurrio un problema","", "");

        }


    }

    //IMPLEMENTANDO REGISTRO Y CIERRE DE SESION
    @PostMapping("/logout")
    public ResponseEntity<String> cerrarSesion(@RequestBody LogoutRequestDTO logoutRequestDTO) {
        try {
            System.out.println("Cerrando sesión: " + logoutRequestDTO.tipoDocumento() + ", " + logoutRequestDTO.numeroDocumento());
            registrarCierreSesion(logoutRequestDTO);

            // formateo de fecha
            String fechaHoraFormateada = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            // mensaje de confirmacion de registro de sesion
            String mensaje = String.format("Se ha registrado tu sesión\n" +
                                            "Tipo Documento: %s,\n" +
                                            "Número Documento: %s,\n" +
                                            "FechaFechaCierreSesion: %s",
                    logoutRequestDTO.tipoDocumento(),
                    logoutRequestDTO.numeroDocumento(),
                    fechaHoraFormateada);

            return ResponseEntity.ok(mensaje);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error en el servidor al registrar el cierre de sesión");
        }
    }

    //METODO PARA REGISTRAR CIERRE DE SESION
    private void registrarCierreSesion(LogoutRequestDTO logoutRequestDTO) throws IOException {
        String archivoRegistro = "cierreSesion.txt";
        try (FileWriter writer = new FileWriter(archivoRegistro, true)) {

            String fechaHoraFormateada = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            writer.write("Tipo Documento: " + logoutRequestDTO.tipoDocumento() + ", ");
            writer.write("Número Documento: " + logoutRequestDTO.numeroDocumento() + ", ");
            writer.write("FechaCierreSesion: " + fechaHoraFormateada + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }}


