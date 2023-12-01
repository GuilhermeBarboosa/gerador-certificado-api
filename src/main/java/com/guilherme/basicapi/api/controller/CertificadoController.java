package com.guilherme.basicapi.api.controller;

import com.guilherme.basicapi.api.service.CertificadoService;
import com.guilherme.basicapi.errors.VerifyType;
import com.guilherme.basicapi.exception.ApiException;
import com.guilherme.basicapi.model.input.CursoInput;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private VerifyType verifyType;

    @PostMapping
    public ResponseEntity<?> geradorDeCertificado(@RequestParam("urlDados") MultipartFile urlDados,
                                       @RequestParam("dataInicio") String dataInicio,
                                       @RequestParam("urlImg") MultipartFile urlImg,
                                       @RequestParam("urlVerso") MultipartFile urlVerso,
                                       @RequestParam("qtdHoras") String qtdHoras,
                                       @RequestParam("nomeCurso") String nomeCurso) {

        CursoInput cursoInput = new CursoInput( nomeCurso, urlDados, urlImg, urlVerso, qtdHoras);
//        if (this.verifyType.isTypeFile(cursoInput) != null) {
//            ApiException exception = new ApiException(this.verifyType.isTypeFile(cursoInput));
//            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(exception);
//        }

        return ResponseEntity.ok(this.certificadoService.gerarCertificado(cursoInput));
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

    @PostMapping("/upload")
    public String uploadCSV(@ModelAttribute CursoInput cursoInput) {
        System.out.println(cursoInput.toString());
//        MultipartFile file = certificado.getUrlDados();
//        System.out.println(file.getContentType());
//        // Verifique se o arquivo é do tipo text/csv
//        if (file != null && file.getContentType() != null && file.getContentType().equals("text/csv")) {
//            System.out.println("Arquivo CSV recebido com sucesso!");
//            // Aqui você pode salvar, processar ou manipular o arquivo conforme sua lógica
//            return "Arquivo CSV recebido com sucesso!";
//        } else {
//            return "Por favor, envie um arquivo CSV.";
//        }
        return "Arquivo CSV recebido com sucesso!";
    }
}
