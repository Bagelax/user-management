package usermanagementbe.workers;

import usermanagementbe.model.ScheduledOperation;
import usermanagementbe.service.ErrorMessageService;
import usermanagementbe.service.MachineService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class OperationsQueueWorker extends Thread {
    private BlockingQueue<ScheduledOperation> operationsQ;
    private ConcurrentHashMap<String, Object> config;

    public OperationsQueueWorker(BlockingQueue<ScheduledOperation> operationsQ, ConcurrentHashMap<String, Object> config) {
        this.operationsQ = operationsQ;
        this.config = config;
    }

    @Override
    public void run() {
        while (this.config.get("running").equals("true")) {
            try {
                ScheduledOperation op = this.operationsQ.take();
                if (op.getMachine() == null) break;

                OperationWorker worker = new OperationWorker(op,
                        (MachineService) this.config.get("machineService"),
                        (ErrorMessageService) this.config.get("errorMessageService"));
                worker.start();
            }
            catch (InterruptedException e) {
                break;
            }
        }
    }
}
