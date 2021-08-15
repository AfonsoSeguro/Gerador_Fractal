/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Threads;

import Fractais.Fractal;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 *
 * @author afons
 */
public class FractalThreadBalBD extends Thread{
    //descrição das vvariaveis mais premonorizada no contrutor
    AtomicInteger colunasI;
    int tamanhoH, tamanhoV;
    double maxC, minC, iteracoes;
    BigDecimal centroX, centroY, zoom, mini;
    String fra, col;
    PixelWriter graf;
    MathContext mc;
    ProgressReporter reporter;
    boolean stop;
    
    public FractalThreadBalBD(AtomicInteger colunasI, int tamanhoH, int tamanhoV, double maxC, double minC, double iteracoes, BigDecimal centroX, BigDecimal centroY, BigDecimal zoom, double mini, String fra, String col, PixelWriter graf, MathContext mc, ProgressReporter reporter) {
        super();
        this.colunasI = colunasI;//variavel que conta o numero de linhas partilhada por todas a threads
        this.tamanhoH = tamanhoH;//tamanho horizontal da janela
        this.tamanhoV = tamanhoV;//tamanho vertical da janela
        this.maxC = maxC;//anglo maximo para a tonalidade do fractal(Hsv)
        this.minC = minC;//anglo mininmo para a tonalidade do fractal(Hsv)
        this.centroX = centroX;//coordenada real do centro fractal
        this.centroY = centroY;//coordenada imaginaria do centro fractal
        this.zoom = zoom;//valor do zoom do fractal
        this.mini = new BigDecimal(mini);//lado mais pequeno da janela
        this.iteracoes = iteracoes;//numero de iterações
        this.fra = fra;//algoritmo fractal(MandelBroth,...)
        this.col = col;//palete de cores
        this.graf = graf;//imagem onde vai ser desenhado o fractal
        this.mc = mc;//numero de casas decimais que os valores bigDecimal podem ir
        this.reporter = reporter;// progressbar multithread
        this.stop = false;//caso a thread seja mandada parar
    }
    
    @Override
    public void interrupt(){
        this.stop = true;
        super.interrupt();
    }
    
    @Override
    public void run(){
        BigDecimal x, y, tamH = new BigDecimal(tamanhoH), tamV = new BigDecimal(tamanhoV), dois = new BigDecimal(2);
        double color;
        BigDecimal xc = centroX.subtract(((zoom.multiply(tamH, mc)).divide(mini, mc)).divide(dois, mc), mc);
        BigDecimal yc = centroY.subtract(((zoom.multiply(tamV, mc)).divide(mini, mc)).divide(dois, mc), mc);
        BigDecimal zomi = zoom.divide(mini, mc);
        for (int colunas = this.colunasI.getAndIncrement(); colunas < this.tamanhoV; colunas = this.colunasI.getAndIncrement()) {
           for (int linhas = 0;linhas < this.tamanhoH; linhas++){

                //conversão das coordenadas reais para cordenadas virtuais
                x = xc.add(zomi.multiply(new BigDecimal(linhas), mc));
                y = yc.add(zomi.multiply(new BigDecimal(colunas), mc));                   
                
                //verifica se o fractal converge e se sim, retorna um valor em função od ponto de convergencia
                if (fra.contains("MandelBrot")) color = Fractal.MandelBrothBD(x, y, iteracoes, mc);
                else color = Fractal.BurningShipBD(x, y, iteracoes);

                color /= (double)iteracoes;
                //torna o ponto de convergencia em uma cor consoante a palete escolhida
                if("HSB".equals(col))graf.setColor(linhas, colunas, Color.hsb(360 - (color*(maxC - minC) + minC), 1.0 , color));
                else if("RGB".equals(col))graf.setColor(linhas, colunas, Color.color(1 - (minC/180), color, 1 - (maxC/360)));
                else graf.setColor(linhas, colunas, Color.gray(color));
            }
            //sempre que uma linha é calculada a barra de progresso é actualizada
            this.reporter.accumulateProgress(1);
            if(this.stop)return;
        }
    }
    
    
}
