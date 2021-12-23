import { Component, OnInit } from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {RequestService} from "../../services/request.service";
import {ConfigService} from "../../services/config.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  public email: FormControl;
  public password: FormControl;
  public submitted: boolean;

  constructor(
    private requestService: RequestService,
    private configService: ConfigService,
    private router: Router
  ) {
    this.email = new FormControl('', [Validators.required, Validators.email]);
    this.password = new FormControl('', [Validators.required]);
    this.submitted = false;
  }

  ngOnInit(): void {
  }

  login(): void {
    if (this.email.invalid || this.password.invalid) {
      this.submitted = true;
      return;
    }
    let email = this.email?.value;
    let password = this.password?.value;
    this.requestService.login(email, password).subscribe(
      loginResponse => {
        this.configService.token = loginResponse.token;
        this.router.navigate(['']);
      },
      error => {
        alert(`Error logging in: ${error.error.error}`);
      });
  }
}
