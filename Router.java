package com.company;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.IOException;


class semaphore {
    String outputfile;
    protected int value = 0 ;
    protected semaphore(int initial) { value = initial ; }
    public synchronized void Wait(Device D) throws InterruptedException, IOException {
        FileWriter file = new FileWriter("logfile.txt",true);
        value-- ;
        if (value < 0) {
            outputfile=D.name+"  "+'{'+D.type_device+'}'+" arrived and waiting\n";
            file.write(outputfile);
            System.out.println(outputfile);
            wait();

        }
        else{
            outputfile=D.name+"   "+'{'+D.type_device+'}'+" arrived\n";
            file.write(outputfile);
            System.out.println(outputfile);
        }
        file.close();
    }
    public synchronized void Signal() {
        value++ ;
        if (value <= 0)
        {notify();}
    }
}

class Router{
    int Size;
    semaphore semaphore;
    boolean []ListConnection;
    Router(int S){
        Size=S;
        semaphore=new semaphore(Size);
        ListConnection=new boolean[Size];
        for (int i = 0; i <Size; i++) {
            ListConnection[i]=false;
        }
    }
    synchronized void occupy_connection(Device D) throws IOException, InterruptedException {
        for(int i=0;i<Size;i++){
            if(!ListConnection[i]){
                D.Router_contact_number=i;
                ListConnection[i]=true;

                break;
            }
        }
    }
    synchronized void release_connection(Device D) throws IOException {
        ListConnection[D.Router_contact_number]=false;
    }

}
class Device extends Thread{
    String name;
    String type_device;
    int Router_contact_number;
    String outputfile;
    Router router;
    Device(String n,String t,Router r){this.name=n;this.type_device=t;this.router=r;}
    void log_in() throws IOException, InterruptedException {
        Random ran = new Random();
        int rand = ran.nextInt(1000);
        outputfile="Connection "+(this.Router_contact_number+1)+" : "+this.name+" log in\n";
        FileWriter file = new FileWriter("logfile.txt",true);
        file.write(outputfile);
        file.close();
        System.out.println(outputfile);
        Thread.sleep(rand);
    }
    void perform_online_activity(Device D) throws IOException, InterruptedException {
        Random ran = new Random();
        int rand = ran.nextInt(900);
        FileWriter file = new FileWriter("logfile.txt",true);
        String outputfile="Connection "+(D.Router_contact_number+1)+" : "+D.name+"  performs online activity\n";
        file.write(outputfile);
        System.out.println(outputfile);
        file.close();
        Thread.sleep(rand);
    }
    void log_out() throws IOException, InterruptedException {
        Random ran = new Random();
        int rand = ran.nextInt(900);
        outputfile="Connection "+(this.Router_contact_number+1)+" : "+this.name+"  logged out\n";
        FileWriter file = new FileWriter("logfile.txt",true);
        file.write(outputfile);
        file.close();
        System.out.println(outputfile);
        Thread.sleep(rand);
    }
    @Override
    public void run(){
        try {
            router.semaphore.Wait(this);
            router.occupy_connection(this);
            outputfile="Connection " + (this.Router_contact_number+1) + ": " + name + " Occupied\n";
            FileWriter file = new FileWriter("logfile.txt",true);
            file.write(outputfile);
            file.close();
            System.out.println(outputfile);
            System.out.println();
            log_in();
            perform_online_activity(this);
            log_out();
            router.release_connection(this);
            router.semaphore.Signal();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }


    }
}
class Network{
    public static void main(String[] args) throws InterruptedException {
        Scanner Scan=new Scanner(System.in);
        ArrayList<String> names=new ArrayList<>();
        ArrayList<String> types=new ArrayList<>();
        System.out.println("What is the number of WI-FI Connections?");
        int num_connection=Scan.nextInt();
        Router router=new Router(num_connection);
        System.out.println("What is the number of devices Clients want to connect?");
        int num_device=Scan.nextInt();
        int n=num_device;
        while (num_device!=0){
            Thread.sleep(10);
            System.out.println("Enter Device name");
            String name_device=Scan.next();
            System.out.println("Enter Device type");
            String type=Scan.next();
            names.add(name_device);
            types.add(type);
            num_device--;
        }
        for (int i=0;i<n;i++) {
            Device D = new Device(names.get(i), types.get(i),router);
            D.start();

        }
    }
}