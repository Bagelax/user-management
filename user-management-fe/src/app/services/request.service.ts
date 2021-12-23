import { Injectable } from '@angular/core';
import {ConfigService} from "./config.service";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {LoginResponse} from "../models/login-response";
import {Observable} from "rxjs";
import {PermissionResponse} from "../models/permission-response";
import {UserResponse} from "../models/user-response";
import {DeletedResponse} from "../models/deleted-response";

@Injectable({
  providedIn: 'root'
})
export class RequestService {

  private readonly apiUrl: string;

  constructor(private configService: ConfigService, private httpClient: HttpClient) {
    this.apiUrl = environment.apiUrl;
  }

  public login(email: string, password: string): Observable<LoginResponse> {
    let url = `${this.apiUrl}/login`;
    return this.httpClient.post<LoginResponse>(url, {
      email: email,
      password: password
    });
  }

  public getPermissions(): Observable<Array<PermissionResponse>> {
    let url = `${this.apiUrl}/permissions/granted`
    return this.httpClient.get<Array<PermissionResponse>>(url, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  public getUsers(): Observable<Array<UserResponse>> {
    let url = `${this.apiUrl}/users`
    return this.httpClient.get<Array<UserResponse>>(url, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  deleteUser(id: string): Observable<DeletedResponse> {
    let url = `${this.apiUrl}/users/${id}`
    return this.httpClient.delete<DeletedResponse>(url, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  getUser(id: String): Observable<UserResponse> {
    let url = `${this.apiUrl}/users/${id}`
    return this.httpClient.get<UserResponse>(url, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  createUser(email: string, password: string, name: string, surname: string, permissions: Array<string>) {
    let url = `${this.apiUrl}/users`
    return this.httpClient.post<UserResponse>(url, {
        email: email,
        password: password,
        name: name,
        surname: surname,
        permissions: permissions
      }, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  changeUser(id: string, email: string, password: string, name: string, surname: string, permissions: Array<string>) {
    let url = `${this.apiUrl}/users/${id}`
    return this.httpClient.put<UserResponse>(url, {
      email: email,
      password: password,
      name: name,
      surname: surname,
      permissions: permissions
    }, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  getAllPermissions(): Observable<Array<PermissionResponse>> {
    let url = `${this.apiUrl}/permissions`
    return this.httpClient.get<Array<PermissionResponse>>(url, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }
}
