/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Fractais.Fractal;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author afons
 */
public class FractalThreadBalServer extends Thread{
    //descrição das variáveis mais pormenorizada no construtor
    AtomicInteger colunasI;
    int tamanhoH, tamanhoV;
    double centroX, centroY, zoom, mini, iteracoes;
    String fra;
    BufferedImage colors;
    boolean stop;
    float palete;

    FractalThreadBalServer(AtomicInteger colunasI, int tamanhoH, int tamanhoV, double centroX, double centroY, double zoom, int mini, double iteracoes, String fra, BufferedImage colors, float palete) {
        super();
        this.colunasI = colunasI;//variável que conta o número de colunas partilhada por todas as threads
        this.tamanhoH = tamanhoH;//tamanho horizontal da janela
        this.tamanhoV = tamanhoV;//tamanho vertical da janela
        this.centroX = centroX;//coordenada real do centro fractal
        this.centroY = centroY;//coordenada imaginária do centro fractal
        this.zoom = zoom;//valor do zoom do fractal
        this.mini = mini;//lado mais pequeno da janela
        this.iteracoes = iteracoes;//número de iterações
        this.fra = fra;//algoritmo fractal(MandelBroth,...)
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
        double color,x,y;
         for (int colunas = this.colunasI.getAndIncrement(); colunas < this.tamanhoV; colunas = this.colunasI.getAndIncrement()) {
           for (int linhas = 0;linhas < this.tamanhoH; linhas++){
                
                //conversão das coordenadas reais para cordenadas virtuais
                x = this.centroX - (this.zoom * this.tamanhoH / this.mini) / 2 + this.zoom * linhas / this.mini;
                y = this.centroY - (this.zoom * this.tamanhoV / this.mini) / 2 + this.zoom * colunas / this.mini;
                
                //verifica se o fractal converge e se sim, retorna um valor em função od ponto de convergencia
                if (this.fra.contains("MandelBrot"))color = Fractal.MandelBroth(x, y, iteracoes);
                else color = Fractal.BurningShip(x, y, this.iteracoes);

                float colorF = (float)(color / this.iteracoes * palete);
                colors.setRGB(linhas, colunas, Color.HSBtoRGB(1 - colorF, 1f, colorF));
                
            }
            if(this.stop)return;
        }        
    }
}