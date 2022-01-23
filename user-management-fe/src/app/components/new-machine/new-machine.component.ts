import { Component, OnInit } from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {MachineService} from "../../services/machine.service";

@Component({
  selector: 'app-new-machine',
  templateUrl: './new-machine.component.html',
  styleUrls: ['./new-machine.component.css']
})
export class NewMachineComponent implements OnInit {
  name: FormControl;
  submitted: boolean;

  constructor(private machineService: MachineService) {
    this.name = new FormControl('', [Validators.required]);
    this.submitted = false;
  }

  ngOnInit(): void {
  }

  addMachine() {
    if (this.name.invalid) {
      this.submitted = true;
      return;
    }

    this.machineService.createMachine(this.name.value).subscribe(() => {
      alert(`Added machine ${this.name.value}`);
    }, error => {
      alert(`Error creating machine: ${error.error.error}`);
    })
  }

}
