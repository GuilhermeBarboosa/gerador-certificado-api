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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public File gerarCertificado(CursoInput cursoInput) {
        List<UserOutput> usuarios = new ArrayList<>();
        List<File> fileList = new ArrayList<>();
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(cursoInput.getUrlDados().getInputStream()));

            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

            List<CSVRecord> records = csvParser.getRecords();

            if (!records.isEmpty()) {
                records.remove(0);
            }

            for (CSVRecord record : records) {

                String column1 = record.get(0);

                String[] values = column1.split(";");

                if (values.length >= 2) {

                    String nome = values[0].trim();
                    String cpf = values[1].trim();

                    UserOutput userCreated = new UserOutput(nome, cpf, cursoInput);
                    usuarios.add(userCreated);

                } else {
                    throw new RuntimeException("Dados inválidos");
                }
            }

            csvParser.close();

            for (UserOutput usuario : usuarios) {

                InputStream arquivoJasper = this.getClass().getResourceAsStream("/template-certificado/certificado.jasper");

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

                List<UserOutput> usuarioList = new ArrayList<>();
                usuarioList.add(usuario);

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(usuarioList);

                JasperPrint jasperPrint = JasperFillManager.fillReport(arquivoJasper, null, dataSource);

                byte[] certificado = JasperExportManager.exportReportToPdf(jasperPrint);

                fileList.add(salvarCertificado(usuario, certificado));
            }

            return getArquivoRar(fileList);

        } catch (IOException | JRException e) {
            e.printStackTrace();
        }

        return null;
    }

    private File salvarCertificado(UserOutput userOutput, byte[] certificado) {
        try {
            userOutput.setNomeCurso(userOutput.getNomeCurso().replaceAll(" ", "-"));
            String caminhoCompleto = caminhoCertificado + userOutput.getNomeCurso() + "-certificado-" + userOutput.getCpf() + ".pdf";
            File file = new File(caminhoCompleto);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                FileCopyUtils.copy(certificado, fos);
            }

            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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

                    Pattern pattern = Pattern.compile("\\d{11}");
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

        return getArquivoRar(arquivosEncontrados);
    }

    private static File getArquivoRar(List<File> arquivosEncontrados) {
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

    public File getAllCertificados() {
        File diretorio = new File(caminhoCertificado);
        List<File> arquivosEncontrados = new ArrayList<>();
        File[] arquivos = diretorio.listFiles();

        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo.isFile()) {
                    arquivosEncontrados.add(arquivo);
                }
            }
        } else {
            throw new RuntimeException("Diretório não encontrado");
        }

        return getArquivoRar(arquivosEncontrados);
    }

}
