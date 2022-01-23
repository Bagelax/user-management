import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {ViewUsersComponent} from "./components/view-users/view-users.component";
import {EditUserComponent} from "./components/edit-user/edit-user.component";
import {NewUserComponent} from "./components/new-user/new-user.component";
import {IndexComponent} from "./components/index/index.component";
import {TokenGuard} from "./gurads/token.guard";
import {PermissionGuard} from "./gurads/permission.guard";
import {ViewMachinesComponent} from "./components/view-machines/view-machines.component";
import {NewMachineComponent} from "./components/new-machine/new-machine.component";
import {ErrorListComponent} from "./components/error-list/error-list.component";

const routes: Routes = [
  {
    path: '',
    component: IndexComponent,
    canActivate: [TokenGuard]
  },
  {
    path: "login",
    component: LoginComponent
  },
  {
    path: "users",
    component: ViewUsersComponent,
    canActivate: [TokenGuard, PermissionGuard],
    data: {
      requiredPermission: "CAN_READ_USERS"
    }
  },
  {
    path: "users/:id/edit",
    component: EditUserComponent,
    canActivate: [TokenGuard, PermissionGuard],
    data: {
      requiredPermission: "CAN_UPDATE_USERS"
    }
  },
  {
    path: "users/new",
    component: NewUserComponent,
    canActivate: [TokenGuard, PermissionGuard],
    data: {
      requiredPermission: "CAN_CREATE_USERS"
    }
  },
  {
    path: "machines",
    component: ViewMachinesComponent,
    canActivate: [TokenGuard]
  },
  {
    path: "machines/new",
    component: NewMachineComponent,
    canActivate: [TokenGuard, PermissionGuard],
    data: {
      requiredPermission: "CAN_CREATE_MACHINES"
    }
  },
  {
    path: "errors",
    component: ErrorListComponent,
    canActivate: [TokenGuard]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
