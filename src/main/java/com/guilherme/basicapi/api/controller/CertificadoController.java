package com.guilherme.basicapi.api.controller;

import com.guilherme.basicapi.api.service.CertificadoService;
import com.guilherme.basicapi.model.input.CursoInput;
import com.guilherme.basicapi.model.output.CursoOutput;
import com.guilherme.basicapi.model.output.UserOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

}
