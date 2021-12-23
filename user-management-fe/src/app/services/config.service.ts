import { Injectable } from '@angular/core';
import {PermissionResponse} from "../models/permission-response";

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private _permissions: Array<PermissionResponse>;
  private _allPermissions: Array<PermissionResponse>;
  private _token: string;
  private _permissionsFetched: boolean;

  constructor() {
    this._token = localStorage.getItem('token') || "";
    this._permissions = new Array<PermissionResponse>();
    this._permissionsFetched = false;
    this._allPermissions = new Array<PermissionResponse>();
  }

  get token(): string {
    return this._token;
  }

  set token(value: string) {
    localStorage.setItem('token', value);
    this._token = value;
  }

  public clearToken() {
    localStorage.removeItem('token');
  }

  get permissions(): Array<PermissionResponse> {
    return this._permissions;
  }

  get permissionsFetched(): boolean {
    return this._permissionsFetched;
  }

  set permissionsFetched(value: boolean) {
    this._permissionsFetched = value;
  }

  set permissions(value: Array<PermissionResponse>) {
    this._permissions = value;
  }

  hasPermission(requiredPermission: string) {
    return this.permissions.map(x => x.name).indexOf(requiredPermission) > -1;
  }

  get allPermissions(): Array<PermissionResponse> {
    return this._allPermissions;
  }

  set allPermissions(value: Array<PermissionResponse>) {
    this._allPermissions = value;
  }
}
