import {Component, OnDestroy, OnInit} from '@angular/core';
import {MachineResponse} from "../../models/machine-response";
import {MachineService} from "../../services/machine.service";
import {ConfigService} from "../../services/config.service";
import {Form, FormArray, FormControl} from "@angular/forms";

@Component({
  selector: 'app-view-machines',
  templateUrl: './view-machines.component.html',
  styleUrls: ['./view-machines.component.css']
})
export class ViewMachinesComponent implements OnInit, OnDestroy {
  machines: Array<MachineResponse>;
  startPermission: string = "CAN_START_MACHINES";
  stopPermission: string = "CAN_STOP_MACHINES";
  restartPermission: string = "CAN_RESTART_MACHINES";
  searchPermission: string = "CAN_SEARCH_MACHINES";
  deletePermission: string = "CAN_DESTROY_MACHINES";
  statuses: Array<any>;
  scheduleStart: boolean = false;
  scheduleStop: boolean = false;
  scheduleRestart: boolean = false;
  intervalId: number;
  filterStatuses: FormArray;
  filterName: FormControl;
  filterDateFrom: FormControl;
  filterDateTo: FormControl;
  scheduleStartTime: FormControl;
  scheduleStopTime: FormControl;
  scheduleRestartTime: FormControl;
  filterStatusesValue: Array<string> | null;
  filterNameValue: string | null;
  filterDateFromValue: Date | null;
  filterDateToValue: Date | null;
  filterActive: boolean;

  constructor(private machineService: MachineService, private config: ConfigService) {
    this.machines = new Array<MachineResponse>();
    this.intervalId = 0;
    this.statuses = new Array<any>();
    this.filterName = new FormControl('');
    this.filterDateFrom = new FormControl('');
    this.filterDateTo = new FormControl('');
    this.scheduleStartTime = new FormControl('');
    this.scheduleStopTime = new FormControl('');
    this.scheduleRestartTime = new FormControl('');
    this.filterActive = false;
    this.filterStatusesValue = null;
    this.filterNameValue = null;
    this.filterDateFromValue = null;
    this.filterDateToValue = null;

    let machineStatuses = ["RUNNING", "STOPPED", "STARTING", "STOPPING"];
    let cbControls = new Array<FormControl>();

    for (let s of machineStatuses) {
      let tmp = new FormControl(true);
      this.statuses.push({
        name: s,
        control: tmp
      });
      cbControls.push(tmp);
    }
    this.filterStatuses = new FormArray(cbControls);
  }

  refreshMachines() {
    if (this.filterActive) {
      this.machineService.filterMachines(this.filterNameValue,
                                         this.filterStatusesValue,
                                         this.filterDateFromValue,
                                         this.filterDateToValue).subscribe(machines => {
        this.machines = machines;
      });
    }
    else {
      this.machineService.getMachines().subscribe(machines => {
        this.machines = machines;
      });
    }
  }

  ngOnInit(): void {
    this.refreshMachines();
    this.intervalId = setInterval(() => {
      this.refreshMachines();
    }, 2000);
  }

  ngOnDestroy() {
    clearInterval(this.intervalId);
  }

  hasPermission(permission: string): boolean {
    return this.config.hasPermission(permission);
  }

  start(id: string) {
    if (this.scheduleStart) {
      let executeAt = new Date(this.scheduleStartTime.value);
      this.machineService.scheduleMachineStart(id, executeAt.getTime()).subscribe(() => {
        alert("Scheduled start");
      });
    }
    else {
      this.machineService.startMachine(id).subscribe(() => {
        alert("Starting")
      }, error => {
        alert(`Error starting: ${error.error.error}`)
      });
    }
  }

  stop(id: string) {
    if (this.scheduleStop) {
      let executeAt = new Date(this.scheduleStopTime.value);
      console.log(this.scheduleStopTime.value);
      console.log(executeAt);
      this.machineService.scheduleMachineStop(id, executeAt.getTime()).subscribe(() => {
        alert("Scheduled stop");
      });
    }
    else {
      this.machineService.stopMachine(id).subscribe(() => {
        alert("Stopping")
      }, error => {
        alert(`Error stopping: ${error.error.error}`)
      });
    }
  }

  restart(id: string) {
    if (this.scheduleRestart) {
      let executeAt = new Date(this.scheduleRestartTime.value);
      this.machineService.scheduleMachineRestart(id, executeAt.getTime()).subscribe(() => {
        alert("Scheduled restart");
      });
    }
    else {
      this.machineService.restartMachine(id).subscribe(() => {
        alert("Restarting")
      }, error => {
        alert(`Error restarting: ${error.error.error}`)
      });
    }
  }

  delete(id: string) {
    this.machineService.deleteMachine(id).subscribe(deleted => {
      this.machines = this.machines.filter(x => x.id != deleted.deletedId);
    });
  }

  filterMachines() {
    this.filterActive = true;
    if (this.filterName?.value != "") this.filterNameValue = this.filterName.value;
    if (this.filterDateFrom?.value != "") this.filterDateFromValue = new Date(this.filterDateFrom.value);
    if (this.filterDateTo?.value != "") this.filterDateToValue = new Date(this.filterDateTo.value);
    this.filterStatusesValue = new Array<string>();
    for (let i = 0; i < this.filterStatuses.value.length; i++)
      if (this.filterStatuses.value[i])
        this.filterStatusesValue.push(this.statuses[i].name);

    console.log(this.filterNameValue);
    console.log(this.filterDateFromValue?.getTime());
    console.log(this.filterDateToValue?.getTime());
    console.log(this.filterStatusesValue);
    this.refreshMachines();
  }
}
