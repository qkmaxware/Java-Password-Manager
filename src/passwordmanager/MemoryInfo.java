/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

/**
 *
 * @author Colin Halseth
 */
import java.awt.FlowLayout;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class MemoryInfo extends JPanel{
    
    public Timer timer;
    
    private final int mb = 1024*1024;
    private final String unit = "mb";
    
    public MemoryInfo(int refreshRate){
        JLabel lable = new JLabel("Memory Usage: ");
        JProgressBar pb = new JProgressBar();
        pb.setMinimum(0); pb.setMaximum(100);
        Runtime run = Runtime.getRuntime();
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        pb.setStringPainted(true);
        double percent = ((double)GetUsedMemory(run))/run.totalMemory();
        pb.setValue((int)(percent*100));
        String value = GetUsedMemory(run)/mb+"/"+run.totalMemory()/mb+unit;
        pb.setString(value);
        
        this.add(lable);
        this.add(pb);
        
        timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                double percent = ((double)GetUsedMemory(run))/run.totalMemory();
                pb.setValue((int)(percent*100));
                String value = GetUsedMemory(run)/mb+"/"+run.totalMemory()/mb+unit;
                pb.setString(value);
            }
            
        }, 0, 5000);
    } 
    
    public long GetUsedMemory(Runtime run){
        return run.totalMemory() - run.freeMemory();
    }
    
}
