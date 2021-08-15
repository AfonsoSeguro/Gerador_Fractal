/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Video;

import java.io.File;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;



/**
 *
 * @author afons
 */
public class FractalVideo {
    //Cria uma imagem
    public static void makeSingleImage(ImageView vImg, String path){
        if(!(path.endsWith("jpg") || path.endsWith("png")))path += ".png";
        File ficheiro = new File(path);
        try{ImageIO.write(SwingFXUtils.fromFXImage(vImg.getImage(), null), "png", ficheiro);}
        catch(Exception ex){System.err.println(ex.getMessage());}
    }
    //Guarda as imagens que fazem o vídeo
    public static void makeImagesVideo(ImageView vImg, String nome){
        File pasta = new File("FractalImages");
        if(!pasta.exists())pasta.mkdir();
        pasta = new File("FractalImages/" + nome + ".png");
        try{ImageIO.write(SwingFXUtils.fromFXImage(vImg.getImage(), null), "png", pasta);}
        catch(Exception ex){System.err.println(ex.getMessage());}
    }
    //Junta as imagens e cria o vídeo
    public static void makeVideo(String nome, int fps) {
        try{
            File prog = new File("FractalMovieMaker.exe");
            //Process process = new ProcessBuilder(prog.getAbsolutePath() + " " + nome + " " + fps).start();
            ProcessBuilder pb = new ProcessBuilder(prog.getAbsolutePath(), nome, fps + "");
            Process p = pb.start();
        }
        catch(Exception ex){
            System.err.println(ex.getMessage());
        }
    }
    //Apaga as imagens que já não são precisas
    public static void apagaImagensVelhas(){
        File pasta = new File("FractalImages");
        if(!pasta.exists())return;
        File [] imgs = pasta.listFiles();
        for (File img : imgs) {
            img.delete();
        }
    }
    
}

//https://stackoverflow.com/questions/24544361/how-to-create-a-mp4-video-from-images-in-java-by-using-jcodec-library