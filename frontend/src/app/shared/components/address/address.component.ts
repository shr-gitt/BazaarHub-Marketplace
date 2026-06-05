import { CommonModule } from '@angular/common';
import {
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import {
  District,
  Municipality,
  NEPAL_PROVINCES,
} from '../../../core/models/address.model';

@Component({
  selector: 'app-address',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './address.component.html',
  styleUrls: ['./address.component.scss'],
})
export class AddressComponent implements OnInit, OnChanges {
  @Input() group!: FormGroup;

  provinces = NEPAL_PROVINCES;
  districts: District[] = [];
  municipalities: Municipality[] = [];
  addressGroup!: FormGroup;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['group'] && this.group) {
      this.initForm();
    }
  }

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.addressGroup = this.group;

    const province = this.addressGroup.get('province');
    const district = this.addressGroup.get('district');
    const municipality = this.addressGroup.get('municipality');

    this.seedFromCurrentValues();

    province?.valueChanges.subscribe((v) => {
      const p = NEPAL_PROVINCES.find((x) => x.value === v);
      this.districts = p?.districts || [];
      district?.enable({ emitEvent: false });

      const stillValid = this.districts.some(
        (d) => d.value === district?.value,
      );
      if (!stillValid) {
        district?.setValue('', { emitEvent: true });
        municipality?.setValue('', { emitEvent: false });
        municipality?.disable();
      } else {
        const d = this.districts.find((x) => x.value === district?.value);
        this.municipalities = d?.municipalities || [];
        if (this.municipalities.length) municipality?.enable();
      }
    });

    district?.valueChanges.subscribe((v) => {
      const d = this.districts.find((x) => x.value === v);
      this.municipalities = d?.municipalities || [];

      if (this.municipalities.length) {
        municipality?.enable();
      } else {
        municipality?.disable();
      }

      const stillValid = this.municipalities.some(
        (m) => m.value === municipality?.value,
      );
      if (!stillValid) municipality?.setValue('', { emitEvent: false });
    });
  }

  private seedFromCurrentValues(): void {
    const province = this.addressGroup.get('province');
    const district = this.addressGroup.get('district');
    const municipality = this.addressGroup.get('municipality');

    district?.disable({ emitEvent: false });
    municipality?.disable({ emitEvent: false });

    const pVal = province?.value;
    const dVal = district?.value;

    if (pVal) {
      const p = NEPAL_PROVINCES.find((x) => x.value === pVal);
      this.districts = p?.districts || [];
      district?.enable({ emitEvent: false });
    }

    if (dVal && this.districts.length) {
      const d = this.districts.find((x) => x.value === dVal);
      this.municipalities = d?.municipalities || [];
      if (this.municipalities.length)
        municipality?.enable({ emitEvent: false });
    }
  }
}
