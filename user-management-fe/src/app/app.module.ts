import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from "./components/login/login.component";
import { ViewUsersComponent } from './components/view-users/view-users.component';
import { EditUserComponent } from './components/edit-user/edit-user.component';
import { NewUserComponent } from './components/new-user/new-user.component';
import { IndexComponent } from './components/index/index.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import { ViewMachinesComponent } from './components/view-machines/view-machines.component';
import { NewMachineComponent } from './components/new-machine/new-machine.component';
import { ErrorListComponent } from './components/error-list/error-list.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    ViewUsersComponent,
    EditUserComponent,
    NewUserComponent,
    IndexComponent,
    ViewMachinesComponent,
    NewMachineComponent,
    ErrorListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
