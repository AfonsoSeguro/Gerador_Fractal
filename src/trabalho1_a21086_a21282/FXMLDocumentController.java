/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1_a21086_a21282;

import Fractais.ComplexB;
import Threads.CalcularFractal;
import Video.ThreadImageSave;
import Video.ThreadVideoSave;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author afons
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Button bstart;//botão start
    @FXML
    private ScrollPane scrollCont;//ScrollPane
    @FXML
    private TextField itera;//TextField para numero de iterações
    @FXML
    private TextField width;//TextField para tamanho horinzontal real da janela onde vai ser calculado os fractais
    @FXML
    private TextField height;//TextField para tamanho verticais da janela onde vai ser calculado os fractais
    @FXML
    private TextField virtualX;//TextField para defenir a coordenada virtual X ao passar com o cursor por cima do fractal
    @FXML
    private TextField realX;//TextField para defenir a coordenada real X ao passar com o cursor por cima do fractal
    @FXML
    private TextField virtualY;//TextField para defenir a coordenada virtual Y ao passar com o cursor por cima do fractal
    @FXML
    private TextField realY;//TextField para defenir a coordenada real Y ao passar com o cursor por cima do fractal
    @FXML
    private TextField centerX;//TextField para defenir a coordenada real do centro do fractal
    @FXML
    private TextField widthL;//TextField para tamanho horinzontal virtual da janela onde vai ser calculado os fractais
    @FXML
    private TextField centerY;//TextField para defenir a coordenada imaginaria do centro do fractal
    @FXML
    private TextField heightL;//TextField para tamanho vertical virtual da janela onde vai ser calculado os fractais
    @FXML
    private Font x2;
    @FXML
    private Font x3;
    @FXML
    private TextField xMin;//TextField com o ponto minimo das coordenadas reais da imagem fractal
    @FXML
    private TextField xMax;//TextField com o ponto maximo das coordenadas reais da imagem fractal
    @FXML
    private TextField yMin;//TextField com o ponto minimo das coordenadas imaginarias da imagem fractal
    @FXML
    private TextField yMax;//TextField com o ponto maximo das coordenadas imaginarias da imagem fractal
    @FXML
    private ComboBox<String> MenuFractalFunction;//escolher Fractal
    @FXML
    private ComboBox<String> MenuConfiguration;//escolher o algoritmo para desenhar fractal(Balanciado, Paralelo, Sequencial)
    @FXML
    private ComboBox<String> MenuResolution;//escoher a resolução do fractal
    @FXML
    private ComboBox<String> MenuPalette;//escolher a cor do fractal
    @FXML
    private TextArea GeralInformation;//TextArea com todas a informações do fractal
    @FXML
    private Slider minSlider;//slider para limitar as cores do fractais
    @FXML
    private Slider maxSlider;//slider para limitar as cores do fractais
    @FXML
    private ProgressBar progress;//barra de progresso para indicar o quanto o falta para o fractal ser calculado
    @FXML
    private TextField zoom;//TextField que indica o zoom do fractal
    @FXML
    private Color x1;
    @FXML
    private TextField inicialZoom;//TextField que indica o valor inicial do zoom
    @FXML
    private TextField finalZoom;//TextField que indica o valor final do zoom
    @FXML
    private TextField xCPoint;
    @FXML
    private TextField yCPoint;
    @FXML
    private TextField balanIp;
    @FXML
    private TextField balanPort;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField numFrames;//TextField que indica o número de frames
    @FXML
    private Slider palete;
    
    private double mini;//indica qual o lado mais pequeno
        
    private CalcularFractal calc;//classe adjacente que calcula os fractais(outra Thread)
    
    private MathContext mc;
    
    private VideoPoint video;



    //começa o calculo do fractal
    @FXML
    private void startCalc(ActionEvent event) {
        this.scrollCont.setVvalue(0);
        this.scrollCont.setHvalue(0);
        this.desenharFractal();
    }
    
    //cancela o calculo do fractal
    @FXML
    private void stopCalc(ActionEvent event) {
        this.calc.cancel();
        //this.progress.setProgress(0);
    }
    
    //Ao clicar no botão "home",repoem os valores de zoom e do centro
    @FXML
    private void reset(ActionEvent event) {
        this.zoom.setText("4");
        this.centerX.setText("-0.5");
        this.centerY.setText("0");
        this.desenharFractal();
    }
    
    //função de inicialização
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //apaga os valores antes colocados e coloca valores pre defenidos nas comboBoxs
        this.MenuFractalFunction.getItems().clear();
        this.MenuFractalFunction.getItems().addAll("MandelBrot", "MandelBrot(Infinite)", "MandelBrot(FastInf)", "BurningShip", "BurningShip(Infinite)", "Dynamic Julia");
        this.MenuFractalFunction.setValue("MandelBrot(FastInf)");
        this.MenuConfiguration.getItems().clear();
        this.MenuConfiguration.getItems().addAll("Sequencial", "Parallel", "Balanced", "Balanced(GUI)");
        this.MenuConfiguration.setValue("Balanced");
        this.MenuResolution.getItems().clear();
        this.MenuResolution.getItems().addAll("Small", "HD", "FULL HD", "4K");
        this.MenuResolution.setValue("HD");
        this.MenuPalette.getItems().clear();
        this.MenuPalette.getItems().addAll("HSB", "RGB", "NME", "B&W");
        this.MenuPalette.setValue("HSB");
        this.mc = new MathContext(5);
        //liga alguns compoentes da interface(scrollPane, progressBar e textArea) a outra classe que calcula os fractais e actualiza os valores
        this.calc = new CalcularFractal();
        this.progress.progressProperty().bind(this.calc.progressProperty());
        this.scrollCont.contentProperty().bind(this.calc.valueProperty());
        this.GeralInformation.textProperty().bind(this.calc.messageProperty());
        this.errorLabel.textProperty().bind(this.calc.titleProperty());
        this.desenharFractal();
        this.video = new VideoPoint(new ComplexB(new BigDecimal(this.xCPoint.getText()), new BigDecimal(this.xCPoint.getText())), new BigDecimal(this.inicialZoom.getText()), new BigDecimal(this.finalZoom.getText()));
    }
    
    //ao passar com o rato sobre o fractal, actualiza o as cordenadas reais e imaginarias consoante a posição do rato, e
    //caso o fractal escolhido seja o de julia, carrega a imagem fractal com os pontos virtuais calculado pela posição do cursor
    @FXML
    private void actualizarCoordenadas(MouseEvent event) {
        
        double h = Double.parseDouble(this.height.getText());
        double w = Double.parseDouble(this.width.getText());
        double yY = (event.getY() + ((h - 677) * this.scrollCont.getVvalue()));
        double zoom = Double.parseDouble(this.zoom.getText());
        //caso o cursor passe o fractal, as cordenadas nao sao calculadas
        if(event.getX() <= w && yY <= h){
            this.virtualX.setText(event.getX() + "");
            this.realX.setText(Double.parseDouble(this.centerX.getText()) - (zoom * (w / this.mini)) / 2 + (zoom * event.getX()) / this.mini + "");
            this.virtualY.setText(yY + "");
            this.realY.setText(Double.parseDouble(this.centerY.getText()) - (zoom * (h / this.mini)) / 2 + (zoom * yY) / this.mini + "");
        
        
            //calculo balanceado do fractal de julia em função das cordenadas virtuais do rato
            if ("Dynamic Julia".equals(this.MenuFractalFunction.getValue())) {
                double X = event.getX() <= w ? event.getX() : w;
                double Y = yY <= h ? yY : h;
                int tamanhoH = Integer.parseInt(this.width.getText()), tamanhoV = Integer.parseInt(this.height.getText());
                this.mini = tamanhoH < tamanhoV ? tamanhoH : tamanhoV;
                double centroX = Double.parseDouble(this.centerX.getText()), centroY = Double.parseDouble(this.centerY.getText());
                //lancamento das threads para calculo balanceado
                this.calc.actualizarFractal(tamanhoH, tamanhoV, this.maxSlider.getValue(), this.minSlider.getValue(), new BigDecimal(this.centerX.getText()), new BigDecimal(this.centerY.getText()), new BigDecimal(this.zoom.getText()), this.mini, Double.parseDouble(this.itera.getText()), this.MenuFractalFunction.getValue(), this.MenuPalette.getValue(), this.MenuConfiguration.getValue(), (int)(palete.getValue() * 100) + 1);
                this.calc.setJulia(X, Y);
                this.calc.restart();
                
                //actualiza o as cordenadas reais e imaginarias
                double xm = (centroX - (zoom * tamanhoH / this.mini) / 2);
                double xM = (centroX - (zoom * tamanhoH / this.mini) / 2 + zoom * tamanhoH / this.mini);
                double ym = (centroY - (zoom * tamanhoV / this.mini) / 2);
                double yM = (centroY - (zoom * tamanhoV / this.mini) / 2 + zoom * tamanhoV / this.mini);

                this.xMin.setText(xm + "");
                this.xMax.setText(xM + "");
                this.yMin.setText(ym + "");
                this.yMax.setText(yM + "");
                this.widthL.setText((-xm + xM) + "");
                this.heightL.setText(-ym + yM + "");
            }
        }
    }
    
    //lança outra thread para calculo do fractal
    private void desenharFractal() {
        BigDecimal tamH = new BigDecimal(this.width.getText()), tamV = new BigDecimal(this.height.getText());
        int tamanhoH = tamH.intValue(), tamanhoV = tamV.intValue();
        BigDecimal centroX = new BigDecimal(this.centerX.getText()), centroY = new BigDecimal(this.centerY.getText());
        BigDecimal zoomB = new BigDecimal(this.zoom.getText());
        double zoom = zoomB.doubleValue();
        this.mini = tamanhoH < tamanhoV ? tamanhoH : tamanhoV;
        BigDecimal miniB = new BigDecimal(this.mini);
        BigDecimal dois = new BigDecimal(2);
        
        //lançamento da thread
        this.calc.actualizarFractal(tamanhoH, tamanhoV, this.maxSlider.getValue(), this.minSlider.getValue(), centroX, centroY, zoomB, this.mini, Double.parseDouble(this.itera.getText()), this.MenuFractalFunction.getValue(), this.MenuPalette.getValue(), this.MenuConfiguration.getValue(), (int)(palete.getValue() * 100) + 1);
        if(this.MenuFractalFunction.getValue().equals("Dynamic Julia"))this.calc.setJulia(0, 0);
        this.calc.restart();
        
        
        //actualiza o as cordenadas reais e imaginarias
        BigDecimal zhm = zoomB.multiply(tamH, mc).divide(miniB, mc);
        BigDecimal zvm = zoomB.multiply(tamV, mc).divide(miniB, mc);
        BigDecimal xm = (centroX.subtract(zhm.divide(dois, mc), mc));
        BigDecimal xM = ((centroX.subtract(zhm.divide(dois, mc), mc)).add(zhm, mc));
        BigDecimal ym = (centroY.subtract(zvm.divide(dois, mc), mc));
        BigDecimal yM = ((centroY.subtract(zvm.divide(dois, mc), mc)).add(zvm, mc));

        this.xMin.setText(xm.stripTrailingZeros().toString());
        this.xMax.setText(xM.stripTrailingZeros().toString());
        this.yMin.setText(ym.stripTrailingZeros().toString());
        this.yMax.setText(yM.stripTrailingZeros().toString());
        this.widthL.setText((xm.negate().add(xM, mc)).stripTrailingZeros().toString());
        this.heightL.setText((ym.negate().add(yM, mc)).stripTrailingZeros().toString());
    }
    
    //altera os valores da resolução do fractal
    @FXML
    private void changeSize(ActionEvent event) {
        String sol = this.MenuResolution.getValue();
        switch (sol) {
            case "Small":
                this.width.setText(200 + "");
                this.height.setText(200 + "");
                break;
            case "HD":
                this.width.setText(1280 + "");
                this.height.setText(720 + "");
                break;
            case "FULL HD":
                this.width.setText(1920 + "");
                this.height.setText(1080 + "");
                break;
            default:
                this.width.setText(4000 + "");
                this.height.setText(2000 + "");
                break;
        }
    }
    
    //divide o zoom a metade e recalcula o fraactal
    @FXML
    private void zooming(MouseEvent event) {
        //caso seja um fractal de julia não é permitido fazer zoom
        if ("Dynamic Julia".equals(this.MenuFractalFunction.getValue()))return;
        double h = Double.parseDouble(this.height.getText());
        double w = Double.parseDouble(this.width.getText());
        double yY = (event.getY() + ((h - 677) * this.scrollCont.getVvalue()));
        BigDecimal zoomB = new BigDecimal(this.zoom.getText());
        //caso o zoom seja feita fora da imagem fractal o mesmo nao acontece
        if(event.getX() <= w && yY <= h){
            BigDecimal dois = new BigDecimal(2);
            if(event.getButton() == MouseButton.PRIMARY)zoomB = zoomB.multiply(new BigDecimal(0.5));
            else zoomB = zoomB.multiply(dois);
            double zoom = zoomB.doubleValue();
            this.mc = new MathContext(calcMath(zoomB));
            //actualiza o centro com o ponto imaginario onde o mesmo foi clicado
            this.centerX.setText((new BigDecimal(this.centerX.getText())).subtract((zoomB.multiply((new BigDecimal(w).divide(new BigDecimal(this.mini), mc)), mc)).divide(dois, mc), mc).add((zoomB.multiply(new BigDecimal(event.getX(),mc).divide(new BigDecimal(this.mini), mc))), mc).stripTrailingZeros().toString());
            this.centerY.setText((new BigDecimal(this.centerY.getText())).subtract((zoomB.multiply((new BigDecimal(h).divide(new BigDecimal(this.mini), mc)), mc)).divide(dois, mc), mc).add((zoomB.multiply(new BigDecimal(yY,mc).divide(new BigDecimal(this.mini), mc))), mc).stripTrailingZeros().toString());

            //this.centerX.setText(Double.parseDouble(this.centerX.getText()) - ((zoom * (w / this.mini)) / 2) + ((zoom * event.getX()) / this.mini) + "");
            //this.centerY.setText(Double.parseDouble(this.centerY.getText()) - ((zoom * (h / this.mini)) / 2) + ((zoom * yY) / this.mini) + "");
            this.zoom.setText(zoomB.stripTrailingZeros().toString());
            this.desenharFractal();
        }
    }
    
    //nem todas as paletes estão disponiveis para todos os fractais, esta função limita as cores consoante a função fractal
    @FXML
    private void changeFracal(ActionEvent event) {
        //a função mandelbroth tem mais uma palete(NME) 
        String value = this.MenuPalette.getValue();
        if(this.MenuFractalFunction.getValue().equals("MandelBrot")){
            this.MenuPalette.getItems().clear();
            this.MenuPalette.getItems().addAll("HSB", "RGB", "NME", "B&W");
        }
        else{
            this.MenuPalette.getItems().clear();
            this.MenuPalette.getItems().addAll("HSB", "RGB", "B&W");
        }
        //caso a palete escolhida anteriormente fosse NME, a palete nova é HSB
        if(value.equals("NME"))this.MenuPalette.setValue("HSB");
        //se não, continua a cor antiga
        else this.MenuPalette.setValue(value);
    }
    
    private static int calcMath(BigDecimal num) {
        int casas = num.stripTrailingZeros().precision();
        return casas > 5 ? casas : 5;
    }

    @FXML
    private void makeVideo(ActionEvent event) {
        if(video.ready() && this.video.zoomFin.compareTo(this.video.zoomInit) <= 0){
        this.calc.actualizarFractal(Integer.parseInt(this.width.getText()), Integer.parseInt(this.height.getText()), this.maxSlider.getValue(), this.minSlider.getValue(), new BigDecimal(this.centerX.getText()), new BigDecimal(this.centerY.getText()), new BigDecimal(this.zoom.getText()), this.mini, Double.parseDouble(this.itera.getText()), this.MenuFractalFunction.getValue(), this.MenuPalette.getValue(), this.MenuConfiguration.getValue(), (int)(palete.getValue() * 100) + 1);
            this.calc.setVideoMaker(video, balanIp.getText(), Integer.parseInt(balanPort.getText()));
            this.calc.restart();
        }
    }

    @FXML
    private void playVideo(ActionEvent event) {
        if(video.ready()){
            this.calc.setVideoPlayer();
            this.calc.restart();
        }
    }

    @FXML
    private void SaveImage(ActionEvent event) {
        FileChooser choose = new FileChooser();
        choose.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Image", "*.png"),new FileChooser.ExtensionFilter("JPG image", "*.jpg"));
        File fich = choose.showSaveDialog(new Stage());
        if(fich == null)return;
        new ThreadImageSave((ImageView)scrollCont.getContent(), fich.getAbsolutePath()).start();
    }

    @FXML
    private void SaveVideo(ActionEvent event) {
        FileChooser choose = new FileChooser();
        choose.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP4 Video", "*.mp4"));
        File fich = choose.showSaveDialog(new Stage());
        if(fich == null || calc.getVideoMatrix() == null)return;
        new ThreadVideoSave(calc.getVideoMatrix(), fich.getAbsolutePath(), MenuPalette.getValue(), maxSlider.getValue(), minSlider.getValue(), Integer.parseInt(width.getText()), Integer.parseInt(height.getText()), Integer.parseInt(this.numFrames.getText())).start();
    }

    @FXML
    private void setVideoInit(ActionEvent event) {
        this.inicialZoom.setText(this.zoom.getText());
        this.video.setZoomInit(new BigDecimal(this.zoom.getText()));
    }

    @FXML
    private void setVideoFinal(ActionEvent event) {
        this.finalZoom.setText(this.zoom.getText());
        this.video.setZoomFin(new BigDecimal(this.zoom.getText()));
    }
    
    @FXML
    private void setVideoPoint(ActionEvent event) {
        this.xCPoint.setText(this.centerX.getText());
        this.yCPoint.setText(this.centerY.getText());
        this.video.setCenterPoint(new ComplexB(new BigDecimal(this.centerX.getText()), new BigDecimal(this.centerY.getText())));

    }
    
    @FXML
    private void actInit(KeyEvent event) {
        try{this.video.setZoomInit(new BigDecimal(this.inicialZoom.getText()));}
        catch(Exception ex){}
    }

    @FXML
    private void actFim(KeyEvent event) {
        try{this.video.setZoomFin(new BigDecimal(this.finalZoom.getText()));}
        catch(Exception ex){}
    }

    @FXML
    private void actxyV(KeyEvent event) {
        try{this.video.setCenterPoint(new ComplexB(new BigDecimal(this.xCPoint.getText()), new BigDecimal(this.yCPoint.getText())));}
        catch(Exception ex){}
    }
}

