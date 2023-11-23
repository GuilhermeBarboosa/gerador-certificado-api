package com.guilherme.basicapi.model.output;

import com.guilherme.basicapi.model.input.CursoInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOutput {

    private String nome;
    private String cpf;

    private String nomeCurso;
    private String dataInicio;
    private String horas;
    private String urlImg;
    private String urlImgVerso;

    public UserOutput(String nome,String cpf, CursoInput cursoInput) {
        this.nome = nome;
        this.cpf = cpf;
        this.nomeCurso = cursoInput.getNomeCurso();
        this.dataInicio = cursoInput.getDataInicio();
        this.horas = cursoInput.getHoras();
        this.urlImg = cursoInput.getUrlImg();
        if(cursoInput.getUrlImgVerso() != null){
            this.urlImgVerso = cursoInput.getUrlImgVerso();
        }
    }

}
