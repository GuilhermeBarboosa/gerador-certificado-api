package com.guilherme.basicapi.model.output;

import com.guilherme.basicapi.model.input.CursoInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Base64;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOutput {

    private String nome;
    private String cpf;

    private String nomeCurso;
    private String urlImg;
    private String urlImgVerso;

    public UserOutput(String nome,String cpf, CursoInput cursoInput) throws IOException {
        this.nome = nome;
        this.cpf = cpf;
        this.nomeCurso = cursoInput.getNomeCurso();

        try {
            byte[] bytesImg = cursoInput.getUrlImg().getBytes();
            String imgBase64 = Base64.getEncoder().encodeToString(bytesImg);
            this.urlImg = imgBase64;

            if (cursoInput.getUrlImgVerso() != null) {
                byte[] bytesImgVerso = cursoInput.getUrlImgVerso().getBytes();
                String imgBase64Verso = Base64.getEncoder().encodeToString(bytesImgVerso);
                this.urlImgVerso = imgBase64Verso;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
