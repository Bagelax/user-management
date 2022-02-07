import { Component, OnInit } from '@angular/core';
import {FormArray, FormControl, Validators} from "@angular/forms";
import {UserService} from "../../services/user.service";
import {ConfigService} from "../../services/config.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-edit',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent implements OnInit {
  email: FormControl;
  password: FormControl;
  name: FormControl;
  surname: FormControl;
  permissions: FormArray;
  submitted: boolean;
  id: string | null;
  public permissionsMapping: Array<any>;

  constructor(private config: ConfigService, private requestService: UserService, private route: ActivatedRoute, private router: Router) {
    this.email = new FormControl('', [Validators.required, Validators.email]);
    this.password = new FormControl('');
    this.name = new FormControl('', [Validators.required]);
    this.surname = new FormControl('', [Validators.required]);
    this.permissionsMapping = new Array<any>();
    let cbControls = new Array<FormControl>();
    for (let p of this.config.permissions) {
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
    this.id = this.route.snapshot.paramMap.get('id');
  }

  ngOnInit(): void {
    if (this.id == null)
      this.router.navigate(['']);
    else {
      this.requestService.getUser(this.id).subscribe(userResponse => {
        this.email.setValue(userResponse.email);
        this.name.setValue(userResponse.name);
        this.surname.setValue(userResponse.surname);
        for (let p of userResponse.permissions) {

          for (let i = 0; i < this.permissionsMapping.length; i++)
            if (p.id == this.permissionsMapping[i].value) {
              this.permissionsMapping[i].control.setValue(true);
              break;
            }
        }
      });
    }
  }

  editUser() {
    if (this.email.invalid || this.name.invalid || this.surname.invalid || this.permissions.invalid) {
      this.submitted = true;
      return;
    }

    let permissions = new Array<string>();
    for (let i = 0; i < this.permissions.value.length; i++)
      if (this.permissions.value[i])
        permissions.push(this.permissionsMapping[i].value);

    if (this.id != null)
      this.requestService.changeUser(this.id, this.email.value, this.password.value, this.name.value, this.surname.value, permissions).subscribe(
        () => {
          alert("Changed user.");
        },
        error => {
          alert(`Error changing user: ${error.error.error}`);
        });
      }
}
