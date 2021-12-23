import {Component, OnInit} from '@angular/core';
import {ConfigService} from "./services/config.service";
import {RequestService} from "./services/request.service";
import {NavigationEnd, Router} from "@angular/router";
import {filter} from "rxjs/operators";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'user-management-fe';

  constructor(private config: ConfigService, private requestService: RequestService, private router: Router) {
    this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe(() => {
      this.fetchPermissions();
    });
  }

  public logout() {
    this.config.clearToken();
    window.location.reload();
  }

  public loggedIn() {
    return this.config.token.length > 0;
  }

  private fetchPermissions(): void {
    if (this.loggedIn() && !this.config.permissionsFetched) {
      this.config.permissionsFetched = true;
      this.requestService.getPermissions().subscribe(permissionsResponse => {
        this.config.permissions = permissionsResponse;
      }, error => {
        if (error.status == 403) {
          this.config.clearToken();
          this.router.navigate(['']);
        }
        alert(`Error getting permissions: ${error.error.error}`);
      });
      this.requestService.getAllPermissions().subscribe(permissionsResponse => {
        this.config.allPermissions = permissionsResponse;
      }, error => {
        if (error.status == 403) {
          this.config.clearToken();
        }
        alert(`Error getting permissions: ${error.error.error}`);
      });
    }
  }

  ngOnInit(): void {
    this.fetchPermissions();
  }

  hasPermission(permission: string): boolean {
    return this.config.hasPermission(permission);
  }
}
