package mcsn.pad;


public class DeamonThread extends Thread {
	
	Deamon d;
	boolean background;
	
	public DeamonThread(Deamon _d, boolean _background) {
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
    		   Thread.sleep(10000);
    		   d.FetchProcessing();
    	   }
       } catch (Exception e) {
    	   e.printStackTrace();
       }
    }

}
