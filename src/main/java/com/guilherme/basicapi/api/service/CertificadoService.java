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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CertificadoService {

    @Value("${filerdiretorio}")
    private String filedir;
    private String urlDados;
    private String caminhoCertificado;
    private String caminhoImg;

    @PostConstruct
    public void init() {
        filedir = filedir.replaceAll("\"", "").replaceAll(";", "");
        urlDados = filedir + "csv/";
        caminhoCertificado = filedir + "certificados/";
        caminhoImg = filedir + "images/";
    }


    public void gerarCertificado(CursoInput cursoInput) {

        List<UserOutput> usuarios = new ArrayList<>();
        String caminhoCertificado = urlDados + cursoInput.getUrlDados();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoCertificado))) {

            String line;
            // Ignorar a primeira linha se ela contiver cabeçalhos
            if ((line = br.readLine()) != null) {
                // Cabeçalhos encontrados, processar apenas se for necessário
                String[] headers = line.split(";");
                int nomeIndex = -1, cpfIndex = -1;

                // Encontrar os índices dos cabeçalhos necessários
                for (int i = 0; i < headers.length; i++) {
                    String header = headers[i].trim().toLowerCase(); // Converta para minúsculas e remova espaços em branco

                    if (header.equals("\uFEFFnome") || header.equals("nome")) {
                        nomeIndex = i;
                    }
                    if (header.equals("cpf")) {
                        cpfIndex = i;
                    }
                }

                // Verificar se todos os cabeçalhos foram encontrados
                if (nomeIndex != -1 && cpfIndex != -1) {
                    // Processar as linhas restantes do arquivo
                    while ((line = br.readLine()) != null) {
                        String[] campos = line.split(";");
                        if (campos.length > Math.max(nomeIndex, cpfIndex)) {
                            String nome = campos[nomeIndex].trim();
                            String cpf = campos[cpfIndex].trim();

                            // Crie seu objeto UserOutput usando nome, cpf e telefone
                            UserOutput userCreated = new UserOutput(nome, cpf, cursoInput);

                            // Adicione o objeto à lista de usuarios
                            usuarios.add(userCreated);
                        }
                    }
                } else {
                    throw new Exception("Cabeçalhos não encontrados");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (UserOutput usuario : usuarios) {
            try {
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

                //Fazendo refactor dos outputs
                usuario.setUrlImg(caminhoImg + usuario.getUrlImg());
                if (usuario.getUrlImgVerso() != null) {
                    usuario.setUrlImgVerso(caminhoImg + usuario.getUrlImgVerso());
                }
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

            } catch (JRException e) {
                // Lidar com exceções relacionadas ao JasperReports
                e.printStackTrace();
            }
        }

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

}
