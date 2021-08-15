/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Threads;

import Fractais.Fractal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 *
 * @author afons
 */
public class FractalThreadBalGUI extends Thread{
//descrição das vvariaveis mais premonorizada no contrutor
    AtomicInteger colunasI;
    int tamanhoH, tamanhoV;
    double maxC, minC, centroX, centroY, zoom, mini, iteracoes;
    String fra, col;
    double [][] graf;
    ProgressReporter reporter;
    boolean stop, wait;
    

    public FractalThreadBalGUI(AtomicInteger colunasI, int tamanhoH, int tamanhoV, double maxC, double minC, double centroX, double centroY, double zoom, double mini, double iteracoes, String fra, String col, double [][] graf, ProgressReporter reporter) {
        super();
        this.colunasI = colunasI;//variavel que conta o numero de colunas partilhada por todas a threads
        this.tamanhoH = tamanhoH;//tamanho horizontal da janela
        this.tamanhoV = tamanhoV;//tamanho vertical da janela
        this.maxC = maxC;//anglo maximo para a tonalidade do fractal(Hsv)
        this.minC = minC;//anglo mininmo para a tonalidade do fractal(Hsv)
        this.centroX = centroX;//coordenada real do centro fractal
        this.centroY = centroY;//coordenada imaginaria do centro fractal
        this.zoom = zoom;//valor do zoom do fractal
        this.mini = mini;//lado mais pequeno da janela
        this.iteracoes = iteracoes;//numero de iterações
        this.fra = fra;//algoritmo fractal(MandelBroth,...)
        this.col = col;//palete de cores
        this.graf = graf;//imagem onde vai ser desenhado o fractal
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
        double color,x,y;
        for (int colunas = this.colunasI.getAndIncrement(); colunas < this.tamanhoV; colunas = this.colunasI.getAndIncrement()) {
            for (int linhas = 0;linhas < this.tamanhoH; linhas++){
                
                //conversão das coordenadas reais para cordenadas virtuais
                x = this.centroX - (this.zoom * this.tamanhoH / this.mini) / 2 + this.zoom * linhas / this.mini;
                y = this.centroY - (this.zoom * this.tamanhoV / this.mini) / 2 + this.zoom * colunas / this.mini;
                
                
                //verifica se o fractal converge e se sim, retorna um valor em função od ponto de convergencia
                if (fra.contains("MandelBrot")) {
                    if(!"NME".equals(col))color = Fractal.MandelBroth(x, y, iteracoes);
                    else color = Fractal.NormalMapEffect(x, -y, iteracoes);
                }
                else if ("BurningShip".equals(this.fra)) color = Fractal.BurningShip(x, y, this.iteracoes);
                else color = Fractal.Julia(0, 0, x, y, this.iteracoes);

                color /= this.iteracoes;
                
                //torna o ponto de convergencia em uma cor consoante a palete escolhida
                synchronized(this.graf){
                    this.graf[linhas][colunas] = color;
                }
            }
            //sempre que uma linha é calculada a barra de progresso é actualizada
            this.reporter.accumulateProgress(1);
            if(this.stop)return;
        }
    }
}
