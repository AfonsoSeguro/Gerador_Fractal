/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Threads;

import Fractais.Complex;
import Fractais.ComplexB;
import Fractais.Fractal;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import trabalho1_a21086_a21282.SocketInformationClient;
import trabalho1_a21086_a21282.VideoPoint;

/**
 *
 * @author afons
 */
//service é o equivalente ao SwingWorker mas em javaFx
public class CalcularFractal extends Service<ImageView>{
    //atributos comentados individualmente no contrutor
    int tamanhoH,tamanhoV, i = 0;
    double maxC, minC, centroX, centroY, zoom, mini, iteracoes, X, Y;
    BigDecimal centroXB, centroYB, zoomB;
    String fra, col, alg;
    Thread [] trs;
    boolean playVideo, makeVideo;
    VideoPoint videoPoints;
    ImageView [] frames;
    String movieIp;
    int moviePort;
    float palete;
    
    
    public CalcularFractal() {
        super();
    }
    
    public void actualizarFractal(int tamanhoH, int tamanhoV, double maxC, double minC, BigDecimal centroX, BigDecimal centroY, BigDecimal zoom, double mini, double iteracoes, String fra, String col, String alg, float palete) {
        this.tamanhoH = tamanhoH;//tamanho horizontal da janela
        this.tamanhoV = tamanhoV;//tamanho vertical da janela
        this.maxC = maxC;//angulo maximo para a tonalidade do fractal(Hsv)
        this.minC = minC;//angulo mininmo para a tonalidade do fractal(Hsv)
        this.centroX = centroX.doubleValue();//coordenada real do centro fractal
        this.centroY = centroY.doubleValue();//coordenada imaginaria do centro fractal
        this.zoom = zoom.doubleValue();//valor do zoom do fractal
        this.mini = mini;//lado mais pequeno da janela
        this.iteracoes = iteracoes;//numero de iterações
        this.fra = fra;//algoritmo fractal(MandelBroth,...)
        this.col = col;//palete de cores
        this.alg = alg;//algoritmo de threadas(balanciado, paralelo, sequencial)
        this.centroXB = centroX;//coordenada real do centro fractal mas convertido para  BigDecimal
        this.centroYB = centroY;//coordenada imaginaria do centro fractal mas convertido para  BigDecimal
        this.zoomB = zoom;//valor do zoom do fractal mas convertido para  BigDecimal
        this.palete = palete;
        playVideo = makeVideo = false;
    }

    public void setJulia(double X, double Y){
        this.X = X;//valor da coordenada real do ponto de julia
        this.Y = Y;//valor da coordenada imaginaria do ponto de julia
    }
    
    
    public void setVideoMaker(VideoPoint vi, String ip, int port){
        this.movieIp = ip;
        this.moviePort = port;
        playVideo = makeVideo = true;
        this.videoPoints = vi;
    }
    
    public void setVideoPlayer(){
        playVideo = true;
    }
    
    public ImageView [] getVideoMatrix(){
        return frames;
    }
    
    @Override
    public boolean cancel(){
        if(!(trs == null)){
            for (Thread tr : trs) {
                if(!(tr == null))tr.interrupt();
            }
        }
        return super.cancel();
    }
    
