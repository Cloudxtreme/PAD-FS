package mcsn.pad;


public class DaemonThread extends Thread {
	
	private Daemon d;
	private boolean background;
	private int sec;
	
	public DaemonThread(Daemon _d) {
		d=_d;
		background=false;
		sec=10;
	}
	
	public DaemonThread(Daemon _d, int _sec) {
		d=_d;
		background=true;
		sec=_sec;
	}
	
	public void run() {
		
		if (d == null)
			return; //junit
		
       d.FetchProcessing();
       if (!background)
    	   return;
       try {
    	   while (background) {
    		   System.out.println("PAD-FS: DEAMON THREAD SLEEPING");
    		   Thread.sleep(sec*1000);
    		   System.out.println("PAD-FS: DEAMON IS PROCESSING");
    		   d.FetchProcessing();
    	   }
       } catch (Exception e) {
    	   e.printStackTrace();
       }
    }

}
