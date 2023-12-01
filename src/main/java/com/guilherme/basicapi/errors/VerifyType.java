package com.guilherme.basicapi.errors;

import com.guilherme.basicapi.model.input.CursoInput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class VerifyType {

    public String isTypeFile(CursoInput cursoInput) {

        if (!Objects.equals(cursoInput.getUrlDados().getContentType(), "text/csv")) {
           return "Tipo de dados não suportado";
        }

        if (!Objects.equals(cursoInput.getUrlImg().getContentType(), "image/png") ||
                !Objects.equals(cursoInput.getUrlImg().getContentType(), "image/jpeg") ||
                !Objects.equals(cursoInput.getUrlImg().getContentType(), "image/jpg")) {
            ResponseEntity.status(415).body("Tipo de arquivo não suportado");
            return "Tipo de imagem não suportado";
        }

        if (!Objects.equals(cursoInput.getUrlImgVerso().getContentType(), "image/png") ||
                !Objects.equals(cursoInput.getUrlImgVerso().getContentType(), "image/jpeg") ||
                !Objects.equals(cursoInput.getUrlImgVerso().getContentType(), "image/jpg")) {
            return "Tipo de imagem verso não suportado";
        }

        return null;
    }

}