    @Override
    protected Task<ImageView> createTask() {
        return new Task<ImageView>() {
            
            //calcula o numero de casas decimais de num, para calcular o mathContext em função do zoom
            private int calcMath(BigDecimal num) {
                int casas = num.stripTrailingZeros().precision();
                return casas > 5 ? casas : 5;
            }
            
            private ComplexB pontoMaisNegroSeq(BigDecimal xC, BigDecimal yC, BigDecimal zs, double itera, int tamH, int tamV, MathContext mc){                
                double [] pontos = new double[tamH];
                for (int i = 0; i < tamH; i++) {
                    double color = Fractal.MandelBrothBD(xC.add(zs.multiply(new BigDecimal(i), mc), mc), yC.add(zs.multiply(new BigDecimal((tamH * i)/tamV), mc), mc), itera, mc);
                    pontos[i] = (itera - color) / itera;
                }
                int maxI = 0;
                double maxV = 0;
                for (int i = 0; i < pontos.length; i++) {
                    if(pontos[i] > maxV ){
                        maxI = i;
                        maxV = pontos[i];
                    }
                }
                return new ComplexB(xC.add(zs.multiply(new BigDecimal(maxI), mc), mc), yC.add(zs.multiply(new BigDecimal((tamH * maxI)/tamV), mc), mc));
            }
            
            private ComplexB pontoMaisNegroBal(BigDecimal xC, BigDecimal yC, BigDecimal zs, double itera, int tamH, int tamV, MathContext mc){
                int numProc = Runtime.getRuntime().availableProcessors();
                double [] pontos = new double[tamH];
                ExecutorService exe = Executors.newFixedThreadPool(numProc);
                trs = new DarkPointThread[numProc];
                AtomicInteger num = new AtomicInteger(0);
                for (Thread tr : trs) {
                    tr = new DarkPointThread(num, xC, yC, zs, itera, tamH, tamV, mc, pontos);
                    exe.execute(tr);
                }
                exe.shutdown();
                try {
                    exe.awaitTermination(1, TimeUnit.DAYS);
                } catch (InterruptedException ex) {
                    //Logger.getLogger(CalcularFractal.class.getName()).log(Level.SEVERE, null, ex);
                }
                int maxI = 0;
                double maxV = 0;
                for (int i = 0; i < pontos.length; i++) {
                    if(pontos[i] > maxV ){
                        maxI = i;
                        maxV = pontos[i];
                    }
                }
                return new ComplexB(xC.add(zs.multiply(new BigDecimal(maxI), mc), mc), yC.add(zs.multiply(new BigDecimal((tamH * maxI)/tamV), mc), mc));
            }
            
            private Complex [] serieErro(ArrayList<ComplexB> center, ArrayList<ComplexB> penter, double itera, BigDecimal zoom, MathContext mc){
                Complex AA = new Complex(0,0);
                Complex um = new Complex(1,0);
                Complex dois = new Complex(2,0);
                Complex A0 = penter.get(0).subtract(center.get(0), mc).doubleComplex();
                Complex A02 = A0.multiply(A0);
                Complex A03 = A0.multiply(A02);
                Complex AB = new Complex(0,0);
                Complex An = new Complex(1,0);
                Complex Bn = new Complex(0,0);
                Complex Cn = new Complex(0,0);
                Complex Xn2;
                int i = 0;
                while(i < 3){
                    Xn2 = dois.multiply(center.get(i).doubleComplex());
                    Complex A = Xn2.multiply(An).sum(um);
                    Complex B = Xn2.multiply(Bn).sum(An.multiply(An));
                    Complex C = Xn2.multiply(Cn).sum(dois.multiply(An).multiply(Bn));

                    An = A;
                    Bn = B;
                    Cn = C;

                    AB = (An.multiply(A0)).sum(Bn.multiply(A02)).sum(Cn.multiply(A03));

                    if((AA.re*AA.re)+(AA.im*AA.im) >= 4 && itera < i)break;
                    AA = (center.get(i).doubleComplex().multiply(AA).multiply(dois)).sum(AA.multiply(AA)).sum(A0);
                    i++;
                }
                Complex [] result = {An, Bn, Cn};
                return result;
            }

            private ImageView desenharFractalSeq(){
                if(fra.contains("FastInf") && zoomB.compareTo(new BigDecimal(1.0E-13)) < 0)return desenharFractalSeqFBD();
                //caso o fractal seja infinito, o mesmo é redirecionado
                if(fra.contains("Infinite"))return desenharFractalSeqBD();
                else if(fra.contains("FastInf") && zoomB.compareTo(new BigDecimal(1.0E-13)) < 0)return desenharFractalSeqFBD();
                
                //Imagem onde vai ser desenhado o fractal
                WritableImage canvas = new WritableImage(tamanhoH, tamanhoV);
                PixelWriter graf = canvas.getPixelWriter();
                //inicio do calculo de tempo
                long temp = System.currentTimeMillis();
                double color,x,y;
                for (int linhas = 0; linhas < tamanhoH; linhas++) {
                    for (int colunas = 0; colunas < tamanhoV; colunas++) {
                        
                        //conversão das coordenadas reais para cordenadas virtuais
                        x = centroX - (zoom * tamanhoH / mini) / 2 + zoom * linhas / mini;
                        y = centroY - (zoom * tamanhoV / mini) / 2 + zoom * colunas / mini;                    
                        
                        //verifica se o fractal converge e se sim, retorna um valor em função od ponto de convergencia
                        if ((fra).contains("MandelBrot")) {
                            if(!"NME".equals(col))color = Fractal.MandelBroth(x, y, iteracoes);
                            else color = Fractal.NormalMapEffect(x, -y, iteracoes);
                        }
                        else if ((fra).contains("BurningShip")) color = Fractal.BurningShip(x, y, iteracoes);
                        else color = Fractal.Julia(0, 0, x, y, iteracoes);
                        
                        color /= iteracoes;
                        
                        //torna o ponto de convergencia em uma cor consoante a palete escolhida
                        if("HSB".equals(col))graf.setColor(linhas, colunas, Color.hsb(360 - (color*(maxC - minC) + minC), 1.0 , color));
                        else if("RGB".equals(col))graf.setColor(linhas, colunas, Color.color(1 - (minC/180), color, 1 - (maxC/360)));
                        else graf.setColor(linhas, colunas, Color.color(color, 0 ,0));
                            
                    }
                    //sempre que uma linha é calculada a barra de progresso é actualizada
                    updateProgress(linhas, tamanhoH);
                    if(this.isCancelled())return null;
                }
                
                //imprime no textArea toda a informação relacioda ao fractal 
                imprimir(System.currentTimeMillis() - temp);
                return new ImageView(canvas);
            }
            
            private ImageView desenharFractalPar() {
                if(fra.contains("FastInf") && zoomB.compareTo(new BigDecimal(1.0E-13)) < 0)return desenharFractalParFBD();
                //Imagem onde vai ser desenhado o fractal
                WritableImage canvas = new WritableImage(tamanhoH, tamanhoV);
                PixelWriter graf = canvas.getPixelWriter();
                //progress bar que pode ser actualizada por varias threads
                ProgressReporter reporter = new ProgressReporter();
                reporter.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        updateProgress((int) evt.getNewValue(), tamanhoH * 2);
                    }
                });
                //para saber se vamos calcular com BigDecimal ou não
                boolean bd = fra.contains("Infinite");
                //inicio do calculo de tempo
                long temp = System.currentTimeMillis();
                //numero threads disponiveis
                int numProc = Runtime.getRuntime().availableProcessors() / 2;
                //case apenas só haja um thread, não vale a pena fazer paralelo, logo redireciona para o sequencial
                if(numProc == 1)return desenharFractalSeq();
                //calculo do intervalo que cada thread vai calcular consoante o o numero de cores
                int intervalo = tamanhoH / numProc;
                //inicialização de uma pool fixa de threads
                ExecutorService exe = Executors.newFixedThreadPool(numProc);
                
