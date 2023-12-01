package com.guilherme.basicapi.model.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CursoInput {
    private String nomeCurso;
    private MultipartFile urlDados;
    private MultipartFile urlImg;
    private MultipartFile urlImgVerso;

    public CursoInput(String nomeCurso,
                      MultipartFile urlDados,
                      MultipartFile urlImg,
                      MultipartFile urlVerso,
                      String qtdHoras) {
        this.nomeCurso = nomeCurso;
        this.urlDados = urlDados;
        this.urlImg = urlImg;
        if (urlVerso.getSize() > 0) {
            this.urlImgVerso = urlVerso;
        } else {
            this.urlImgVerso = null;
        }


    }

}
