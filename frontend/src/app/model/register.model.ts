export interface CreateUserRequest{
  firstname: string,
  lastname: string,
  email: string,
  password: string,
  role: string,
  isElectricOrHybrid: boolean,
}

export interface UserForSecretary {
  id: string,
  firstname: string,
  lastname: string,
  email: string,
  role: string,
  isElectricOrHybrid: boolean,
}

export interface UserUpdateRequest {
  firstname: string,
  lastname: string,
  role: string,
  isHybridOrElectric: string,
}

