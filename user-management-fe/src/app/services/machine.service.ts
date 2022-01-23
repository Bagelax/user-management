import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {MachineResponse} from "../models/machine-response";
import {DeletedResponse} from "../models/deleted-response";
import {environment} from "../../environments/environment";
import {ConfigService} from "./config.service";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class MachineService {

  private readonly apiUrl: string;

  constructor(private configService: ConfigService, private httpClient: HttpClient) {
    this.apiUrl = environment.apiUrl;
  }

  public getMachines(): Observable<Array<MachineResponse>> {
    let url = `${this.apiUrl}/machines`
    return this.httpClient.get<Array<MachineResponse>>(url, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  deleteMachine(id: string): Observable<DeletedResponse> {
    let url = `${this.apiUrl}/machines/${id}`
    return this.httpClient.delete<DeletedResponse>(url, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  getMachine(id: String): Observable<MachineResponse> {
    let url = `${this.apiUrl}/machines/${id}`
    return this.httpClient.get<MachineResponse>(url, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  createMachine(name: string) {
    let url = `${this.apiUrl}/machines`
    return this.httpClient.post<MachineResponse>(url, {
      name: name
    }, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  filterMachines(name: string | null, status: Array<string> | null, dateFrom: Date | null, dateTo: Date | null):
    Observable<Array<MachineResponse>> {
    let url = `${this.apiUrl}/machines/filter?`;
    let statuses = status?.reduce((s1, s2, _i) => {
      return s1 + "&status=" + s2;
    });
    url += "status=" + statuses;
    if (name != null) url += "&name=" + name;
    if (dateFrom != null) url += "&dateFrom=" + dateFrom.getTime();
    if (dateTo != null) url += "&dateTo=" + dateTo.getTime();
    return this.httpClient.get<Array<MachineResponse>>(url, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  startMachine(id: string): Observable<any> {
    let url = `${this.apiUrl}/machines/${id}/start`
    return this.httpClient.post<MachineResponse>(url,{}, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  stopMachine(id: string): Observable<any> {
    let url = `${this.apiUrl}/machines/${id}/stop`
    return this.httpClient.post<MachineResponse>(url,{}, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  restartMachine(id: string): Observable<any> {
    let url = `${this.apiUrl}/machines/${id}/restart`
    return this.httpClient.post<MachineResponse>(url,{}, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  scheduleMachineStart(id: string, executeAt: number): Observable<any> {
    let url = `${this.apiUrl}/machines/${id}/schedule/start`
    return this.httpClient.post<MachineResponse>(url,{
      executeAt: executeAt
    }, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  scheduleMachineStop(id: string, executeAt: number): Observable<any> {
    let url = `${this.apiUrl}/machines/${id}/schedule/stop`
    return this.httpClient.post<MachineResponse>(url,{
      executeAt: executeAt
    }, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

  scheduleMachineRestart(id: string, executeAt: number): Observable<any> {
    let url = `${this.apiUrl}/machines/${id}/schedule/restart`
    return this.httpClient.post<MachineResponse>(url,{
      executeAt: executeAt
    }, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }
}