                if(!bd)trs = new FractalThreadLinhasPar[numProc];
                else trs = new FractalThreadLinhasPar[numProc];
                
                for (int i = 0; i < numProc; i++) {
                    //inicialização da thread especifica
                     if(!bd)trs[i] = new FractalThreadLinhasPar(tamanhoH, tamanhoV, i * intervalo,(i * intervalo) + intervalo, maxC, minC, centroX, centroY, zoom, mini, iteracoes, fra, col, graf, fra.contains("Infinite"), reporter);
                     else trs[i] = new FractalThreadLinhasPar(tamanhoH, tamanhoV, i * intervalo,(i * intervalo) + intervalo, maxC, minC, centroXB, centroYB, zoomB, mini, iteracoes, fra, col, graf, fra.contains("Infinite"),new MathContext(calcMath(centroXB)), reporter);
                     exe.execute(trs[i]);
                }
                //indica que mais nenhuma thread vai ser adicionada
                exe.shutdown();
                try {
                    //espera pelas threads lançadas até ao maximo de um dia
                    exe.awaitTermination(1, TimeUnit.DAYS);
                } catch (InterruptedException ex) {
                    //Logger.getLogger(CalcularFractal.class.getName()).log(Level.SEVERE, null, ex);
                }
                //imprime no textArea toda a informação relacioda ao fractal 
                imprimir(System.currentTimeMillis() - temp);
                return new ImageView(canvas);
            }
               
            private ImageView desenharFractalBal(){
                if(fra.contains("FastInf") && zoomB.compareTo(new BigDecimal(1.0E-13)) < 0)return desenharFractalBalFBD();
                //Imagem onde vai ser desenhado o fractal
                WritableImage canvas = new WritableImage(tamanhoH, tamanhoV);
                PixelWriter graf = canvas.getPixelWriter();
                //progress bar que pode ser actualizada por varias threads
                ProgressReporter reporter = new ProgressReporter();
                reporter.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        updateProgress((int) evt.getNewValue(), tamanhoV);
                    }
                });
                //inicio do calculo de tempo
                long temp = System.currentTimeMillis();
                //para saber se vamos calcular com BigDecimal ou não
                boolean bd = fra.contains("Infinite");
                //class que reperesente um inteiro mas preparado para ser mexido por varias threads
                AtomicInteger colunasI = new AtomicInteger(0);
                //numero threads disponiveis
                int numProc = Runtime.getRuntime().availableProcessors();
                //inicialização de uma pool fixa de threads
                ExecutorService exe = Executors.newFixedThreadPool(numProc);
                
                if(!bd)trs = new FractalThreadBal[numProc];
                else trs = new FractalThreadBalBD[numProc];
                for (int i = 0; i < numProc; i++) {
                    //inicialização da thread especifica
                    if(!bd)trs[i] = new FractalThreadBal(colunasI, tamanhoH, tamanhoV, maxC, minC, centroX, centroY, zoom, mini, iteracoes, fra, col, graf, reporter);
                    else trs[i] = new FractalThreadBalBD(colunasI, tamanhoH, tamanhoV, maxC, minC, iteracoes, centroXB, centroYB, zoomB, mini, fra, col, graf, new MathContext(calcMath(centroXB)), reporter);
                    exe.execute(trs[i]);
                }
                //indica que mais nenhuma thread vai ser adicionada
                exe.shutdown();
                try {
                    //espera pelas threads lançadas até ao maximo de um dia
                    exe.awaitTermination(1, TimeUnit.DAYS);
                } catch (InterruptedException ex) {
                    //Logger.getLogger(CalcularFractal.class.getName()).log(Level.SEVERE, null, ex);
                }
                //imprime no textArea toda a informação relacioda ao fractal 
                imprimir(System.currentTimeMillis() - temp);
                ImageView img = new ImageView(canvas);
                return img;
            }
            
            private ImageView desenharFractalBalGUI(){
                if(fra.contains("FastInf") && zoomB.compareTo(new BigDecimal(1.0E-13)) < 0)return desenharFractalBalFBDGUI();
                //Imagem onde vai ser desenhado o fractal
                double [][] colors = new double [tamanhoH][tamanhoV];
                //progress bar que pode ser actualizada por varias threads
                ProgressReporter reporter = new ProgressReporter();
                reporter.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        updateProgress((int) evt.getNewValue(), tamanhoV);
                    }
                });
                //inicio do calculo de tempo
                long temp = System.currentTimeMillis();
                //para saber se vamos calcular com BgiDecimal ou não
                boolean bd = fra.contains("Infinite");
                //class que reperesente um inteiro mas preparado para ser mexido por varias threads
                AtomicInteger colunasI = new AtomicInteger(0);
                //numero threads disponiveis
                int numProc = Runtime.getRuntime().availableProcessors();
                //inicialização de uma pool fixa de threads
                ExecutorService exe = Executors.newFixedThreadPool(numProc);
                
                if(!bd)trs = new FractalThreadBalGUI[numProc];
                else trs = new FractalThreadBalBDGUI[numProc];
                for (int i = 0; i < numProc; i++) {
                    //inicialização da thread especifica
                    if(!bd)trs[i] = new FractalThreadBalGUI(colunasI, tamanhoH, tamanhoV, maxC, minC, centroX, centroY, zoom, mini, iteracoes, fra, col, colors, reporter);
                    else trs[i] = new FractalThreadBalBDGUI(colunasI, tamanhoH, tamanhoV, maxC, minC, iteracoes, centroXB, centroYB, zoomB, mini, fra, col, colors, new MathContext(calcMath(centroXB)), reporter);
                    exe.execute(trs[i]);
                }
                //indica que mais nenhuma thread vai ser adicionada
                exe.shutdown();
                
                while(colunasI.get() < tamanhoV){
                    double [][]copy;
                    synchronized(colors){
                        copy = colors.clone();
                    }
                    WritableImage canvas = new WritableImage(tamanhoH, tamanhoV);
                    PixelWriter graf = canvas.getPixelWriter();
                    for (int j = 0; j < tamanhoV; j++) {
                        for (int i = 0; i < tamanhoH; i++) {
                            if("HSB".equals(col))graf.setColor(i, j, Color.hsb(360 - (copy[i][j]*(maxC - minC) + minC), 1.0 , copy[i][j]));
                            else if("RGB".equals(col))graf.setColor(i, j, Color.color(1 - (minC/180), copy[i][j], 1 - (maxC/360)));
                            else graf.setColor(i, j, Color.gray(copy[i][j]));
                        }
                    }
                    if(this.isCancelled())return new ImageView(canvas);
                    updateValue(new ImageView(canvas));
                    
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException ex) {
                        //Logger.getLogger(CalcularFractal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                WritableImage canvas = new WritableImage(tamanhoH, tamanhoV);
                PixelWriter graf = canvas.getPixelWriter();
                for (int j = 0; j < tamanhoV; j++) {
                    for (int i = 0; i < tamanhoH; i++) {
                        if("HSB".equals(col))graf.setColor(i, j, Color.hsb(360 - (colors[i][j]*(maxC - minC) + minC), 1.0 , colors[i][j]));
                        else if("RGB".equals(col))graf.setColor(i, j, Color.color(1 - (minC/180), colors[i][j], 1 - (maxC/360)));
                        else graf.setColor(i, j, Color.gray(colors[i][j]));
                    }
                }
                //imprime no textArea toda a informação relacioda ao fractal 
                imprimir(System.currentTimeMillis() - temp);
                return new ImageView(canvas);
            }
            
            private ImageView desenharFractalSeqBD(){
                //Imagem onde vai ser desenhado o fractal
                WritableImage canvas = new WritableImage(tamanhoH, tamanhoV);
                PixelWriter graf = canvas.getPixelWriter();
                //Inicialização e conversão dos atributos usados para BigDecimal
                BigDecimal x, y, tamH = new BigDecimal(tamanhoH), tamV = new BigDecimal(tamanhoV), dois = new BigDecimal(2),miniB = new BigDecimal(mini);
                MathContext mc = new MathContext(calcMath(centroXB));
                //inicio do calculo de tempo
                long temp = System.currentTimeMillis();
                double color;
                BigDecimal xc = centroXB.subtract(((zoomB.multiply(tamH, mc)).divide(new BigDecimal(mini), mc)).divide(dois, mc), mc);
                BigDecimal yc = centroYB.subtract(((zoomB.multiply(tamV, mc)).divide(new BigDecimal(mini), mc)).divide(dois, mc), mc);
                BigDecimal zomi = zoomB.divide(new BigDecimal(mini), mc);
                for (int linhas = 0; linhas < tamanhoH; linhas++) {
                    for (int colunas = 0; colunas < tamanhoV; colunas++) {
                        
                        //conversão das coordenadas reais para cordenadas virtuais
                        x = xc.add(zomi.multiply(new BigDecimal(linhas), mc));
                        y = yc.add(zomi.multiply(new BigDecimal(colunas), mc));   
                        
                        //verifica se o fractal converge e se sim, retorna um valor em função od ponto de convergencia
                        if ("MandelBrot(Infinite)".equals(fra)) color = Fractal.MandelBrothBD(x, y, iteracoes, mc);
                        else color = Fractal.BurningShipBD(x, y, iteracoes);

                        color /= (double)iteracoes;
                        
                        //torna o ponto de convergencia em uma cor consoante a palete escolhida e pinta-a
                        if("HSB".equals(col))graf.setColor(linhas, colunas, Color.hsb(360 - (color*(maxC - minC) + minC), 1.0 , color));
                        else if("RGB".equals(col))graf.setColor(linhas, colunas, Color.color(1 - (minC/180), color, 1 - (maxC/360)));
                        else graf.setColor(linhas, colunas, Color.gray(color));
                    }
                    //sempre que uma linha é calculada a barra de progresso é actualizada
                    updateProgress(linhas, tamanhoH - 1);
                    if(this.isCancelled())return null;
                }
                //imprime no textArea toda a informação relacioda ao fractal 
                imprimir(System.currentTimeMillis() - temp);
                return new ImageView(canvas);
            }        
            
            private ImageView desenharFractalSeqFBD(){
                WritableImage canvas = new WritableImage(tamanhoH, tamanhoV);
                PixelWriter graf = canvas.getPixelWriter();
                
                long temp = System.currentTimeMillis();
                BigDecimal x, y, tamH = new BigDecimal(tamanhoH), tamV = new BigDecimal(tamanhoV), dois = new BigDecimal(2),miniB = new BigDecimal(mini);
                MathContext mc = new MathContext(calcMath(centroXB));

                BigDecimal xC = centroXB.subtract(((zoomB.multiply(tamH, mc)).divide(new BigDecimal(mini), mc)).divide(dois, mc), mc);
                BigDecimal yC = centroYB.subtract(((zoomB.multiply(tamV, mc)).divide(new BigDecimal(mini), mc)).divide(dois, mc), mc);
                BigDecimal zs = zoomB.divide(new BigDecimal(mini), mc);

                long tiempo = System.currentTimeMillis();
                ComplexB cp = pontoMaisNegroSeq(xC, yC, zs, iteracoes, tamanhoH, tamanhoV, mc);//, 1,1.5 * ((double)itera - MandelBrothP.MandelBrothBD(xC.add(zs.multiply(new BigDecimal(size/2), mc), mc), yC.add(zs.multiply(new BigDecimal(size/2), mc), mc), (double)itera, mc))/(double)itera);
                System.out.println(System.currentTimeMillis() - tiempo);
                //-1.1876116653582036 -0.3016687738833415
                //ComplexB cp = new ComplexB(new BigDecimal(-0.6834926996410432), new BigDecimal(-0.32792535928503335));
                ComplexB pp = new ComplexB(xC.add(zs.multiply(new BigDecimal(tamanhoH/2), mc), mc), yC.add(zs.multiply(new BigDecimal(tamanhoV/2), mc), mc));
                //ArrayList<ComplexB> center = MandelBrothP.centralPoint(cp.re, cp.im, itera, mc);
                ArrayList<ComplexB> center = Fractal.centralPoint(cp.re, cp.im, iteracoes, mc);
                ArrayList<ComplexB> penter = Fractal.centralPoint(pp.re, pp.im, iteracoes, mc);
                
                Complex [] ABC = serieErro(center, penter, iteracoes, zoomB, mc);

                Complex [] centroDouble = new Complex[center.size()];
                for (int i = 0; i < centroDouble.length; i++)centroDouble[i] = center.get(i).doubleComplex();

                BigDecimal cX = center.get(0).re;
                BigDecimal cY = center.get(0).im;
                
                for (int linhas = 0; linhas < tamanhoH; linhas++) {
                    for (int colunas = 0; colunas < tamanhoV; colunas++) {
                        x = xC.add(zs.multiply(new BigDecimal(linhas), mc), mc);
                        y = yC.add(zs.multiply(new BigDecimal(colunas), mc), mc);

                        //calcular a cor
                        double color = Fractal.ColorPertubationAproximation(centroDouble,x.subtract(cX, mc).doubleValue(), y.subtract(cY, mc).doubleValue(), ABC, (int)iteracoes, mc)/iteracoes;
                        
                        //torna o ponto de convergencia em uma cor consoante a palete escolhida e pinta-a
                        if("HSB".equals(col))graf.setColor(linhas, colunas, Color.hsb(360 - (color*(maxC - minC) + minC), 1.0 , color));
                        else if("RGB".equals(col))graf.setColor(linhas, colunas, Color.color(1 - (minC/180), color, 1 - (maxC/360)));
                        else graf.setColor(linhas, colunas, Color.gray(color));
                        //pintar o pixel
                    }
                    //sempre que uma linha é calculada a barra de progresso é actualizada
                    updateProgress(linhas, tamanhoH - 1);
                    if(this.isCancelled())return null;
                }
                //imprime no textArea toda a informação relacioda ao fractal 
                imprimir(System.currentTimeMillis() - temp);
                return new ImageView(canvas);
            }
            
            private ImageView desenharFractalParFBD() {
                
                //Imagem onde vai ser desenhado o fractal
                WritableImage canvas = new WritableImage(tamanhoH, tamanhoV);
                PixelWriter graf = canvas.getPixelWriter();
                //progress bar que pode ser actualizada por varias threads
                ProgressReporter reporter = new ProgressReporter();
                reporter.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        updateProgress((int) evt.getNewValue(), tamanhoH * 2);
                    }
                });
                
                BigDecimal x, y, tamH = new BigDecimal(tamanhoH), tamV = new BigDecimal(tamanhoV), dois = new BigDecimal(2),miniB = new BigDecimal(mini);
                MathContext mc = new MathContext(calcMath(centroXB));

                BigDecimal xC = centroXB.subtract(((zoomB.multiply(tamH, mc)).divide(new BigDecimal(mini), mc)).divide(dois, mc), mc);
                BigDecimal yC = centroYB.subtract(((zoomB.multiply(tamV, mc)).divide(new BigDecimal(mini), mc)).divide(dois, mc), mc);
                BigDecimal zs = zoomB.divide(new BigDecimal(mini), mc);

                ComplexB cp = pontoMaisNegroBal(xC, yC, zs, iteracoes, tamanhoH, tamanhoV, mc);//, 1,1.5 * ((double)itera - MandelBrothP.MandelBrothBD(xC.add(zs.multiply(new BigDecimal(size/2), mc), mc), yC.add(zs.multiply(new BigDecimal(size/2), mc), mc), (double)itera, mc))/(double)itera);
                //-1.1876116653582036 -0.3016687738833415
                //ComplexB cp = new ComplexB(new BigDecimal(-0.6834926996410432), new BigDecimal(-0.32792535928503335));
                ComplexB pp = new ComplexB(xC.add(zs.multiply(new BigDecimal(tamanhoH/2), mc), mc), yC.add(zs.multiply(new BigDecimal(tamanhoV/2), mc), mc));
                //ArrayList<ComplexB> center = MandelBrothP.centralPoint(cp.re, cp.im, itera, mc);
                ArrayList<ComplexB> center = Fractal.centralPoint(cp.re, cp.im, iteracoes, mc);
                ArrayList<ComplexB> penter = Fractal.centralPoint(pp.re, pp.im, iteracoes, mc);
                
                Complex [] ABC = serieErro(center, penter, iteracoes, zoomB, mc);

                Complex [] centroDouble = new Complex[center.size()];
                for (int i = 0; i < centroDouble.length; i++)centroDouble[i] = center.get(i).doubleComplex();

                BigDecimal cX = center.get(0).re;
                BigDecimal cY = center.get(0).im;
                
                
                //para saber se vamos calcular com BigDecimal ou não
                boolean bd = fra.contains("Infinite");
                //inicio do calculo de tempo
                long temp = System.currentTimeMillis();
                //numero threads disponiveis
                int numProc = Runtime.getRuntime().availableProcessors() / 2;
                //case apenas só haja um thread, não vale a pena fazer paralelo, logo redireciona para o sequencial
                if(numProc == 1)return desenharFractalSeqFBD();
                //calculo do intervalo que cada thread vai calcular consoante o o numero de cores
                int intervalo = tamanhoH / numProc;
                //inicialização de uma pool fixa de threads
                ExecutorService exe = Executors.newFixedThreadPool(numProc);
                
                trs = new FractalThreadParBDF[numProc];
                
                for (int i = 0; i < numProc; i++) {
                    //inicialização da thread especifica
                     trs[i] = new FractalThreadParBDF(i * intervalo, (i * intervalo) + intervalo, centroDouble, cX, cY, xC, yC, zs, ABC, tamanhoH, tamanhoV,  maxC, minC, iteracoes, col, graf, mc, reporter);
                     exe.execute(trs[i]);
                }
                //indica que mais nenhuma thread vai ser adicionada
                exe.shutdown();
                try {
                    //espera pelas threads lançadas até ao maximo de um dia
                    exe.awaitTermination(1, TimeUnit.DAYS);
                } catch (InterruptedException ex) {
                    //Logger.getLogger(CalcularFractal.class.getName()).log(Level.SEVERE, null, ex);
                }
                //imprime no textArea toda a informação relacioda ao fractal 
                imprimir(System.currentTimeMillis() - temp);
                return new ImageView(canvas);
            }
            
            
            private ImageView desenharFractalBalFBD(){
                WritableImage canvas = new WritableImage(tamanhoH, tamanhoV);
                PixelWriter graf = canvas.getPixelWriter();
                
                //progress bar que pode ser actualizada por varias threads
                ProgressReporter reporter = new ProgressReporter();
                reporter.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        updateProgress((int) evt.getNewValue(), tamanhoV);
                    }
                });
                
                long temp = System.currentTimeMillis();
                BigDecimal x, y, tamH = new BigDecimal(tamanhoH), tamV = new BigDecimal(tamanhoV), dois = new BigDecimal(2),miniB = new BigDecimal(mini);
                MathContext mc = new MathContext(calcMath(centroXB));

                BigDecimal xC = centroXB.subtract(((zoomB.multiply(tamH, mc)).divide(new BigDecimal(mini), mc)).divide(dois, mc), mc);
                BigDecimal yC = centroYB.subtract(((zoomB.multiply(tamV, mc)).divide(new BigDecimal(mini), mc)).divide(dois, mc), mc);
                BigDecimal zs = zoomB.divide(new BigDecimal(mini), mc);

                ComplexB cp = pontoMaisNegroBal(xC, yC, zs, iteracoes, tamanhoH, tamanhoV, mc);//, 1,1.5 * ((double)itera - MandelBrothP.MandelBrothBD(xC.add(zs.multiply(new BigDecimal(size/2), mc), mc), yC.add(zs.multiply(new BigDecimal(size/2), mc), mc), (double)itera, mc))/(double)itera);
                //-1.1876116653582036 -0.3016687738833415
                //ComplexB cp = new ComplexB(new BigDecimal(-0.6834926996410432), new BigDecimal(-0.32792535928503335));
                ComplexB pp = new ComplexB(xC.add(zs.multiply(new BigDecimal(tamanhoH/2), mc), mc), yC.add(zs.multiply(new BigDecimal(tamanhoV/2), mc), mc));
                //ArrayList<ComplexB> center = MandelBrothP.centralPoint(cp.re, cp.im, itera, mc);
                ArrayList<ComplexB> center = Fractal.centralPoint(cp.re, cp.im, iteracoes, mc);
                ArrayList<ComplexB> penter = Fractal.centralPoint(pp.re, pp.im, iteracoes, mc);
                
                Complex [] ABC = serieErro(center, penter, iteracoes, zoomB, mc);

                Complex [] centroDouble = new Complex[center.size()];
                for (int i = 0; i < centroDouble.length; i++)centroDouble[i] = center.get(i).doubleComplex();

                BigDecimal cX = center.get(0).re;
                BigDecimal cY = center.get(0).im;
                
                AtomicInteger colunasI = new AtomicInteger(0);
                int numProc = Runtime.getRuntime().availableProcessors();
                ExecutorService exe = Executors.newFixedThreadPool(numProc);
                trs = new FractalThreadBalBDF[numProc];
                
                for (Thread tr : trs) {
                    tr = new FractalThreadBalBDF(colunasI, tamanhoH, tamanhoV, maxC, minC, iteracoes, col, graf, mc, centroDouble, cX, cY, xC, yC, zs, ABC, reporter);
                    exe.execute(tr);
                }
                exe.shutdown();
                try {
                    exe.awaitTermination(1, TimeUnit.DAYS);
                } catch (InterruptedException ex) {
                    //Logger.getLogger(CalcularFractal.class.getName()).log(Level.SEVERE, null, ex);
                }
                //imprime no textArea toda a informação relacioda ao fractal 
                imprimir(System.currentTimeMillis() - temp);
                return new ImageView(canvas);
            }
            
            private ImageView desenharFractalBalFBDGUI(){
                double [][] colors = new double [tamanhoH][tamanhoV];
                
                //progress bar que pode ser actualizada por varias threads
                ProgressReporter reporter = new ProgressReporter();
                reporter.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        updateProgress((int) evt.getNewValue(), tamanhoV);
                    }
                });
                
                
                long temp = System.currentTimeMillis();
                BigDecimal x, y, tamH = new BigDecimal(tamanhoH), tamV = new BigDecimal(tamanhoV), dois = new BigDecimal(2),miniB = new BigDecimal(mini);
                MathContext mc = new MathContext(calcMath(centroXB));

                BigDecimal xC = centroXB.subtract(((zoomB.multiply(tamH, mc)).divide(new BigDecimal(mini), mc)).divide(dois, mc), mc);
                BigDecimal yC = centroYB.subtract(((zoomB.multiply(tamV, mc)).divide(new BigDecimal(mini), mc)).divide(dois, mc), mc);
                BigDecimal zs = zoomB.divide(new BigDecimal(mini), mc);

                ComplexB cp = pontoMaisNegroBal(xC, yC, zs, iteracoes, tamanhoH, tamanhoV, mc);//, 1,1.5 * ((double)itera - MandelBrothP.MandelBrothBD(xC.add(zs.multiply(new BigDecimal(size/2), mc), mc), yC.add(zs.multiply(new BigDecimal(size/2), mc), mc), (double)itera, mc))/(double)itera);
                //-1.1876116653582036 -0.3016687738833415
                //ComplexB cp = new ComplexB(new BigDecimal(-0.6834926996410432), new BigDecimal(-0.32792535928503335));
                ComplexB pp = new ComplexB(xC.add(zs.multiply(new BigDecimal(tamanhoH/2), mc), mc), yC.add(zs.multiply(new BigDecimal(tamanhoV/2), mc), mc));
                //ArrayList<ComplexB> center = MandelBrothP.centralPoint(cp.re, cp.im, itera, mc);
                ArrayList<ComplexB> center = Fractal.centralPoint(cp.re, cp.im, iteracoes, mc);
                ArrayList<ComplexB> penter = Fractal.centralPoint(pp.re, pp.im, iteracoes, mc);
                
                Complex [] ABC = serieErro(center, penter, iteracoes, zoomB, mc);

                Complex [] centroDouble = new Complex[center.size()];
                for (int i = 0; i < centroDouble.length; i++)centroDouble[i] = center.get(i).doubleComplex();

                BigDecimal cX = center.get(0).re;
                BigDecimal cY = center.get(0).im;
                
                AtomicInteger colunasI = new AtomicInteger(0);
                int numProc = Runtime.getRuntime().availableProcessors();
                ExecutorService exe = Executors.newFixedThreadPool(numProc);
                trs = new FractalThreadBalBDF[numProc];
                
                for (Thread tr : trs) {
                    tr = new FractalThreadBalBDFGUI(colunasI, tamanhoH, tamanhoV, maxC, minC, iteracoes, col, colors, mc, centroDouble, cX, cY, xC, yC, zs, ABC, reporter);
                    exe.execute(tr);
                }
                exe.shutdown();
                
                while(colunasI.get() < tamanhoV){
                    double [][]copy;
                    synchronized(colors){
                        copy = colors.clone();
                    }
                    WritableImage canvas = new WritableImage(tamanhoH, tamanhoV);
                    PixelWriter graf = canvas.getPixelWriter();
                    for (int j = 0; j < tamanhoV; j++) {
                        for (int i = 0; i < tamanhoH; i++) {
                            if("HSB".equals(col))graf.setColor(i, j, Color.hsb(360 - (copy[i][j]*(maxC - minC) + minC), 1.0 , copy[i][j]));
                            else if("RGB".equals(col))graf.setColor(i, j, Color.color(1 - (minC/180), copy[i][j], 1 - (maxC/360)));
                            else graf.setColor(i, j, Color.gray(copy[i][j]));
                        }
                    }
                    if(this.isCancelled())return new ImageView(canvas);
                    updateValue(new ImageView(canvas));
                    
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException ex) {
                        //Logger.getLogger(CalcularFractal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                WritableImage canvas = new WritableImage(tamanhoH, tamanhoV);
                PixelWriter graf = canvas.getPixelWriter();
                for (int j = 0; j < tamanhoV; j++) {
                    for (int i = 0; i < tamanhoH; i++) {
                        if("HSB".equals(col))graf.setColor(i, j, Color.hsb(360 - (colors[i][j]*(maxC - minC) + minC), 1.0 , colors[i][j]));
                        else if("RGB".equals(col))graf.setColor(i, j, Color.color(1 - (minC/180), colors[i][j], 1 - (maxC/360)));
                        else graf.setColor(i, j, Color.gray(colors[i][j]));
                    }
                }
                //imprime no textArea toda a informação relacioda ao fractal 
                imprimir(System.currentTimeMillis() - temp);
                return new ImageView(canvas);
            }
            
            
            private ImageView desenharFractalJulia(){
                //Imagem onde vai ser desenhado o fractal
                WritableImage canvas = new WritableImage(tamanhoH, tamanhoV);
                PixelWriter graf = canvas.getPixelWriter();
                //inicio do calculo de tempo
                long temp = System.currentTimeMillis();
                //class que reperesente um inteiro mas preparado para ser mexido por varias threads
                AtomicInteger linhasI = new AtomicInteger(0);
                //numero de cores disponiveis
                int numProc = Runtime.getRuntime().availableProcessors();
                //inicialização de uma pool fixa de threads
                ExecutorService exe = Executors.newFixedThreadPool(numProc);
                for (int i = 0; i < numProc; i++) {
                    //inicialização da thread especifica
                    exe.execute(new FractalThreadJuliaBal(linhasI, tamanhoH, tamanhoV, maxC, minC, centroX, centroY, zoom, mini, iteracoes, X, Y, fra, col, graf));
                }
                //indica que mais nenhuma thread vai ser adicionada
                exe.shutdown();
                try {
                    //espera pelas threads lançadas até ao maximo de um dia
                    exe.awaitTermination(1, TimeUnit.DAYS);
                } catch (InterruptedException ex) {
                    //Logger.getLogger(CalcularFractal.class.getName()).log(Level.SEVERE, null, ex);
                }
                //imprime no textArea toda a informação relacioda ao fractal 
                imprimir(System.currentTimeMillis() - temp);
                return new ImageView(canvas);
                
            }
            
            //retorna para o textArea toda a informação referente ao fractal
            private void imprimir(double tempo) {
                
                double xm = (centroX - (zoom * tamanhoH / mini) / 2);
                double xM = (centroX - (zoom * tamanhoH / mini) / 2 + zoom * tamanhoH / mini);
                double ym = (centroY - (zoom * tamanhoV / mini) / 2);
                double yM = (centroY - (zoom * tamanhoV / mini) / 2 + zoom * tamanhoV / mini);
                
                String imp = "";
                imp += "Fractal: " + fra + "\n";
                imp += "Max Itera.: " + iteracoes + "\n";
                imp += "\n";
                imp += "Algorithm: " + alg + "\n";
                imp += "Calculus Time(seconds): " + (tempo / 1000) + "\n";
                imp += "\n";
                imp += "Fractal Zoom: " + zoom + "\n";
                imp += "\n";
                imp += "Centro X: " + centroX + "\n";
                imp += "Centro Y: " + centroY + "\n";
                imp += "Width: " + (-xm + xM) + "\n";
                imp += "Heigth: " + (-ym + yM) + "\n";
                imp += "\n";
                imp += "X Minimum: " + xm + "\n";
                imp += "Y Minimum: " + ym + "\n";
                imp += "X Maximum: " + xM + "\n";
                imp += "Y Maximum: " + yM + "\n";
                imp += "\n";
                imp += "Color Palete: " + col + "\n";
                imp += "\n";
                imp += "Image Width: " + tamanhoH + "\n";
                imp += "Image Heigth: " + tamanhoV + "\n";
                updateMessage(imp);
            }

            public BufferedImage byteArrayToImage(byte[] data) throws IOException {
                return ImageIO.read(new ByteArrayInputStream(data));
            }         
            
            //imagem vem de lado
            private void movieMaker(){
                try{
                    Socket socket = new Socket(movieIp, moviePort);

                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());             
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    out.writeObject("ClientePC");
                    out.writeObject(new SocketInformationClient(tamanhoH, tamanhoV, iteracoes, videoPoints.fin.re, videoPoints.fin.im, videoPoints.zoomInit, videoPoints.zoomFin, fra, alg, palete));
                    out.flush();

                    byte [][] data = (byte[][])in.readObject();
                    frames = new ImageView[data.length];
                    for (int i = 0; i < data.length; i++) {
                       Image img = SwingFXUtils.toFXImage(byteArrayToImage(data[i]), null);
                       frames[i] = new ImageView(img);
                    }

                    socket.close();
                    in.close();
                    out.close();
                }
                catch(Exception ex){
                    updateTitle(ex.getMessage());
                }
                makeVideo = false;
            }
            
            private ImageView play(){
                playVideo = makeVideo = false;                       
                for (int i = 0; i < frames.length; i++) {
                    updateValue(frames[i]);
                    try {
                        Thread.sleep(70);
                    } catch (InterruptedException ex) {
                        //Logger.getLogger(CalcularFractal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return frames[frames.length - 1];
                
            }
            
            //quando um thread é chamada inicia o metodo call é o primeiro a ser executado
            @Override
            protected ImageView call() throws Exception {
                if(playVideo){
                    if(makeVideo)movieMaker();
                    return play();
                }
                if(!fra.equals("Dynamic Julia")){
                    if(alg.equals("Sequencial"))return this.desenharFractalSeq();
                    else if(alg.equals("Parallel"))return this.desenharFractalPar();
                    else if(alg.equals("Balanced(GUI)"))return this.desenharFractalBalGUI();
                    else return this.desenharFractalBal();
                }
                else return desenharFractalJulia();
            }
        };
    }   
}
/*
Falta fractal infinito paralelo
Zoom tem quase o dobro das casas do centro
*/
