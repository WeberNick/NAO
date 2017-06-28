package foundation.monitor;

import org.hyperic.sigar.*;
import java.text.DecimalFormat;
/**
 * <p>This class monitors the current hardware situation and deliver information about the memory usage and the cpu performance.</p> 
 * @author Mike Siefert
 * @version 1.0
 * <br>See <a href="https://support.hyperic.com/display/SIGAR/Home#Home-overview">Sigar API</a>
 */
public class PerformanceManager{
	private DecimalFormat dc; 
	
    public PerformanceManager(){
    	dc = new DecimalFormat("#,###,##0.00");
    }
    
    /**
	 * <p>This method returns a String with the current CPU performance.</p>
	 * @return str is a formatted String.
	 */
    
    public String getCpuPerformance(){
    	StringBuffer str = new StringBuffer();
    	CpuPerc [] cpu;
		try {
			cpu = new Sigar().getCpuPercList();
			
			str.append("CPU Performance: ");
	    	for(int i=0;i<cpu.length;i++){
	    		str.append((i+1) + ". ");
	    		str.append(cpu[i].toString(), 0, 17);
	    		if((i%2==1) && (i != (cpu.length-1))){
	    			str.append("<br>");
	    			str.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	    			str.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	    			str.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	    			str.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	    			str.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	    			str.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	    		}
	    		else if((i%2)==0){
	    			str.append(" | ");
	    		}
	    	}
	    	
	    	
	    	
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return str.toString();
    }
	
    /**
   	 * <p>This method returns a String with the current memory usage.</p>
   	 * @return str is a formatted String.
   	 */
    
    public String getMemoryUsage(){
    	StringBuffer str = new StringBuffer();
    	Mem memory;
    	try {
			memory = new Sigar().getMem();
			double actualUsed = (double) memory.getActualUsed() /1024/1024;
			double total = (double)memory.getTotal()/1024/1024;
			double percentUsage = ((actualUsed/total) * 100);
		
		str.append("Memory Performance: "
						+ dc.format(actualUsed) + " mb / " 
						+ dc.format(total)  + "mb | "  
						+ dc.format(percentUsage) + " %");
					
    	} catch (SigarException e) {
			e.printStackTrace();
		}				
		return str.toString();
    }
}
