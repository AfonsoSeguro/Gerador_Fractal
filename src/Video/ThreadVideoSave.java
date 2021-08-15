/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Video;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 * @author afons
 */
public class ThreadVideoSave extends Thread{
    //descrição das variáveis mais pormenorizada no construtor
    private ImageView [] frames;
    private String path, col;
    private int tamH, tamV, numFrames;
    private double maxC, minC;

    public ThreadVideoSave(ImageView [] frames, String path, String col, double maxC, double minC, int tamH, int tamV, int numFrames) {
        super();
        this.frames = frames;
        this.path = path;
        this.col = col;
        this.maxC = maxC;
        this.minC = minC;
        this.tamH = tamH;//tamanho horizontal da janela
        this.tamV = tamV;//tamanho vertical da janela
        this.numFrames = numFrames;//número de frames
    }
    
    private static int getDigitNum(int num){
        int i = 0;
        while((num /= 10) > 0)i++;
        return i;
    }

    @Override
    public void run(){
        ImageNameChooser im = new ImageNameChooser(getDigitNum(frames.length), 0, "img");
        FractalVideo.apagaImagensVelhas();
        for (int i = 0; i < frames.length; i++) {
            FractalVideo.makeImagesVideo(frames[i], im.GetName());
            im.incrementAndGet();
        }
        FractalVideo.makeVideo(path, numFrames);
    }
}
