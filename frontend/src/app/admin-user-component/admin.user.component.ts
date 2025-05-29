import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatTableModule} from '@angular/material/table';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatSelectModule} from '@angular/material/select';
import {UserService} from '../service/user.service';
import {UserForSecretary, UserUpdateRequest} from '../model/register.model';

@Component({
  selector: 'app-user-crud-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatSelectModule,
    FormsModule,
  ],
  templateUrl: './admin.user.component.html',
  styleUrls: ['./admin.user.component.css'],
})
export class AdminUserComponent implements OnInit {
  users: UserForSecretary[] = [];
  displayedColumns: string[] = ['firstname', 'lastname', 'email', 'role', 'isElectricOrHybrid', 'actions'];
  userForm: FormGroup;
  editingUserId: string | null = null;

  constructor(private registerService: UserService, private fb: FormBuilder) {
    this.userForm = this.fb.group({
      firstname: [''],
      lastname: [''],
      email: [''],
      password: [''],
      role: ['user'],
      isElectricOrHybrid: [false]
    });
  }

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.registerService.getAll().subscribe(users => this.users = users);
  }

  createUser() {
    const {firstname, lastname, email, password, role, isElectricOrHybrid} = this.userForm.value;
    this.registerService.createUser(firstname, lastname, email, password, role, isElectricOrHybrid)
      .subscribe(() => {
        this.userForm.reset({role: 'user', isElectricOrHybrid: false});
        this.loadUsers();
      });
  }

  enableEdit(user: UserForSecretary) {
    this.editingUserId = user.id;
  }

  updateUser(user: UserForSecretary) {
    const updated: Partial<UserUpdateRequest> = {
      firstname: user.firstname,
      lastname: user.lastname,
      role: user.role,
      isHybridOrElectric: user.isElectricOrHybrid.toString(),
    };
    this.registerService.update(user.id, updated).subscribe(() => {
      this.editingUserId = null;
      this.loadUsers();
    });
  }

  deleteUser(id: string) {
    this.registerService.delete(id).subscribe(() => this.loadUsers());
  }
}
