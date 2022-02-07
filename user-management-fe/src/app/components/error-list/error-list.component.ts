import { Component, OnInit } from '@angular/core';
import {ErrorResponse} from "../../models/error-response";
import {ErrorsService} from "../../services/errors.service";

@Component({
  selector: 'app-error-list',
  templateUrl: './error-list.component.html',
  styleUrls: ['./error-list.component.css']
})
export class ErrorListComponent implements OnInit {
  errors: Array<ErrorResponse>;

  constructor(private errorsService: ErrorsService) {
    this.errors = new Array<ErrorResponse>();
  }

  ngOnInit(): void {
    this.errorsService.getErrors().subscribe(errors => {
      this.errors = errors;
    })
  }

}
