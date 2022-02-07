package usermanagementbe.workers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import usermanagementbe.model.ErrorMessage;
import usermanagementbe.model.MachineStatus;
import usermanagementbe.model.ScheduledOperation;
import usermanagementbe.service.ErrorMessageService;
import usermanagementbe.service.MachineService;
import usermanagementbe.service.ScheduledOperationService;
import usermanagementbe.workers.exceptions.IllegalOperationException;
import usermanagementbe.workers.exceptions.MachineDeletedException;
import usermanagementbe.workers.exceptions.MachineOperationRunningException;
import usermanagementbe.workers.exceptions.MachineStatusIllegal;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Random;

public class OperationWorker extends Thread {
    private ScheduledOperation op;
    private Random random;
    private final MachineService machineService;
    private final ErrorMessageService errorMessageService;
    static final int MIN_WAIT_TIME = 10000, MAX_WAIT_TIME = 13000;

    public OperationWorker(ScheduledOperation op,
                           MachineService machineService,
                           ErrorMessageService errorMessageService) {
        this.op = op;
        this.machineService = machineService;
        this.errorMessageService = errorMessageService;
        this.random = new Random();
    }

    private void checkMachine(MachineStatus requiredStatus) throws MachineDeletedException, MachineOperationRunningException, MachineStatusIllegal {
        MachineStatus status = this.op.getMachine().getStatus();
        if (!this.op.getMachine().isActive()) throw new MachineDeletedException();
        if (!status.equals(requiredStatus)) throw new MachineStatusIllegal();
        if (status.equals(MachineStatus.STARTING) || status.equals(MachineStatus.STOPPING)) throw new MachineOperationRunningException();
    }

    private void saveError(String message) {
        ErrorMessage error = new ErrorMessage();
        error.setError(message);
        error.setMachine(this.op.getMachine());
        error.setScheduledOperation(this.op);
        ZonedDateTime now = ZonedDateTime.now(Clock.systemUTC());
        error.setTime(new Timestamp(now.toEpochSecond() * 1000));
        this.errorMessageService.save(error);
    }

    @Override
    public void run() {
        try {
            switch (this.op.getOperation()) {
                case START -> {
                    System.out.println("Starting");
                    this.checkMachine(MachineStatus.STOPPED);
                    this.op.getMachine().setStatus(MachineStatus.STARTING);
                    this.machineService.save(this.op.getMachine());
                    sleep(this.random.nextInt(MIN_WAIT_TIME, MAX_WAIT_TIME));
                    System.out.println("Started");
                    this.op.getMachine().setStatus(MachineStatus.RUNNING);
                    this.machineService.save(this.op.getMachine());
                }
                case STOP -> {
                    System.out.println("Stopping");
                    this.checkMachine(MachineStatus.RUNNING);
                    this.op.getMachine().setStatus(MachineStatus.STOPPING);
                    this.machineService.save(this.op.getMachine());
                    sleep(this.random.nextInt(MIN_WAIT_TIME, MAX_WAIT_TIME));
                    System.out.println("Stopped");
                    this.op.getMachine().setStatus(MachineStatus.STOPPED);
                    this.machineService.save(this.op.getMachine());
                }
                case RESTART -> {
                    System.out.println("Restarting");
                    this.checkMachine(MachineStatus.RUNNING);
                    long sleepTime = (long) random.nextInt(MIN_WAIT_TIME, MAX_WAIT_TIME) / 3;
                    this.checkMachine(MachineStatus.RUNNING);
                    this.op.getMachine().setStatus(MachineStatus.STOPPING);
                    this.machineService.save(this.op.getMachine());
                    sleep(sleepTime);
                    System.out.println("Stopped");
                    this.op.getMachine().setStatus(MachineStatus.STOPPED);
                    this.machineService.save(this.op.getMachine());
                    sleep(sleepTime);
                    System.out.println("Starting");
                    this.op.getMachine().setStatus(MachineStatus.STARTING);
                    this.machineService.save(this.op.getMachine());
                    sleep(sleepTime);
                    System.out.println("Started");
                    this.op.getMachine().setStatus(MachineStatus.RUNNING);
                    this.machineService.save(this.op.getMachine());
                }
                default -> throw new IllegalOperationException();
            }
        }
        catch (MachineDeletedException e) {
            this.saveError("Machine deleted");
        }
        catch (MachineStatusIllegal e) {
            this.saveError("Machine status illegal");
        }
        catch (MachineOperationRunningException e) {
            this.saveError("Machine already running an operation");
        }
        catch (IllegalOperationException e) {
            this.saveError("Illegal operation");
        }
        catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            String sStackTrace = sw.toString();
            this.saveError(sStackTrace);
        }
    }
}
