package usermanagementbe.workers;

import usermanagementbe.model.ScheduledOperation;
import usermanagementbe.service.ScheduledOperationService;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ScheduledOperationsWorker extends Thread {
    private BlockingQueue<ScheduledOperation> operationsQ;
    private ConcurrentHashMap<String, Object> config;
    private final ScheduledOperationService scheduledOperationService;

    public ScheduledOperationsWorker(BlockingQueue<ScheduledOperation> operationsQ, ConcurrentHashMap<String, Object> config) {
        this.operationsQ = operationsQ;
        this.config = config;
        this.scheduledOperationService = (ScheduledOperationService) config.get("scheduledOperationService");
    }

    @Override
    public void run() {
        while (this.config.get("running").equals("true")) {
            try {
                List<ScheduledOperation> operations = this.scheduledOperationService.findInLastSecond();
                System.out.println("Searching for scheduled operations");

                for (ScheduledOperation op : operations) {
                    System.out.println("Running operation: " + op.toString());
                    this.operationsQ.put(op);
                    op.setExecuted(true);
                    this.scheduledOperationService.save(op);
                }

                sleep(1000);
            }
            catch (InterruptedException e) {
                break;
            }
        }
    }
}
