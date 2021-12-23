import { Component, OnInit } from '@angular/core';
import {FormArray, FormControl, Validators} from "@angular/forms";
import {RequestService} from "../../services/request.service";
import {ConfigService} from "../../services/config.service";

@Component({
  selector: 'app-new',
  templateUrl: './new-user.component.html',
  styleUrls: ['./new-user.component.css']
})
export class NewUserComponent implements OnInit {
  email: FormControl;
  password: FormControl;
  name: FormControl;
  surname: FormControl;
  permissions: FormArray;
  submitted: boolean;
  public permissionsMapping: Array<any>;

  constructor(private config: ConfigService, private requestService: RequestService) {
    this.email = new FormControl('', [Validators.required, Validators.email]);
    this.password = new FormControl('', [Validators.required]);
    this.name = new FormControl('', [Validators.required]);
    this.surname = new FormControl('', [Validators.required]);
    this.permissionsMapping = new Array<any>();
    let cbControls = new Array<FormControl>();
    for (let p of this.config.allPermissions) {
      let tmp = new FormControl(false);
      this.permissionsMapping.push({
        name: p.name,
        value: p.id,
        control: tmp
      });
      cbControls.push(tmp);
    }
    this.permissions = new FormArray(cbControls);
    this.submitted = false;
  }

  ngOnInit(): void {
  }

  addUser() {
    if (this.email.invalid || this.password.invalid || this.name.invalid || this.surname.invalid || this.permissions.invalid) {
      this.submitted = true;
      return;
    }

    let permissions = new Array<string>();
    for (let i = 0; i < this.permissions.value.length; i++)
      if (this.permissions.value[i])
        permissions.push(this.permissionsMapping[i].value);

    this.requestService.createUser(this.email.value, this.password.value, this.name.value, this.surname.value, permissions).subscribe(
      () => {
        alert("Added user.");
      },
      error => {
        alert(`Error creating user: ${error.error.error}`);
      });
  }
}
