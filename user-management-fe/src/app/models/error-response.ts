import {MachineResponse} from "./machine-response";

export interface ErrorResponse {
  id: string,
  error: string,
  time: number,
  machine: MachineResponse
}
