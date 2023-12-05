package com.guilherme.basicapi.api.controller;

import com.guilherme.basicapi.api.service.ArquivoService;
import com.guilherme.basicapi.api.service.CertificadoService;
import com.guilherme.basicapi.model.input.CursoInput;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gerar-certificado")
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;

    @Autowired
    private ArquivoService arquivoService;

    @PostMapping
    public void geradorDeCertificado(@RequestParam("urlDados") MultipartFile urlDados,
                                     @RequestParam("dataInicio") String dataInicio,
                                     @RequestParam("urlImg") MultipartFile urlImg,
                                     @RequestParam("urlVerso") MultipartFile urlVerso,
                                     @RequestParam("qtdHoras") String qtdHoras,
                                     @RequestParam("nomeCurso") String nomeCurso,
                                     HttpServletResponse response) throws IOException {

        CursoInput cursoInput = new CursoInput(nomeCurso, urlDados, urlImg, urlVerso, qtdHoras);
        File arquivosGerados = this.certificadoService.gerarCertificado(cursoInput);
        arquivoService.renderArquivo(response, arquivosGerados);
    }

    @PostMapping("/procurar")
    public void procurarCertificado(@RequestBody String cpf, HttpServletResponse response) throws IOException {
        File arquivo = this.certificadoService.procurarCertificados(cpf);
        arquivoService.renderArquivo(response, arquivo);
    }


    @GetMapping("/getAll")
    public void getAll(HttpServletResponse response) throws IOException {
        arquivoService.renderArquivo(response, certificadoService.getAllCertificados());
    }
}
