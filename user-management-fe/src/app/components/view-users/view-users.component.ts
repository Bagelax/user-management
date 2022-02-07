import { Component, OnInit } from '@angular/core';
import {UserResponse} from "../../models/user-response";
import {UserService} from "../../services/user.service";
import {ConfigService} from "../../services/config.service";

@Component({
  selector: 'app-users',
  templateUrl: './view-users.component.html',
  styleUrls: ['./view-users.component.css']
})
export class ViewUsersComponent implements OnInit {
  users: Array<any>;
  editPermission: string = "CAN_UPDATE_USERS";
  deletePermission: string = "CAN_DELETE_USERS";

  constructor(private requestService: UserService, private config: ConfigService) {
    this.users = new Array<UserResponse>();
  }

  ngOnInit(): void {
    this.requestService.getUsers().subscribe(users => {
      users.forEach(user => {
        this.users.push({
          id: user.id,
          email: user.email,
          name: user.name,
          surname: user.surname,
          permissions: user.permissions.map(x => x.name).join(", ")
        });
      });
    });
  }

  hasPermission(permission: string): boolean {
    return this.config.hasPermission(permission);
  }

  delete(id: string) {
    this.requestService.deleteUser(id).subscribe(deleted => {
      this.users = this.users.filter(x => x.id != deleted.deletedId);
    })
  }

}
