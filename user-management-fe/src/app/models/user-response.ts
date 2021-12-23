import {PermissionResponse} from "./permission-response";

export interface UserResponse {
  id: string,
  email: string,
  name: string,
  surname: string,
  permissions: Array<PermissionResponse>
}
