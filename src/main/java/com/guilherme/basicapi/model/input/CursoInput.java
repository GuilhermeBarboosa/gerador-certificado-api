package com.guilherme.basicapi.model.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoInput {
    private String nomeCurso;
//    private String dataInicio;
//    private String horas;
    private String urlDados;
    private String urlImg;
    private String urlImgVerso;
}
