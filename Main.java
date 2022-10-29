package thread;
import java.util.Random;
import java.util.concurrent.Semaphore;

class Empresa implements Runnable {
    static public int cont = 0; // variável compartilhada
    static public int conectado = -1;
    
    private String id;
    private float tempo;
    private String empresa;
    
    
    public Empresa(String id, String empresa ,float tempo) {
        this.id = id;
        this.empresa = empresa;
        this.tempo = tempo;
    }

    public void up(Semaphore s) {
        s.release();
    }

    public void down(Semaphore s) {
        try {
            s.acquire();
        } catch (InterruptedException ignored) {}
    }

    public void sleep(float segs) {
        try {
            Thread.sleep((long)(segs*1_000));
        } catch (InterruptedException ignored) {}
    }
    
    private void realizaAcao() {
        try {
        	Random rand = new Random();
            sleep(rand.nextFloat((10 - 5) + 1) + 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void regiaoCritica() {
    	System.out.println("+ F"+id + " [" + empresa+"] acessou");
    	realizaAcao();
    	up(Main.s);
        --cont;
        if(cont == 0) {
        	conectado = -1;
        }
    	System.out.println("- F"+id + " [" + empresa+"] terminou acesso");
    }
    @Override
    public void run() {
    		sleep(tempo);
    		System.out.println("F"+id + " [" + empresa+"] tentando acesso");
        	if(conectado == -1) {
        		conectado = empresa == "A" ? 0 : 1;
        		++cont;
        		down(Main.s);
        		regiaoCritica();
        	}else if(conectado == 0 && empresa == "A") {
        		++cont;
        		down(Main.s);
        		regiaoCritica();
        	}else if(conectado == 1 && empresa == "B") {
        		++cont;
        		down(Main.s);
        		regiaoCritica();
        	}
        
    }
}

public class Main {

	public static final int N = 10;
	public static final int total = 3;
	public static final Semaphore s = new Semaphore(total);
	
	public static void main(String[] args) throws InterruptedException{
		
		Random rand = new Random();
		int max = 10;
		int min = 5;
		int i;
		Thread [] funcionarioA = new Thread[N];
        Thread [] funcionarioB = new Thread[N];
        
        for(i=0;i < N;i++) {
        	funcionarioA[i] = new Thread(new Empresa(String.valueOf(i+1), "A", rand.nextFloat((max - min) + 1) + min));
        	funcionarioB[i] = new Thread(new Empresa(String.valueOf(i+1), "B", rand.nextFloat((max - min) + 1) + min));
        }
        System.out.println("comecou!");
        for (i=0; i<N; ++i) {
        	funcionarioA[i].start();
        	funcionarioB[i].start();
        }
        // aguarda fim dos threads
//        for (i=0; i<N; ++i) {
//        	funcionarioA[i].join();
//    		funcionarioB[i].join();
//        }
//		
		System.out.println("ACABOU!");
	}

}
