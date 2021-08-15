/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Threads;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.scene.image.PixelWriter;

/**
 *
 * @author afons
 */
public class FractalThreadLinhasPar extends Thread {
    //descrição das vvariaveis mais premonorizada no contrutor
    int tamanhoH, tamanhoV;
    int linhaMin, linhaMax;
    double maxC, minC, centroX, centroY, zoom, mini, iteracoes;
    BigDecimal zoomB, centroXB, centroYB;
    String fra, col;
    PixelWriter graf;
    boolean bd;
    ProgressReporter reporter;
    MathContext math;
    Thread [] trs;

    public FractalThreadLinhasPar(int tamanhoH, int tamanhoV, int linhaMin, int linhaMax, double maxC, double minC, double centroX, double centroY, double zoom, double mini, double iteracoes, String fra, String col, PixelWriter graf, boolean bd, ProgressReporter reporter) {
        this.tamanhoH = tamanhoH;//tamanho horizontal da janela
        this.tamanhoV = tamanhoV;//tamanho vertical da janela
        this.linhaMin = linhaMin;//linhas inicial de calculo do fractal
        this.linhaMax = linhaMax;//linhas final de calculo do fractal
        this.maxC = maxC;//anglo maximo para a tonalidade do fractal(Hsv)
        this.minC = minC;//anglo mininmo para a tonalidade do fractal(Hsv)
        this.centroX = centroX;//coordenada real do centro fractal
        this.centroY = centroY;//coordenada imaginaria do centro fractal
        this.zoom= zoom;//valor do zoom do fractal
        this.mini = mini;//lado mais pequeno da janela
        this.iteracoes = iteracoes;//numero de iterações
        this.fra = fra;//algoritmo fractal(MandelBroth,...)
        this.col = col;//palete de cores
        this.graf = graf;//imagem onde vai ser desenhado o fractal
        this.bd = bd;//caso seja bigDecimal ou nao
        this.reporter = reporter;// progressbar multithread
    }
    
    public FractalThreadLinhasPar(int tamanhoH, int tamanhoV, int linhaMin, int linhaMax, double maxC, double minC, BigDecimal centroX, BigDecimal centroY, BigDecimal zoom, double mini, double iteracoes, String fra, String col, PixelWriter graf, boolean bd, MathContext math, ProgressReporter reporter) {
        super();
        this.tamanhoH = tamanhoH;//tamanho horizontal da janela
        this.tamanhoV = tamanhoV;//tamanho vertical da janela
        this.linhaMin = linhaMin;//linhas inicial de calculo do fractal
        this.linhaMax = linhaMax;//linhas final de calculo do fractal
        this.maxC = maxC;//anglo maximo para a tonalidade do fractal(Hsv)
        this.minC = minC;//anglo mininmo para a tonalidade do fractal(Hsv)
        this.centroXB = centroX;//coordenada real do centro fractal
        this.centroYB = centroY;//coordenada imaginaria do centro fractal
        this.zoomB= zoom;//valor do zoom do fractal em BigDecimal
        this.mini = mini;//lado mais pequeno da janela
        this.iteracoes = iteracoes;//numero de iterações
        this.fra = fra;//algoritmo fractal(MandelBroth,...)
        this.col = col;//palete de cores
        this.graf = graf;//imagem onde vai ser desenhado o fractal
        this.bd = bd;//caso seja bigDecimal ou nao
        this.reporter = reporter;// progressbar multithread
        this.math = math;//numero de casas decimais que os valores bigDecimal podem ir
    }
    
    @Override
    public void interrupt(){
        if(!(trs == null)){
            for (Thread tr : trs) {
                if(!(tr == null)){
                    tr.interrupt();
                }
            }
        }     
    }

    @Override
    public void run() {
        //numero de recursos disponiveis sabendo que metade das threads ja foram lançadas
        int numProc = Runtime.getRuntime().availableProcessors() / 4;
        //caso seja o processador apenas tenha dois cores, pinta apenas duas linhas
        if(numProc < 1)numProc = 1;
        //calculo do intervalo que cada thread vai calcular consoante o o numero de cores
        int intervalo = this.tamanhoV / numProc;
        //inicialização de uma pool fixa de threads
        ExecutorService exe = Executors.newFixedThreadPool(numProc);
        
        if(!this.bd)trs = new FractalThreadColunasPar[numProc];
        else trs = new FractalThreadColunasParBD[numProc];
        
        for (int i = 0; i < numProc; i++) {
            //inicialização da thread especifica
            if(!this.bd)trs[i] = new FractalThreadColunasPar(this.tamanhoH, this.tamanhoV, this.linhaMin, this.linhaMax, i * intervalo, (i * intervalo) + intervalo, this.maxC, this.minC, this.centroX, this.centroY, this.zoom, this.mini, this.iteracoes, this.fra, this.col, this.graf, this.reporter);
            else trs[i] = new FractalThreadColunasParBD(this.tamanhoH, this.tamanhoV, this.linhaMin, this.linhaMax, i * intervalo, (i * intervalo) + intervalo, this.maxC, this.minC, this.iteracoes, this.centroXB, this.centroYB, this.zoomB, this.mini, this.fra, this.col, this.graf, this.math, this.reporter);
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
    }
}
