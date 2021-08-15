/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fractais;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

/**
 *
 * @author afons
 */
public class Fractal {
    //Otimizações feitas
    //Melhoramento para o menor numero de calculos possiveis
    //Testes de periocidade
    //Função de cardiode

    //Otimizações futuras
    //Border Tracing
    //Simetria
    //Teoria da pertubação e da aproximação por series
    
    /*
    Otimização 1
    Formula de mandelbroth para o menor numero de calculos possivel por iteração
    
    public static int color(double real, double imaginario, int itera){
        double x2 = 0, y2 = 0, x = 0, y = 0;
        while(x2 + y2 <= 4 && itera > 0){
            y = 2 * x * y + imaginario;
            x = x2 - y2 + real;
            x2 = x * x;
            y2 = y * y;
            itera--;
        }
        return itera;
    }
    */
    
    /*
    Otimização 2
    Verificação de convergencia de um ponto a partir de vários teste por intervalos,ou seja, se um ponto convergir, quer dizer que o seu valor é constante a medida que o numero de iterações avança, ao
    fazer testes de periocidade concegue-se determinar se um um ponte converge sem ser necessário calcular todas as iterações
    
    public static int color(double real, double imaginario, int itera){
        double zr = 0, zi = 0, xo = 0, yo = 0, aux;
        double per = 0, maxPer = itera/100;
        if(converg(real, imaginario))return 0;
        while (itera > 0 && (zr * zr + zi * zi < 4)) {
            aux = zr * zr - zi * zi + real;
            zi = 2 * zi * zr + imaginario;
            zr = aux;
            itera--;
            if(zr == xo && zi == yo)return 0;
            per++;
            if(per > maxPer){
                per = 0;
                xo = real;
                yo = imaginario;
            }
        }
        return itera;
    }
    */
    
    /*
    Otimizaçoa 3
    Verificação da função de cardiode, esta função representa o centro do fractal de mandelbroth, ou seja, se algum ponto estiver contido nessa função, automaticamente ja sabemos de converge, não
    sendo necessario calcular uma grande parte preta do fractal
    
        double x14 = (x - 1/4.0);
        double y2 = y * y;
        double q = x14 * x14 + y2;
        return q *(q + x14)<= y2/4.0;
    */
    
    
    private static boolean converg(double x, double y){
        double x14 = (x - 1/4.0);
        double y2 = y * y;
        double q = x14 * x14 + y2;
        return q *(q + x14)<= y2/4.0;
    }
    
    public static double MandelBroth(double real, double imaginario, double itera) {
        if(converg(real, imaginario))return 0;
        double zr2 = 0, zi2 = 0, zr = 0, zi = 0, per = 0, maxPer = itera/1000, xo = 0, yo = 0;
        while(zr2 + zi2 < 4 && itera > 0){
            zi = 2 * zr * zi + imaginario;
            zr = zr2 - zi2 + real;
            zr2 = zr * zr;
            zi2 = zi * zi;
            itera--;
            if(per > maxPer){
                per = 0;
                xo = zr;
                yo = zi;
            }
        }
        return itera;
    }

    public static double BurningShip(double real, double imaginario, double itera) {
        double zr = 0, zi = 0, aux,xo  = 0, yo = 0, zr2 = 0, zi2 = 0;
        double per = 0, maxPer = itera/1000;
        while (itera > 0 && (zr2 + zi2 < 4)) {
            if (zr < 0) zr *= -1;
            if (zi < 0) zi *= -1;
            zi = 2 * zr * zi + imaginario;
            zr = zr2 - zi2 + real;
            zr2 = zr * zr;
            zi2 = zi * zi;
            itera--;
            if(zr == xo && zi == yo)return 0;
            per++;
            if(per > maxPer){
                per = 0;
                xo = zr;
                yo = zi;
            }
        }
        return itera;
    }

    public static double Julia(double cx, double cy, double real, double imaginario, double itera) {
        double zr = real, zi = imaginario, aux, xo  = 0, yo = 0;
        double per = 0, maxPer = itera/1000;
        while (itera > 0 && (zr * zr + zi * zi < 4)) {
            aux = zr * zr - zi * zi + cx;
            zi = 2 * zi * zr + cy;
            zr = aux;
            itera--;
            if(zr == xo && zi == yo)return 0;
            per++;
            if(per > maxPer){
                per = 0;
                xo = zr;
                yo = zi;
            }
        }
        return itera;
    }

    public static double MandelBrothBD(BigDecimal r, BigDecimal i, double itera, MathContext mc) {
        if(converg(r.doubleValue(), i.doubleValue()))return 0;
        BigDecimal zr2 = BigDecimal.ZERO, zi2 = BigDecimal.ZERO, zr = BigDecimal.ZERO, zi = BigDecimal.ZERO, xo = BigDecimal.ZERO, yo = BigDecimal.ZERO, dois = new BigDecimal(2);
        double per = 0, maxPer = itera/1000;
        while(zr2.add(zi2, mc).doubleValue() < 4 && itera > 0){
            zi = (dois.multiply(zr, mc).multiply(zi, mc)).add(i, mc);
            zr = (zr2.subtract(zi2, mc)).add(r, mc);
            zr2 = zr.multiply(zr, mc);
            zi2 = zi.multiply(zi, mc);
            if(zr.equals(xo) && zi.equals(yo))return 0;
            per++;
            if(per > maxPer){
                per = 0;
                xo = zr;
                yo = zi;
            }
            itera--;
        }
        return itera;
    }

