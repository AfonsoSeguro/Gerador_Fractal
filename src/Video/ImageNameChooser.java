/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Video;

/**
 *
 * @author afons
 */
public class ImageNameChooser {
    private int length,num;
    private String name;

    public ImageNameChooser(int length, int num, String name) {
        this.length = length;
        this.num = num;
        this.name = name;
    }
    
    public String GetName(){
        String nome = name;
        int i = 0;
        int testNum = num;
        while((testNum /= 10) > 0)i++;
        for (;i < length; i++) {
            nome += "0";
        }
        nome += num + "";
        return nome;
    }
    
    public int getNum(){
        return num;
    }
    
    public int incrementAndGet(){
        return ++num;
    }
}
