/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fractais;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 * @author afons
 */
public class ComplexB {
    public BigDecimal re;
    public BigDecimal im;
    
     public ComplexB(BigDecimal re, BigDecimal im){
        this.re = re;
        this.im = im;
    }
    
    public ComplexB(){
        this(BigDecimal.ZERO,BigDecimal.ZERO);
    }
    
    public BigDecimal modulus(MathContext mc){
        return sqrt((this.re.multiply(this.re, mc)).add((this.im.multiply(this.im, mc)), mc), mc);
    }
    
    public ComplexB multiply(ComplexB num, MathContext mc){
        return new ComplexB((this.re.multiply(num.re, mc)).subtract(this.im.multiply(num.im, mc), mc),(this.re.multiply(num.im, mc)).add(this.im.multiply(num.re, mc), mc));
    }
    
    public Complex multiply(Complex num, MathContext mc){
        double a = (this.re.multiply(new BigDecimal(num.re), mc)).subtract(this.im.multiply(new BigDecimal(num.im), mc), mc).doubleValue();
        double b = (this.re.multiply(new BigDecimal(num.im), mc)).add(this.im.multiply(new BigDecimal(num.re), mc), mc).doubleValue();
        return new Complex(a,b);
        //return new ComplexB((this.re.multiply(num.re, mc)).subtract(this.im.multiply(num.im, mc), mc),(this.re.multiply(num.im, mc)).add(this.im.multiply(num.re, mc), mc));
    }
    
    public ComplexB sum(ComplexB num, MathContext mc){
        return new ComplexB((this.re.add(num.re, mc)),(this.im.add(num.im,mc)));
    }
    
    public ComplexB sum(Complex num, MathContext mc){
        return new ComplexB((this.re.add(new BigDecimal(num.re), mc)),(this.im.add(new BigDecimal(num.im),mc)));
    }
    
    public ComplexB subtract(ComplexB num, MathContext mc){
        return new ComplexB((this.re.subtract(num.re, mc)),(this.im.subtract(num.im, mc)));
    }
    
    public Complex doubleComplex(){
        return new Complex(this.re.doubleValue(), this.im.doubleValue());
    }
    
    private static BigDecimal sqrt(BigDecimal A, MathContext mc) {
        BigDecimal x0 = BigDecimal.ZERO;
        BigDecimal x1 = A;
        final BigDecimal dois = BigDecimal.valueOf(2);
        while (!x0.equals(x1)) {
            x0 = x1;
            x1 = A.divide(x0, mc);
            x1 = x1.add(x0);
            x1 = x1.divide(dois, mc);
        }
        return x1;
    }
}
