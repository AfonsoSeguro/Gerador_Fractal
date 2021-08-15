/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Fractais.Complex;
import Fractais.Fractal;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author afons
 */
public class FractalThreadBalFServer extends Thread{
    //descrição das variáveis mais pormenorizada no construtor
    AtomicInteger colunasI;
    int tamanhoH, tamanhoV;
    double iteracoes;
    MathContext mc;
    Complex [] centroDouble;
    BigDecimal cX, cY, xC, yC, zs;
    Complex [] ABC;
    BufferedImage colors;
    boolean stop;
    float palete;

    FractalThreadBalFServer(AtomicInteger colunasI, int tamanhoH, int tamanhoV, double iteracoes, MathContext mc, Complex[] centroDouble, BigDecimal cX, BigDecimal cY, BigDecimal xC, BigDecimal yC, BigDecimal zs, Complex[] ABC, BufferedImage colors, float palete) {
        super();
        this.colunasI = colunasI;//variável que conta o número de colunas partilhada por todas as threads
        this.tamanhoH = tamanhoH;//tamanho horizontal da janela
        this.tamanhoV = tamanhoV;//tamanho vertical da janela
        this.iteracoes = iteracoes;//número de iterações
        this.mc = mc;
        this.centroDouble = centroDouble;
        this.cX = cX;
        this.cY = cY;
        this.xC = xC;
        this.yC = yC;
        this.zs = zs;
        this.ABC = ABC;
        this.colors = colors;
        this.stop = false;//caso a thread seja mandada parar
        this.palete = palete;
    }

    @Override
    public void interrupt(){
        this.stop = true;
        super.interrupt();
    }
    
    @Override
    public void run(){
        BigDecimal x,y;
        for (int colunas = colunasI.getAndIncrement(); colunas < tamanhoV; colunas = colunasI.getAndIncrement()) {
            for (int linhas = 0; linhas < tamanhoH; linhas++) {
                x = xC.add(zs.multiply(new BigDecimal(linhas), mc), mc);
                y = yC.add(zs.multiply(new BigDecimal(colunas), mc), mc);

                //calcular a cor
                double color = Fractal.ColorPertubationAproximation(centroDouble,x.subtract(cX, mc).doubleValue(), y.subtract(cY, mc).doubleValue(), ABC, (int)iteracoes, mc);

                float colorF = (float)(color / this.iteracoes * palete);
                colors.setRGB(linhas, colunas, Color.HSBtoRGB(1 - colorF, 1f, colorF));
                //pintar o pixel
            }
            //sempre que uma linha é calculada a barra de progresso é actualizada
            if(this.stop)return;
        }
    }
    
}
