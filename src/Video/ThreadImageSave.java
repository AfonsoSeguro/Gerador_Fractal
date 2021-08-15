/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Video;

import javafx.scene.image.ImageView;

/**
 *
 * @author afons
 */
public class ThreadImageSave extends Thread{
    
    private ImageView img;
    private String name;

    public ThreadImageSave(ImageView img, String name) {
        super();
        this.img = img;
        this.name = name;
    }
    
    @Override
    public void run(){
        FractalVideo.makeSingleImage(img, name);
    }
}
