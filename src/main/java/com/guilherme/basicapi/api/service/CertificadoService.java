package com.guilherme.basicapi.api.service;

import com.guilherme.basicapi.model.input.CursoInput;
import com.guilherme.basicapi.model.output.UserOutput;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class CertificadoService {

    private String filedir = "C:/Users/guilherme.rocha/Documents/Projetos/API's/certificado-api/src/main/resources/";
    private String caminhoArquivo = filedir + "certificados/";
    private String caminhoImg = filedir + "images/";

    public String gerarCertificado(CursoInput cursoInput) {

        List<UserOutput> clientes = new ArrayList<>();
        String caminhoArquivo = cursoInput.getUrlDados();
        caminhoArquivo = caminhoArquivo.replaceAll("\"", "");

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {

            String line;
            // Ignorar a primeira linha se ela contiver cabeçalhos
            if ((line = br.readLine()) != null) {
                // Cabeçalhos encontrados, processar apenas se for necessário
                String[] headers = line.split(";");
                int nomeIndex = -1, cpfIndex = -1, telefoneIndex = -1;

                // Encontrar os índices dos cabeçalhos necessários
                for (int i = 0; i < headers.length; i++) {
                    String header = headers[i].trim().toLowerCase(); // Converta para minúsculas e remova espaços em branco
//                    System.out.println(header);
                    if (header.equals("\uFEFFnome") || header.equals("nome")) {
                        nomeIndex = i;
                    }
                    if (header.equals("cpf")) {
                        cpfIndex = i;
                    }
                    if (header.equals("telefone")) {
                        telefoneIndex = i;
                    }
                }

                // Verificar se todos os cabeçalhos foram encontrados
                if (nomeIndex != -1 && cpfIndex != -1 && telefoneIndex != -1) {
                    // Processar as linhas restantes do arquivo
                    while ((line = br.readLine()) != null) {
                        String[] campos = line.split(";");
                        if (campos.length > Math.max(nomeIndex, Math.max(cpfIndex, telefoneIndex))) {
                            String nome = campos[nomeIndex].trim();
                            String cpf = campos[cpfIndex].trim();
                            String telefone = campos[telefoneIndex].trim();

                            // Crie seu objeto UserOutput usando nome, cpf e telefone
                            UserOutput userCreated = new UserOutput(nome, cpf, telefone, cursoInput);

                            // Adicione o objeto à lista de clientes
                            clientes.add(userCreated);
                        }
                    }
                } else {
                    return "Cabeçalhos não encontrados";
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        for (UserOutput cliente : clientes) {
            try {
                // Carregar o arquivo Jasper
                InputStream arquivoJasper = this.getClass().getResourceAsStream("/template-certificado/certificado.jasper");

                //Fazendo refactor dos outputs
                cliente.setUrlImg(caminhoImg + cliente.getUrlImg());
                if(cliente.getUrlImgVerso() != null){
                    cliente.setUrlImgVerso(caminhoImg + cliente.getUrlImgVerso());
                }
                cliente.setCpf(cliente.getCpf().replaceAll("[^0-9]", ""));
                //--------------------------------------

                // Criar uma fonte de dados para o cliente atual
                List<UserOutput> clienteList = new ArrayList<>();
                clienteList.add(cliente);
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(clienteList);

                // Preencher o relatório com os dados do cliente atual
                JasperPrint jasperPrint = JasperFillManager.fillReport(arquivoJasper, null, dataSource);

                // Exportar o relatório para um array de bytes (PDF)
                byte[] certificado = JasperExportManager.exportReportToPdf(jasperPrint);


//                analyzerImage();
                // Salvar o certificado
                salvarCertificado(cliente, certificado);

            } catch (JRException e) {
                // Lidar com exceções relacionadas ao JasperReports
                e.printStackTrace();
            }
        }

        return "Gerador de certificado";
    }

    private void salvarCertificado(UserOutput userOutput, byte[] certificado) {
        try {
            userOutput.setNomeCurso(userOutput.getNomeCurso().replaceAll(" ", "-"));
            String caminhoCompleto = caminhoArquivo + userOutput.getNomeCurso() + "-Certificado-" + userOutput.getCpf() + ".pdf";

            File file = new File(caminhoCompleto);

            // Certifique-se de que o diretório de destino existe
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // Escrever os bytes do relatório no arquivo
            try (FileOutputStream fos = new FileOutputStream(file)) {
                FileCopyUtils.copy(certificado, fos);
            }

//            System.out.println("Relatório salvo com sucesso em: " + caminhoCompleto);
        } catch (IOException e) {
            e.printStackTrace();
            // Trate a exceção conforme necessário
        }
    }



//    private void analyzerImage(){
//        System.out.println("Entradnoooo");
//
//        // Carregue a imagem
//        String imagePath = caminhoImg + "frente.jpg";
//        System.out.println(imagePath);
//        Mat image = Imgcodecs.imread(imagePath);
//
//        // Converta a imagem para escala de cinza
//        Mat grayImage = new Mat();
//        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
//
//        // Aplique um limiar para segmentar a parte branca
//        Mat binaryImage = new Mat();
//        Imgproc.threshold(grayImage, binaryImage, 200, 255, Imgproc.THRESH_BINARY);
//
//        // Encontre contornos na imagem binarizada
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        // Encontre o maior contorno (a maior parte branca)
//        double maxArea = -1;
//        MatOfPoint largestContour = null;
//
//        for (MatOfPoint contour : contours) {
//            double area = Imgproc.contourArea(contour);
//            if (area > maxArea) {
//                maxArea = area;
//                largestContour = contour;
//            }
//        }
//
//        // Obtenha o retângulo delimitador do maior contorno
//        Rect boundingRect = Imgproc.boundingRect(largestContour);
//
//        // Exiba as coordenadas do retângulo delimitador
//        System.out.println("Coordenada X: " + boundingRect.x);
//        System.out.println("Coordenada Y: " + boundingRect.y);
//
//        // Exiba a largura e a altura do retângulo delimitador
//        System.out.println("Largura: " + boundingRect.width);
//        System.out.println("Altura: " + boundingRect.height);
//
//        // Desenhe o retângulo delimitador na imagem original
//        Imgproc.rectangle(image, new Point(boundingRect.x, boundingRect.y),
//                new Point(boundingRect.x + boundingRect.width, boundingRect.y + boundingRect.height),
//                new Scalar(0, 255, 0), 2);
//
//        // Salve a imagem com o retângulo delimitador desenhado
//        String outputImagePath = caminhoImg + "imagem_com_retangulo.jpg";
//        Imgcodecs.imwrite(outputImagePath, image);
//        System.out.println("Testeeee");
//    }
}
