/* © SRSoftware 2025 */
package de.srsoftware.tools;

import static java.lang.System.Logger.Level.INFO;

import org.junit.jupiter.api.Test;

public class ThreadTest {

	private System.Logger LOG = System.getLogger(ThreadTest.class.getSimpleName());


	@Test
	public void TestThreaded(){
		nest(1);
	}

	private void nest(int level) {
		LOG.log(INFO,"Started iteration #{0} (Thread {1})",level,Thread.currentThread().threadId());
		if (level <100){
			var nested = new Thread(){
				@Override
				public void run() {
					nest(level+1);
				}
			};
			nested.start();
			while (nested.getState() != Thread.State.TERMINATED){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		LOG.log(INFO,"Thread #{0} terminates.",level);
	}
}
