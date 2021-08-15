/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Threads;

import Fractais.Complex;
import Fractais.Fractal;
import java.math.BigDecimal;
import java.math.MathContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 *
 * @author afons
 */
public class FractalThreadParBDF extends Thread{
    //descrição das vvariaveis mais premonorizada no contrutor
    int tamanhoH, tamanhoV, colunasMax, colunasMin;
    double maxC, minC, iteracoes;
    String col;
    PixelWriter graf;
    MathContext mc;
    ProgressReporter reporter;
    boolean stop;
    
    Complex [] centroDouble;
    BigDecimal cX, cY, xC, yC, zs;
    Complex [] ABC;


    public FractalThreadParBDF(int colunasMin, int colunasMax, Complex[] centroDouble, BigDecimal cX, BigDecimal cY, BigDecimal xC, BigDecimal yC, BigDecimal zs, Complex[] ABC, int tamanhoH, int tamanhoV, double maxC, double minC, double iteracoes, String col, PixelWriter graf, MathContext mc, ProgressReporter reporter) {
        super();
        this.colunasMin = colunasMin;
        this.colunasMax = colunasMax;
        this.centroDouble = centroDouble;
        this.cX = cX;
        this.cY = cY;
        this.xC = xC;
        this.yC = yC;
        this.zs = zs;
        this.ABC = ABC;
        this.tamanhoH = tamanhoH;//tamanho horizontal da janela
        this.tamanhoV = tamanhoV;//tamanho vertical da janela
        this.maxC = maxC;//anglo maximo para a tonalidade do fractal(Hsv)
        this.minC = minC;//anglo mininmo para a tonalidade do fractal(Hsv)
        this.iteracoes = iteracoes;//numero de iterações
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
        BigDecimal x,y;
        for (int colunas = colunasMin; colunas < colunasMax; colunas++) {
            for (int linhas = 0; linhas < tamanhoH; linhas++) {
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
            this.reporter.accumulateProgress(1);
            if(this.stop)return;
        }
    }
}
