import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {ConfigService} from "./config.service";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ErrorResponse} from "../models/error-response";

@Injectable({
  providedIn: 'root'
})
export class ErrorsService {

  private readonly apiUrl: string;

  constructor(private configService: ConfigService, private httpClient: HttpClient) {
    this.apiUrl = environment.apiUrl;
  }

  public getErrors(): Observable<Array<ErrorResponse>> {
    let url = `${this.apiUrl}/errors`
    return this.httpClient.get<Array<ErrorResponse>>(url, {
      headers: {
        Authorization: `Bearer ${this.configService.token}`
      }
    });
  }

}
