package mcsn.pad;


public class DaemonThread extends Thread {
	
	Daemon d;
	boolean background;
	
	public DaemonThread(Daemon _d, boolean _background) {
		d=_d;
		background=_background;
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
    		   Thread.sleep(10000);
    		   System.out.println("PAD-FS: DEAMON IS PROCESSING");
    		   d.FetchProcessing();
    	   }
       } catch (Exception e) {
    	   e.printStackTrace();
       }
    }

}