    public static double BurningShipBD(BigDecimal r, BigDecimal i, double itera) {
        BigDecimal zr = BigDecimal.ZERO, zi = BigDecimal.ZERO, aux ,dois = BigDecimal.valueOf(2), a, b, xo = BigDecimal.ZERO, yo = BigDecimal.ZERO;
        BigDecimal menos1 = BigDecimal.valueOf(-1);
        double per = 0, maxPer = itera/1000;
        MathContext mc = new MathContext(64);
        while (itera > 0) {
            a = zr.multiply(zr, mc);
            b = zi.multiply(zi, mc);
            if ((a.add(b, mc).doubleValue() >= 4)) break;
            if (zr.doubleValue() < 0)zr = zr.multiply(menos1, mc);
            if (zi.doubleValue() < 0)zi = zi.multiply(menos1, mc);
            aux = a.subtract(b, mc).add(r, mc);
            zi = dois.multiply(zi, mc).multiply(zr, mc).add(i, mc);
            zr = aux;
            itera--;
            if(zr.equals(xo) && zi.equals(yo))return 0;
            per++;
            if(per > maxPer){
                per = 0;
                xo = zr;
                yo = zi;
            }
        }
        return itera;
    }

    
    public static double JuliaBD(double cx, double cy, double real, double imaginario, double itera) {
        BigDecimal zr = BigDecimal.valueOf(real), zi = BigDecimal.valueOf(imaginario), aux ,dois = BigDecimal.valueOf(2), cX = BigDecimal.valueOf(cx), cY = BigDecimal.valueOf(cy), a, b ,xo = BigDecimal.ZERO, yo = BigDecimal.ZERO;
        double per = 0, maxPer = itera/1000;
        MathContext mc = new MathContext(32);
        while (itera > 0) {
            a = zr.multiply(zr, mc);
            b = zi.multiply(zi, mc);
            if ((a.add(b, mc).doubleValue() >= 4)) break;
            aux = a.subtract(b, mc).add(cX, mc);
            zi = dois.multiply(zi, mc).multiply(zr, mc).add(cY, mc);
            zr = aux;
            itera--;
            if(zr.equals(xo) && zi.equals(yo))return 0;
            per++;
            if(per > maxPer){
                per = 0;
                xo = zr;
                yo = zi;
            }
        }
        return itera;
    }
    
    
    public static double NormalMapEffect(double re, double im, double itera) {
        if(converg(re, im))return 0;
        Complex dC = new Complex();
        Complex dois = new Complex(2,0);
        Complex um = new Complex(1,0);
        double reflection = 0;
        
        double h2 = 1.5;
        double angle = 45.0;
        Complex v = new Complex(2.0*angle*Math.PI/360);
        
        Complex u;
        
        Complex c = new Complex(re, im);
        Complex z = new Complex();
        while (itera > 0) {
            dC = dois.multiply(dC.multiply(z)).sum(um);
            z = z.multiply(z);
            z = z.sum(c);
            itera--;
            if (z.modulus() > 4) {
                u = z.divide(dC);
                u = u.divide(u.modulus());
                reflection = (u.re * v.re + u.im * v.im) + h2;
                reflection /= (1.0 + h2);
                if(reflection < 0)reflection = 0;
                break;
            }
        }
        return reflection * itera;
    }
    
    
    public static ArrayList<ComplexB> centralPoint(BigDecimal r, BigDecimal i, double itera, MathContext mc){
        ArrayList<ComplexB> sol = new ArrayList();
        BigDecimal zr2 = BigDecimal.ZERO, zi2 = BigDecimal.ZERO, zr = BigDecimal.ZERO, zi = BigDecimal.ZERO, xo = BigDecimal.ZERO, yo = BigDecimal.ZERO, dois = new BigDecimal(2);
        while(zr2.add(zi2, mc).doubleValue() < 4 && itera > 0){
            zi = (dois.multiply(zr, mc).multiply(zi, mc)).add(i, mc);
            zr = (zr2.subtract(zi2, mc)).add(r, mc);
            zr2 = zr.multiply(zr, mc);
            zi2 = zi.multiply(zi, mc);
            sol.add(new ComplexB(zr,zi));
            itera--;    
        }
        return sol;
    }
    
    
    public static int ColorPertubation(Complex [] center, double A0x, double A0y, int itera, MathContext mc){
        double AAx = 0, AAy = 0;
        //System.out.println(Y0.re + "+" + Y0.im + "     " + center[0].re + "+" + center[0].im);
        int maxItera = itera;
        
        double AAx2 = 0, AAy2 = 0, cX, cY, xo = 0, yo = 0;
        
        BigDecimal CX = BigDecimal.ZERO, CY = BigDecimal.ZERO;
        while((AAx2)+(AAy2) < 4 && itera > 0){
            double Auy = AAy;
            
            if(maxItera - itera < center.length){
                cX = center[maxItera - itera].re;
                cY = center[maxItera - itera].im;
            }
            else return itera;
            
            double u = AAx * AAy;
            AAy = 2*((cX * AAy) + (cY * AAx)) + ((u) + (u)) + (A0y);
            AAx =  2*((cX * AAx) - (cY * Auy)) + ((AAx2) - (AAy2)) + (A0x);
            
            AAx2 = AAx * AAx;
            AAy2 = AAy * AAy;
            itera--;
            
        }
        return itera;
    }
    
    
    public static boolean ColorPertubationPointFinder(Complex [] center, double A0x, double A0y, int itera, MathContext mc){
        double AAx = 0, AAy = 0;
        //System.out.println(Y0.re + "+" + Y0.im + "     " + center[0].re + "+" + center[0].im);
        int maxItera = itera;
        
        double AAx2 = 0, AAy2 = 0, cX, cY, xo = 0, yo = 0;
        
        BigDecimal CX = BigDecimal.ZERO, CY = BigDecimal.ZERO;
        while((AAx2)+(AAy2) < 4 && itera > 0){
            double Auy = AAy;
            
            if(maxItera - itera < center.length){
                cX = center[maxItera - itera].re;
                cY = center[maxItera - itera].im;
            }
            else return true;
            
            double u = AAx * AAy;
            AAy = 2*((cX * AAy) + (cY * AAx)) + ((u) + (u)) + (A0y);
            AAx =  2*((cX * AAx) - (cY * Auy)) + ((AAx2) - (AAy2)) + (A0x);
            
            AAx2 = AAx * AAx;
            AAy2 = AAy * AAy;
            itera--;
            
        }
        return false;
    }
    
