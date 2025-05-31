import { Component, EventEmitter, Output } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';

@Component({
  selector: 'app-select-date',
  standalone: true,
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './select-date.component.html',
  styleUrls: ['./select-date.component.css']
})
export class SelectDateComponent {
  @Output() dateRangeSelected = new EventEmitter<{ start: Date, end: Date }>();
  form: FormGroup;

  constructor(private fb: FormBuilder) {
    const today = new Date().toLocaleDateString('fr-CA');

    this.form = this.fb.group({
      startDate: [today, Validators.required],
      endDate: [today, Validators.required]
    });
  }

  submit(): void {
    if (this.form.valid) {
      const { startDate, endDate } = this.form.value;
      this.dateRangeSelected.emit({
        start: new Date(startDate),
        end: new Date(endDate)
      });
    }
  }

}
