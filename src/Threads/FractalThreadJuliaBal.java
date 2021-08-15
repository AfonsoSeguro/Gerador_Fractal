/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Threads;

import Fractais.Fractal;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 *
 * @author afons
 */
public class FractalThreadJuliaBal extends Thread{
    //descrição das vvariaveis mais premonorizada no contrutor
    AtomicInteger linhasI;
    int tamanhoH, tamanhoV;
    double maxC, minC, centroX, centroY, zoom, mini, iteracoes, X, Y;
    String fra, col;
    PixelWriter graf;

    public FractalThreadJuliaBal(AtomicInteger linhasI, int tamanhoH, int tamanhoV, double maxC, double minC, double centroX, double centroY, double zoom, double mini, double iteracoes, double X, double Y, String fra, String col, PixelWriter graf) {
        super();     
        this.linhasI = linhasI;//variavel que conta o numero de linhas partilhada por todas a threads
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
        this.X = X;//coordenada real do fractal de julia especifico
        this.Y = Y;//coordenada imaginaria do fractal de julia especifico
    }
    
    @Override
    public void run(){
        double color, x, y;
        for (int linhas = this.linhasI.get(); linhas < tamanhoH; linhas = this.linhasI.getAndIncrement()) {
            for (int colunas = 0; colunas < tamanhoV; colunas++) {
		//conversão das coordenadas reais para cordenadas virtuais
                x = centroX - (zoom * tamanhoH / mini) / 2 + zoom * linhas / mini;
                y = centroY - (zoom * tamanhoV / mini) / 2 + zoom * colunas / mini;
		//verifica se o fractal converge e se sim, retorna um valor em função od ponto de convergencia
                color = Fractal.Julia(((X - (tamanhoH / 2)) / (tamanhoH / 2)), ((Y - (tamanhoV / 2)) / (tamanhoV / 2)), x, y, iteracoes);
                color /= iteracoes;
		//torna o ponto de convergencia em uma cor consoante a palete escolhida
                if("HSB".equals(col))graf.setColor(linhas, colunas, Color.hsb(360 - (color*(maxC - minC) + minC), 1.0 , color));
                else if("RGB".equals(col))graf.setColor(linhas, colunas, Color.color(color, 1.0  - color, color));
                else graf.setColor(linhas, colunas, Color.gray(color));
            }
        }
    }
    
}
