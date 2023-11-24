package com.guilherme.basicapi.api.controller;

import com.guilherme.basicapi.api.service.CertificadoService;
import com.guilherme.basicapi.model.input.CursoInput;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gerar-certificado")
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;

    @PostMapping
    public String geradorDeCertificado(@RequestBody CursoInput cursoInput) {
        this.certificadoService.gerarCertificado(cursoInput);

        return "Gerador de relatorio";
    }

    @PostMapping("/procurar")
    public void procurarCertificado(@RequestBody String cpf, HttpServletResponse response) throws IOException {

        File arquivo = this.certificadoService.procurarCertificados(cpf); // Seu método para encontrar o arquivo com o CPF

        if (arquivo.isFile()) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + arquivo.getName() + "\"");

            try (InputStream inputStream = Files.newInputStream(arquivo.toPath());
                 OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            response.setContentType("text/plain");
            response.getWriter().write("Arquivo não encontrado");
        }

    }

}