    public static int ColorPertubationAproximation(Complex [] center, double A0x, double A0y, Complex [] ABC, int itera, MathContext mc){
        
        //Complex A02 = A0.multiply(A0);
        double A02x = (A0x * A0x) - (A0y * A0y);
        double A02y = (A0x * A0y) + (A0y * A0x);
        
        //Complex A03 = A0.multiply(A02);
        double A03x = (A0x * A02x) - (A0y * A02y);
        double A03y = (A0x * A02y) + (A0y * A02x);
        
        //Complex AA = (ABC[0].multiply(A0)).sum(ABC[1].multiply(A02)).sum(ABC[2].multiply(A03));
        double AAx = ((ABC[0].re * A0x) - (ABC[0].im * A0y)) + ((ABC[1].re * A02x) - (ABC[1].im * A02y)) + ((ABC[2].re * A03x) - (ABC[2].im * A03y));
        double AAy = ((ABC[0].re * A0y) + (ABC[0].im * A0x)) + ((ABC[1].re * A02y) + (ABC[1].im * A02x)) + ((ABC[2].re * A03y) + (ABC[2].im * A03x));
        
        BigDecimal CX = BigDecimal.ZERO, CY = BigDecimal.ZERO;
        
        
        int maxItera = itera;
        itera -= 3;
        
        double AAx2 = AAx * AAx, AAy2 = AAy * AAy, cX, cY;
        
        while((AAx2)+(AAy2) < 4 && itera > 0){
            
            double Auy = AAy;
            
            if(maxItera - itera < center.length){
                cX = center[maxItera - itera].re;
                cY = center[maxItera - itera].im;
            }
            else return 0;
            
            double u = AAx * AAy;
            AAy = 2*((cX * AAy) + (cY * AAx)) + ((u) + (u)) + (A0y);
            AAx =  2*((cX * AAx) - (cY * Auy)) + ((AAx2) - (AAy2)) + (A0x);
            
            AAx2 = AAx * AAx;
            AAy2 = AAy * AAy;
            itera--;
        }
        //System.out.println(System.currentTimeMillis() - time);
        return itera;
    }
}

//inspiração para as otimizações feitas
//https://pt.wikipedia.org/wiki/Conjunto_de_Mandelbrot
//https://en.wikipedia.org/wiki/Plotting_algorithms_for_the_Mandelbrot_set#Multithreading
//https://www.math.univ-toulouse.fr/~cheritat/wiki-draw/index.php/Mandelbrot_set
//https://en.wikipedia.org/wiki/Mandelbrot_set#Main_cardioid_and_period_bulbs
//https://en.wikibooks.org/wiki/Fractals/Iterations_in_the_complex_plane/Mandelbrot_set/mandelbrot

//futuras implementações no trabalho seguinte(feitas)
//https://fractalwiki.org/wiki/Perturbation_theory
//https://www.shadertoy.com/view/ttVSDW
//https://fractaltodesktop.com/perturbation-theory/index.html
//https://fractalwiki.org/wiki/Series_approximation
//https://math.stackexchange.com/questions/3083263/mandelbrot-set-perturbation-theory-when-do-i-use-it