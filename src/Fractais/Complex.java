/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fractais;

/**
 *
 * @author afons
 */
public class Complex {
    
    public double re;
    public double im;
    
    public Complex(double re, double im){
        this.re = re;
        this.im = im;
    }
    
    public Complex(double ang){
        this.re = Math.cos(ang);
        this.im = Math.sin(ang);
    }
    
    public Complex(){
        this(0,0);
    }
    
    public double modulus(){
        return Math.sqrt((this.re * this.re) + (this.im * this.im));
    }
    
    public Complex multiply(Complex num){
        return new Complex((this.re * num.re) - (this.im * num.im),(this.re * num.im) + (this.im * num.re));
    }
    
    public Complex sum(Complex num){
        return new Complex((this.re + num.re),(this.im + num.im));
    }
    
    public Complex subtract(Complex num){
        return new Complex((this.re - num.re),(this.im - num.im));
    }
    
    public Complex divide(Complex num){
        Complex invert = new Complex(num.re, num.im * -1);
        Complex cima = this.multiply(invert);
        Complex baixo = num.multiply(invert);
        return new Complex(cima.re/baixo.re, cima.im/baixo.re);
        
    }
    
    public Complex divide(double num){
        return new Complex(this.re/num, this.im/num);
    }
    
    @Override
    public String toString(){
        if(this.im < 0)return this.re + " " + this.im;
        return this.re + " +" + this.im; 
    }
}
