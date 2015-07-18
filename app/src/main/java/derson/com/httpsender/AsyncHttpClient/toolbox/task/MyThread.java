package derson.com.httpsender.AsyncHttpClient.toolbox.task;

import android.os.Handler;
import android.os.Message;


/**
 * 描述： 单线程(不是短时间经常执行的线程用此线程)
 *
 * @author yangzhi
 */
public class MyThread extends Thread {
	
	/** The tag. */
	private static String TAG = "HPThread";
	
	/** The Constant D. */
	private static final boolean D = true;
	
	/** 下载单位. */
	public MyTaskItem item = null;
	
	/** 下载完成后的消息句柄. */
    private static Handler handler = new Handler() { 
        @Override 
        public void handleMessage(Message msg) { 
        	MyTaskItem item = (MyTaskItem)msg.obj;
            item.listener.onTaskCompleted(item.getResult()); 
        } 
    }; 
    
    /**
     * 构造下载线程队列.
     */
    public MyThread() {
    }
      
    /**
     * 开始一个下载任务.
     *
     * @param item 下载单位
     */
    public void execute(MyTaskItem item) {
    	 this.item = item;
         this.start();
    } 
 
    /**
     * 描述：线程运行.
     *
     * @see Thread#run()
     */
    @Override 
    public void run() { 
            if(item!=null) { 
            	//定义了回调
                if (item.listener != null) { 
                	item.listener.doTask(item);
                	//交由UI线程处理 
                    Message msg = handler.obtainMessage(); 
                    msg.obj = item; 
                    handler.sendMessage(msg); 
                } 
        } 
    } 
    
}

