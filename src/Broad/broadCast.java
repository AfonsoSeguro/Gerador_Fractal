/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Broad;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author afons
 */
public class broadCast {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(6000);
        while(true){
            Socket soc = server.accept();
            ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
            
            
            
            
            
            
            soc.close();
            in.close();
            out.close();
        }
        
    }
}
