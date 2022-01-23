package usermanagementbe.service;

import org.springframework.stereotype.Service;
import usermanagementbe.model.ScheduledOperation;
import usermanagementbe.workers.OperationsQueueWorker;
import usermanagementbe.workers.ScheduledOperationsWorker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConfigService {
    private BlockingQueue<ScheduledOperation> operationQ;
    private ConcurrentHashMap<String, Object> config;

    public ConfigService(MachineService machineService,
                         ScheduledOperationService scheduledOperationService,
                         ErrorMessageService errorMessageService) {
        System.out.println("\n\nCONSTRUCTOR\n\n");
        this.operationQ = new ArrayBlockingQueue<ScheduledOperation>(1000);
        this.config = new ConcurrentHashMap<String, Object>();
        this.config.put("running", "true");
        this.config.put("machineService", machineService);
        this.config.put("scheduledOperationService", scheduledOperationService);
        this.config.put("errorMessageService", errorMessageService);
        OperationsQueueWorker operationsQueueWorker = new OperationsQueueWorker(this.operationQ, this.config);
        operationsQueueWorker.start();
        ScheduledOperationsWorker scheduledOperationsWorker = new ScheduledOperationsWorker(this.operationQ, this.config);
        scheduledOperationsWorker.start();
    }

    public BlockingQueue<ScheduledOperation> getOperationQ() {
        return operationQ;
    }

    public ConcurrentHashMap<String, Object> getConfig() {
        return config;
    }
}
