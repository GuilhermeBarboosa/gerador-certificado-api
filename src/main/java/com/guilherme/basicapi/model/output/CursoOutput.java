package com.guilherme.basicapi.model.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exolab.castor.types.DateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoOutput {
    private String nomeCurso;
    private DateTime dataInicio;
    private String horas;
}
