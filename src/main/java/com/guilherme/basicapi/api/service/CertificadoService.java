package com.guilherme.basicapi.api.service;

import com.guilherme.basicapi.model.input.CursoInput;
import com.guilherme.basicapi.model.output.UserOutput;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class CertificadoService {

    @Value("${filerdiretorio}")
    private String filedir;
    private String caminhoCertificado;

    @PostConstruct
    public void init() {
        filedir = filedir.replaceAll("\"", "").replaceAll(";", "");
        caminhoCertificado = filedir + "certificados/";
    }

    private void salvarCertificado(UserOutput userOutput, byte[] certificado) {
        try {
            userOutput.setNomeCurso(userOutput.getNomeCurso().replaceAll(" ", "-"));
            String caminhoCompleto = caminhoCertificado + userOutput.getNomeCurso() + "-certificado-" + userOutput.getCpf() + ".pdf";

            File file = new File(caminhoCompleto);

            // Certifique-se de que o diretório de destino existe
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // Escrever os bytes do relatório no arquivo
            try (FileOutputStream fos = new FileOutputStream(file)) {
                FileCopyUtils.copy(certificado, fos);
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Trate a exceção conforme necessário
        }
    }

    public File procurarCertificados(String cpf) {
        File diretorio = new File(caminhoCertificado);
        List<File> arquivosEncontrados = new ArrayList<>();

        cpf = cpf.replaceAll("\"", "");
        File[] arquivos = diretorio.listFiles();

        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo.isFile()) {
                    String nomeArquivo = arquivo.getName();

                    // Utilizando expressão regular para encontrar o CPF no nome do arquivo
                    Pattern pattern = Pattern.compile("\\d{11}"); // Expressão regular para CPF (11 dígitos)
                    Matcher matcher = pattern.matcher(nomeArquivo);

                    if (matcher.find()) {
                        String cpfEncontrado = matcher.group();

                        if (cpfEncontrado.equals(cpf)) {
                            arquivosEncontrados.add(arquivo);
                        }
                    }
                }
            }
        } else {
            throw new RuntimeException("Diretório não encontrado");
        }

        // Cria um arquivo .rar
        File arquivoRar = new File("certificados_encontrados.rar");
        try (OutputStream arquivoSaida = Files.newOutputStream(arquivoRar.toPath());
             ArchiveOutputStream saidaCompactada = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, arquivoSaida)) {

            for (File arquivoEncontrado : arquivosEncontrados) {
                ArchiveEntry entrada = saidaCompactada.createArchiveEntry(arquivoEncontrado, arquivoEncontrado.getName());
                saidaCompactada.putArchiveEntry(entrada);
                FileInputStream entradaArquivo = new FileInputStream(arquivoEncontrado);
                IOUtils.copy(entradaArquivo, saidaCompactada);
                entradaArquivo.close();
                saidaCompactada.closeArchiveEntry();
            }
        } catch (IOException | ArchiveException e) {
            e.printStackTrace();
        }

        return arquivoRar;
    }

    public String gerarCertificado(CursoInput cursoInput) {
        List<UserOutput> usuarios = new ArrayList<>();

        try {
            // Lê o arquivo CSV como um InputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(cursoInput.getUrlDados().getInputStream()));

            // Cria um CSVParser
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

            // Itera sobre as linhas do arquivo CSV
            for (CSVRecord csvRecord : csvParser) {
                // Para cada linha, você pode acessar os valores das colunas
                String column1 = csvRecord.get(0); // Obtém o valor da primeira coluna

                String[] values = column1.split(";");

                if (values.length >= 2) {
                    // Obtém o nome e o CPF separadamente
                    String nome = values[0].trim(); // Remove espaços em branco extras
                    String cpf = values[1].trim(); // Remove espaços em branco extras
                    // Crie seu objeto UserOutput usando nome, cpf e telefone

                    UserOutput userCreated = new UserOutput(nome, cpf, cursoInput);
                    usuarios.add(userCreated);

                } else {
                    // Lidar com linhas do CSV que não possuem as duas partes (nome e CPF separados por ';')
                    System.out.println("Formato incorreto na linha: " + column1);
                }
            }

            // Fecha o parser
            csvParser.close();

            for (UserOutput usuario : usuarios) {
                // Carregar o arquivo Jasper
                InputStream arquivoJasper = this.getClass().getResourceAsStream("/template-certificado/certificado.jasper");

                //Trocar o nome para primeira letra maiuscula
                String[] palavras = usuario.getNome().toLowerCase().split(" ");
                StringBuilder resultado = new StringBuilder();

                for (String palavra : palavras) {
                    if (!palavra.isEmpty()) {
                        String primeiraLetraMaiuscula = palavra.substring(0, 1).toUpperCase() + palavra.substring(1);
                        resultado.append(primeiraLetraMaiuscula).append(" ");
                    }
                }
                usuario.setNome(resultado.toString().trim());
                usuario.setCpf(usuario.getCpf().replaceAll("[^0-9]", ""));
                //--------------------------------------

                // Criar uma fonte de dados para o usuario atual
                List<UserOutput> usuarioList = new ArrayList<>();
                usuarioList.add(usuario);

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(usuarioList);

                // Preencher o relatório com os dados do usuario atual
                JasperPrint jasperPrint = JasperFillManager.fillReport(arquivoJasper, null, dataSource);

                // Exportar o relatório para um array de bytes (PDF)
                byte[] certificado = JasperExportManager.exportReportToPdf(jasperPrint);

                // Salvar o certificado
                salvarCertificado(usuario, certificado);

            }

            return "Arquivo CSV processado com sucesso!";

        } catch (IOException | JRException e) {
            e.printStackTrace();
            return "Erro ao processar o arquivo CSV";
        }
    }
}
